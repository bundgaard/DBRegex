/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbregex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 *
 * @author davidb
 */
public class DBRegex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {

        
        JFrame frame = new JFrame("Regular expressions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        JTextArea inputData = new JTextArea();
        JTextField regularExpressionField = new JTextField();
        inputPanel.add(new JLabel("Regular expression:"));
        inputPanel.add(regularExpressionField);
        inputPanel.add(new JLabel("Test data:"));
        JScrollPane inputDataScrollPane = new JScrollPane();
        JScrollPane outputDataScrollPanel = new JScrollPane();
        JScrollPane eastScrollPane = new JScrollPane();
        javax.swing.JSplitPane splitCenter = new JSplitPane();
        
        JPanel centerPanel = new JPanel();
        JTextArea outputData = new JTextArea();
        JList listOfLinesToSave = new JList();
        DefaultListModel defListModel = new DefaultListModel();
        
        listOfLinesToSave.setModel(defListModel);
        eastScrollPane.getViewport().setView(listOfLinesToSave);
        inputDataScrollPane.getViewport().setView(inputData);
        outputData.setSize(640, 480);

        centerPanel.setLayout(new GridLayout(2, 1));
        outputDataScrollPanel.getViewport().setView(outputData);
        centerPanel.add(inputDataScrollPane);
        centerPanel.add(outputDataScrollPanel);
        
        splitCenter.setRightComponent(eastScrollPane);
        splitCenter.setLeftComponent(centerPanel);
        splitCenter.setResizeWeight(0.90);
        
        frame.add(inputPanel, BorderLayout.NORTH);
        //frame.add(centerPanel, BorderLayout.CENTER);
        //frame.add(eastScrollPane, BorderLayout.EAST);
        frame.add(splitCenter,BorderLayout.CENTER);
        JButton testButton = new JButton("Test");
        Class regex = Class.forName("java.util.regex.Pattern");
        System.out.println("regex.getFields(): " + regex.getFields().length);
        javax.swing.JPopupMenu popupMenu = new JPopupMenu("Popup menu");
        final JCheckBoxMenuItem item[] = new JCheckBoxMenuItem[regex.getFields().length];
        //for (int i = 0; i < regex.getFields().length; i++) {
        
        JPopupMenu popupSaveOne = new JPopupMenu("SaveToList");
        JMenuItem saveOne = new JMenuItem("Save To List");
        saveOne.setActionCommand("savetolist");
        saveOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                regularExpressionField.selectAll(); // void function
                String text = regularExpressionField.getSelectedText();
                defListModel.add(0, text);
            }
        });
        
        popupSaveOne.add(saveOne);
        Field f[] = regex.getFields();
        for (int i = 0; i < regex.getFields().length; i++) {

            item[i] = new JCheckBoxMenuItem(f[i].getName());

            item[i].setActionCommand(f[i].getName());
            item[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (((JCheckBoxMenuItem) evt.getSource()).isSelected() != true) {
                        ((JCheckBoxMenuItem) evt.getSource()).setSelected(false);
                    } else {
                        ((JCheckBoxMenuItem) evt.getSource()).setSelected(true);
                    }
                    System.out.println(evt.getActionCommand());
                }
            });
            popupMenu.add(item[i]);
        }

        regularExpressionField.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (e.isPopupTrigger()) {
                    System.out.println("Hello popupTrigger()");
                    if (e.isShiftDown()) {
                        popupSaveOne.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }

                }
                //super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    System.out.println("Relased popupTrigger()");
                    if (e.isShiftDown() && e.isPopupTrigger()) {
                        popupSaveOne.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        });
        for (Field obj : regex.getFields()) {
            System.out.println(obj.getName());
        }

        System.out.println("MULTILINE: " + Pattern.MULTILINE);
        System.out.println("DOTALL: " + Pattern.DOTALL);
        System.out.println("MULTILINE | DOTALL: " + (Pattern.MULTILINE | Pattern.DOTALL));

        testButton.addActionListener((ActionEvent evt) -> {
            if (regularExpressionField.getText() != null || regularExpressionField.getText().equalsIgnoreCase("")) {
                outputData.setText("");
                int flag = 0;
                for (int i = 0; i < item.length; i++) {
                    if (item[i].isSelected()) {
                        try {
                            flag |= regex.getFields()[i].getInt(null);
                        } catch (IllegalAccessException e) {
                            System.err.println(e.getMessage());
                        }

                    }
                }
                System.out.println("flag value: " + flag);
                Pattern pattern = null;
                if (flag < 0) {
                    pattern = Pattern.compile(regularExpressionField.getText().replace('\n', ' ').trim());
                } else {
                    pattern = Pattern.compile(regularExpressionField.getText().replace('\n', ' ').trim(), flag);
                }
                Matcher matcher = pattern.matcher(inputData.getText());
                if (matcher.find()) {
                    outputData.setText(matcher.group());
                }

            }

        });
        frame.add(testButton, BorderLayout.SOUTH);

        final Runnable r = () -> {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenSize = tk.getScreenSize();
            frame.setLocation(
                    /* X */
                    (screenSize.width - frame.getWidth()) / 2,
                    /* Y */
                    (screenSize.height - frame.getHeight()) / 2
            );
            frame.setVisible(true);
        };

        SwingUtilities.invokeLater(r);
    }
}
