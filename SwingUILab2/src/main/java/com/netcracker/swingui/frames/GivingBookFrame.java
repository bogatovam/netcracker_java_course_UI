package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Customer;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GivingBookFrame extends AbstractFrame {
    private Library library;
    private JPanel infoPanel;

    public GivingBookFrame(Library library) {
        super("Выдача книги");
        this.library = library;

        setLocation(270, 200);
        setPreferredSize(new Dimension(900, 400));

        infoPanel = new JPanel(new GridBagLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());

        addItem(infoPanel, createField("Название книги: ", "", true),
                2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("Имя читателя: ", "", true),
                2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createCheckBox("Пол: ", 'F', true),
                2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("Адрес: ", "", true),
                2, 4, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createField("E-mail: ", "", true),
                2, 5, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createPhoneField("Телефон: ", ""),
                2, 6, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createComboBox("Состояние, в котором выдается книга: ", Record.State.GOOD, true),
                2, 8, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);

        JPanel dataPanel = new JPanel(new GridBagLayout());
        GregorianCalendar calendar = new GregorianCalendar();
        addItem(dataPanel, new JLabel("Дата получения книги:"),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("День: ", calendar.get(Calendar.DAY_OF_MONTH), 0, 31, true),
                3, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("Месяц: ", calendar.get(Calendar.MONTH), 0, 12, true),
                4, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(dataPanel, createSpinner("Год: ", calendar.get(Calendar.YEAR), 1950, 2050, true),
                5, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        addItem(infoPanel, dataPanel,
                2, 9, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);


        JPanel buttons = createButtons("Выдать", "Отмена");
        addItem(mainPanel, infoPanel, 2, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, buttons, 2, 3, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private String validationValues() {
        StringBuilder message = new StringBuilder();

        String nameBook = ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
        String customer = ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();
        String address = ((JTextField) ((JPanel) infoPanel.getComponents()[4]).getComponents()[1]).getText();
        String email = ((JTextField) ((JPanel) infoPanel.getComponents()[3]).getComponents()[1]).getText();
        String phone = ((JFormattedTextField) ((JPanel) infoPanel.getComponents()[5]).getComponents()[1]).getText();

        if (nameBook.isEmpty()) {
            message.append("Введите название книги\n");
        }
        if (customer.isEmpty() || !customer.matches("^[ а-яА-Я]+$")) {
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

    private char getGenderFromCheckBox() {
        Component[] comps = infoPanel.getComponents();
        if (((JCheckBox) (((JPanel) comps[2]).getComponents()[1])).isSelected()) {
            return 'F';
        } else return 'M';
    }

    private GregorianCalendar getDataFromSpinners() {
        Component[] comps = infoPanel.getComponents();

        int day = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[1]).getComponents()[1]).getValue();
        int month = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[2]).getComponents()[1]).getValue();
        int year = (int) ((JSpinner) ((JPanel) ((JPanel) comps[7]).getComponents()[3]).getComponents()[1]).getValue();
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar;
    }

    public void button1ActionListener(JButton button1, JButton button2) {
        StringBuilder message = new StringBuilder();
        boolean hasRecord = false;
        boolean res = false;

        String nameBook = ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText();
        String customer = ((JTextField) ((JPanel) infoPanel.getComponents()[1]).getComponents()[1]).getText();
        String address = ((JTextField) ((JPanel) infoPanel.getComponents()[4]).getComponents()[1]).getText();
        String email = ((JTextField) ((JPanel) infoPanel.getComponents()[3]).getComponents()[1]).getText();
        String phone = ((JFormattedTextField) ((JPanel) infoPanel.getComponents()[5]).getComponents()[1]).getText();

        Record.State state = (Record.State) ((JComboBox) ((JPanel) infoPanel.getComponents()[6]).getComponents()[1]).getSelectedItem();
        GregorianCalendar GregorianCalendar = getDataFromSpinners();

        message.append(validationValues());
        if (message.toString().isEmpty()) {
            Book needBook = library.getBookFromName(nameBook);
            if (needBook != null) {
                for (Record record : library.getBook(needBook)) {
                    if (record.getCustomer().getName().equals(customer) &&
                            record.getBeginDate().compareTo(GregorianCalendar) > 0 &&
                            record.getStatus() == Record.Status.GIVE &&
                            record.getEndDate() == null) {
                        hasRecord = true;
                        message.append("Указанный читатель уже взял эту книгу\n");
                    }
                }
            } else {
                hasRecord = true;
                message.append("Книга с таким названием не найдена, попробуйте еще раз \n");
            }
            if (!hasRecord) {
                res = library.giveBookToCustomer(needBook,
                        new Customer(customer,
                                getGenderFromCheckBox(),
                                email,
                                phone,
                                address
                                ),
                        state
                );
                if (!res)
                    message.append("Книги с таким названием нет в наличии \n");
                else {
                    setVisible(false);
                    dispose();
                }
            }
        }
        if(!res) JOptionPane.showMessageDialog(null, message.toString());
    }

    public void button2ActionListener(JButton button1, JButton button2) {
        setVisible(false);
        dispose();
    }
}
