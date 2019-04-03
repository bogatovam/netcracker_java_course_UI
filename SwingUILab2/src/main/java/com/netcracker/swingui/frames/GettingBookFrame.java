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
        addItem(dataPanel,createSpinner("Месяц: ", calendar.get(Calendar.MONTH), 0, 12, true),
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
        String nameBook = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
        String customer = (String) ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();
        Record.State state = (Record.State) ((JComboBox) ((JPanel) infoPanel.getComponents()[2]).getComponents()[1]).getSelectedItem();
        GregorianCalendar date = getDataFromSpinners();

        Book needBook = library.getBookFromName(nameBook);
        if (needBook != null) {
            for (Record record : library.getBook(needBook)) {
                if (record.getCustomer().getName().equals(customer) &&
                        record.getBeginDate().compareTo(date) > 0 &&
                        record.getStatus() == Record.Status.GIVE &&
                        record.getEndDate() == null) {
                    record.setStatus(Record.Status.GET);
                    record.setEndDate(date);
                    hasRecord = true;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Книга с таким названием не найдена, попробуйте еще раз ");
        }
        if (!hasRecord) {
            JOptionPane.showMessageDialog(null,
                    "Указанный читатель не брал эту книгу");
        } else {
            setVisible(false);
            dispose();
        }
    }

    public void button2ActionListener(JButton button1, JButton button2) {
        setVisible(false);
        dispose();

    }

    private String validationValues() {
        StringBuilder message = new StringBuilder();

        String nameBook = ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
        String customer = ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();
        String address = ((JTextField) ((JPanel) infoPanel.getComponents()[4]).getComponents()[1]).getText();
        String email = ((JTextField) ((JPanel) infoPanel.getComponents()[5]).getComponents()[1]).getText();
        String phone = ((JTextField) ((JPanel) infoPanel.getComponents()[3]).getComponents()[1]).getText();

        if (nameBook.isEmpty()) {
            message.append("Введите название книги\n");
        }
        if (customer.isEmpty() || !customer.matches("^[a-zA-Z]+$")) {
            message.append("Некорректное имя читателя\n");
        }
        if (address.isEmpty()) {
            message.append("Введите адресс читателя\n");
        }
        if (email.isEmpty()) {
            message.append("Введите email читателя\n");
        }
        if (phone.isEmpty()) {
            message.append("Введите телефон читателя\n");
        }

        return message.toString();
    }

    private GregorianCalendar getDataFromSpinners() {
        Component[] comps = infoPanel.getComponents();

        int day = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[1]).getComponents()[1]).getValue();
        int month = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[2]).getComponents()[1]).getValue();
        int year = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[3]).getComponents()[1]).getValue();
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar;
    }
}
