package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GettingBookFrame extends AbstractFrame {
    private Library library;
    JPanel infoPanel;
    private Font font = new Font("Verdana", Font.PLAIN, 12);

    public GettingBookFrame(Library library) {
        super("Возврат книги");
        this.library = library;

        setLocation(270, 200);
        setPreferredSize(new Dimension(900, 300));

        infoPanel = new JPanel(new GridBagLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());

        addItem(infoPanel, createField("Название книги: ", "", true),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("Имя читателя: ", "", true),
                2, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createComboBox("Состояние, в котором принимается книга: ", Record.State.GOOD, true),
                2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        JPanel dataPanel = new JPanel(new GridBagLayout());
        GregorianCalendar calendar = new GregorianCalendar();
        addItem(dataPanel, new JLabel("Дата возврата книги:"),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("День: ", calendar.get(Calendar.DAY_OF_MONTH), 0, 31, true),
                3, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("Месяц: ", calendar.get(Calendar.MONTH), 0, 12, true),
                4, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("Год: ", calendar.get(Calendar.YEAR), 1950, 2050, true),
                5, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        addItem(infoPanel, dataPanel,
                2, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);


        JPanel buttons = createButtons("Принять", "Отмена");
        addItem(mainPanel, infoPanel, 2, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, buttons, 2, 3, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public void button1ActionListener(JButton button1, JButton button2) {
        boolean hasRecord = false;
        boolean result = false;
        StringBuilder message = new StringBuilder(validationValues());
        if (message.toString().isEmpty()) {
            String nameBook = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
            String customer = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();
            Record.State state = (Record.State) ((JComboBox) ((JPanel) infoPanel.getComponents()[2]).getComponents()[1]).getSelectedItem();
            GregorianCalendar date = getDataFromSpinners();

            Book needBook = library.getBookFromName(nameBook);
            if (needBook != null) {
                result = library.getBookFromCustomer(needBook, customer, state, date);
            } else {
               message.append("Книга с таким названием не найдена, попробуйте еще раз ");
            }
            if (!result) {
                message.append("Указанный читатель не брал эту книгу");
            } else {
                setVisible(false);
                dispose();
            }
        }
        if (!result)
            JOptionPane.showMessageDialog(null, message.toString());
    }

    public void button2ActionListener(JButton button1, JButton button2) {
        setVisible(false);
        dispose();

    }

    private String validationValues() {
        StringBuilder message = new StringBuilder();

        String nameBook = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
        String customer = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();

        if (nameBook.isEmpty()) {
            message.append("Введите название книги\n");
        }
        if (customer.isEmpty() || !customer.matches("^[ а-яА-Я]+$")) {
            message.append("Некорректное имя читателя\n");
        }

        return message.toString();
    }

    private GregorianCalendar getDataFromSpinners() {
        Component[] comps = infoPanel.getComponents();

        int day = (int) ((JSpinner) ((JPanel) ((JPanel) comps[3]).getComponents()[1]).getComponents()[1]).getValue();
        int month = (int) ((JSpinner) ((JPanel) ((JPanel) comps[3]).getComponents()[2]).getComponents()[1]).getValue();
        int year = (int) ((JSpinner) ((JPanel) ((JPanel) comps[3]).getComponents()[3]).getComponents()[1]).getValue();
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar;
    }
}
