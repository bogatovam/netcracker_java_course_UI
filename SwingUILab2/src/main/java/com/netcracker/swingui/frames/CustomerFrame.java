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

public class CustomerFrame extends AbstractFrame {

    private Customer customer;
    private Library library;

    public CustomerFrame(Library library, String personeName) {
        super("Читатель " + personeName);

        this.customer = library.findCustomerByName(personeName);
        this.library = library;

        setLocation(270, 200);
        setPreferredSize(new Dimension(800, 400));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        addItem(infoPanel, createField("Имя читателя: ", customer.getName(), false),
                2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createCheckBox("Пол: ", customer.getGender(), false),
                2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("Адрес: ", customer.getAddress(), false),
                2, 4, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("E-mail: ", customer.getEmail(), false),
                2, 5, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createPhoneField("Телефон: ", customer.getPhone(), false),
                2, 6, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, infoPanel, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, createTable(), 1, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
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
                        return ((Book) current[rowIndex]).getName();
                    case 1:
                        return ((Book) current[rowIndex]).getAuthors().getName();
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
        JScrollPane panel = new JScrollPane(table);
        return panel;
    }

}
