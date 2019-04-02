package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Customer;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CustomerFrame extends JFrame {
    private JTextField nameCustomer;
    private JCheckBox[] gender;
    private JTextField email;
    private JTextField phone;
    private JTextField address;
    private JTable books;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;

    private Customer customer;
    private Library library;
    private Font font = new Font("Verdana", Font.PLAIN, 12);

    public CustomerFrame(Library library, String personeName) {
        super("Читатель " + personeName);

        this.customer = library.findCustomerByName(personeName);
        this.library = library;

        setLocation(270, 20);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 700));

        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        JPanel mainPanel = new JPanel(new FlowLayout());
        infoPanel.add(createField("Название книги: ", customer.getName()));
        infoPanel.add(createCheckBox("Автор(ы): ", customer.getGender()));
        infoPanel.add(createField("Автор(ы): ", customer.getAddress()));
        infoPanel.add(createField("Автор(ы): ", customer.getEmail()));
        infoPanel.add(createField("Автор(ы): ", customer.getPhone()));

        mainPanel.add(infoPanel);
        mainPanel.setPreferredSize(new Dimension(500, 500));
        mainPanel.add(createTable());
        add(mainPanel);
        pack();
        setVisible(true);
    }

    public JPanel createField(String sourceLabel, String sourceText) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 100, 0));
        JTextField field = new JTextField(sourceText);
        JLabel label = new JLabel(sourceLabel);
        field.setEditable(false);
        field.setFont(font);
        label.setFont(font);

        panel.add(label);
        panel.add(field);
        return panel;
    }

    public JPanel createCheckBox(String sourceLabel, char sourceValue) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 100, 0));
        JCheckBox box1 = new JCheckBox("Женщина");
        JCheckBox box2 = new JCheckBox("Мужчина");
        box1.setFont(font);
        box2.setFont(font);
        ButtonGroup group = new ButtonGroup();
        group.add(box1);
        group.add(box2);
        if(sourceValue == 'F') box1.setSelected(true);
        else box2.setSelected(true);

        JLabel label = new JLabel(sourceLabel);
        label.setFont(font);
        box1.setEnabled(false);
        box2.setEnabled(false);
        panel.add(label);
        panel.add(box1);
        panel.add(box2);
        return panel;
    }

    public JScrollPane createTable() {
        JTable table = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return library.findAllCustomersBooks(customer).length;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object[] current = library.findAllCustomersBooks(customer);
                // if(current!=null)
                switch (columnIndex) {
                    case 0:
                        return ((Book)current[rowIndex]).getName();
                    case 1:
                        return ((Book)current[rowIndex]).getAuthorNames();
                }
                return null;
            }

            @Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Назание книги";
                    case 1:
                        return "Автор(ы)";
                }
                return "";
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                }
                return Object.class;
            }
        });
        table.setFont(font);
        table.setRowHeight(25);
        JScrollPane panel = new JScrollPane(table);
        panel.setPreferredSize(new Dimension(750, 400));
        return panel;
    }
}
