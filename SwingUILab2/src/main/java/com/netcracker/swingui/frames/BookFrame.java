package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Author;
import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class BookFrame extends AbstractFrame {
    private Book book;
    private Library library;

    private JPanel infoPanel;
    private JScrollPane table;
    private JPanel buttons;
    private JPanel authorsPanel;
    private boolean editableFlag = true;
    private boolean isAddingBook = false;

    public BookFrame(Library library) {
        super("Страница книги");
        this.library = library;
        this.editableFlag = true;
        this.isAddingBook = true;

        setLocation(270, 200);
        setPreferredSize(new Dimension(800, 400));

        authorsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        authorsPanel.setBorder((BorderFactory.createTitledBorder("Автор")));

        authorsPanel.add(createField("Имя автора: ", "", editableFlag));
        authorsPanel.add(createCheckBox("Пол: ", 'F', editableFlag));
        authorsPanel.add(createField("E-mail ", "", editableFlag));

        infoPanel = new JPanel(new GridBagLayout());
        addItem(infoPanel, createField("Название книги: ", "", editableFlag),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, authorsPanel,
                2, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createSpinner("Количество: ", 0, 0, 1000, editableFlag),
                2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        table = createTable();
        buttons = createButtons("Сохранить", "Выход");

        JPanel mainPanel = new JPanel(new GridBagLayout());

        addItem(mainPanel, infoPanel, 2, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, table, 2, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addItem(mainPanel, buttons, 2, 3, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    public BookFrame(Library library, Book book, boolean editableFlag) {
        super(book.getName());
        this.book = book;
        this.library = library;
        this.editableFlag = editableFlag;

        setLocation(270, 20);
        setPreferredSize(new Dimension(800, 700));

        infoPanel = new JPanel(new GridBagLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());

        authorsPanel = new JPanel(new GridBagLayout());
        authorsPanel.setBorder((BorderFactory.createTitledBorder("Автор(ы)")));

        addItem(authorsPanel, createField("Имя автора: ", book.getAuthors().getName(), editableFlag),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(authorsPanel, createCheckBox("Пол: ", book.getAuthors().getGender(), editableFlag),
                2, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(authorsPanel, createField("E-mail: ", book.getAuthors().getEmail(), editableFlag),
                2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        addItem(infoPanel, createField("Название книги: ", book.getName(), editableFlag),
                2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, authorsPanel,
                2, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(infoPanel, createSpinner("Количество: ", book.getQty(), 0, 1000, editableFlag),
                2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        table = createTable();
        buttons = createButtons("Редактировать", "Выход");
        addItem(mainPanel, infoPanel, 2, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, table, 2, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addItem(mainPanel, buttons, 2, 3, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public JScrollPane createTable() {
        JTable table = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return library.getBook(book) == null ? 0 : library.getBook(book).size();
            }

            @Override
            public int getColumnCount() {
                return 9;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                List<Record> current = library.getBook(book);
                if (current != null) {
                    switch (columnIndex) {
                        case 0:
                            return current.get(rowIndex).getCustomer().getName();
                        case 1:
                            return current.get(rowIndex).getCustomer().getAddress();
                        case 2:
                            return current.get(rowIndex).getCustomer().getEmail();
                        case 3:
                            return current.get(rowIndex).getCustomer().getGender();
                        case 4:
                            return current.get(rowIndex).getCustomer().getPhone();
                        case 5:
                            return current.get(rowIndex).getState();
                        case 6:
                            return current.get(rowIndex).getStatus();
                        case 7:
                            return current.get(rowIndex).getBeginDate().get(GregorianCalendar.DAY_OF_MONTH) + "." +
                                    current.get(rowIndex).getBeginDate().get(GregorianCalendar.MONTH) + "." +
                                    current.get(rowIndex).getBeginDate().get(GregorianCalendar.YEAR);
                        case 8:
                            return current.get(rowIndex).getEndDate() == null ? "Нет" :
                                    current.get(rowIndex).getEndDate().get(GregorianCalendar.DAY_OF_MONTH) + "." +
                                            current.get(rowIndex).getEndDate().get(GregorianCalendar.MONTH) + "." +
                                            current.get(rowIndex).getEndDate().get(GregorianCalendar.YEAR);
                    }
                }
                return null;
            }


            @Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Имя читателя";
                    case 1:
                        return "Адрес читателя";
                    case 2:
                        return "EMail читателя";
                    case 3:
                        return "Пол";
                    case 4:
                        return "Телефон";
                    case 5:
                        return "Состояние книги";
                    case 6:
                        return "Статус книги";
                    case 7:
                        return "Дата получения книги";
                    case 8:
                        return "Дата возврата книги";
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
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return String.class;
                    case 5:
                        return String.class;
                    case 6:
                        return String.class;
                    case 7:
                        return String.class;
                    case 8:
                        return String.class;
                }
                return Object.class;
            }
        });
        table.setFont(font);
        table.setRowHeight(25);
        JScrollPane panel = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.setPreferredSize(new Dimension(750, 240));

        return panel;
    }

    public void button1ActionListener(JButton button1, JButton button2) {
        if (button1.getText().equals("Редактировать")) {
            button1.setText("Сохранить");
            button2.setText("Отмена");
            setEditable(true);
        } else {
            StringBuilder message = new StringBuilder(validationValues());
            if (message.toString().isEmpty()) {
                Book newBook = new Book();
                Component[] comps = infoPanel.getComponents();
                newBook.setQty((int) ((JSpinner) ((JPanel) comps[2]).getComponents()[1]).getValue());
                newBook.setAuthors(new Author(((JTextField) (((JPanel) ((JPanel) comps[1]).getComponents()[0]).getComponents()[1])).getText(),
                        ((JTextField) (((JPanel) ((JPanel) comps[1]).getComponents()[2]).getComponents()[1])).getText(),
                        getGenderFromCheckBox())
                );
                newBook.setName((String) ((JTextField) ((JPanel) comps[0]).getComponents()[1]).getText());
                if (isAddingBook) {
                    library.addBook(newBook);
                    table.setVisible(true);
                } else
                    library.changeBook(book, newBook);
                book = newBook;
                library.saveToFile(new File("result.json"));

                button1.setText("Редактировать");
                button2.setText("Выход");
                setEditable(false);
            } else JOptionPane.showMessageDialog(null, "Некорректный ввод:\n" + message.toString());
        }
    }

    public void button2ActionListener(JButton button1, JButton button2) {
        if (button2.getText().equals("Выход")) {
            setVisible(false);
            dispose();
        } else {
            button1.setText("Редактировать");
            button2.setText("Выход");
            setEditable(false);
        }
    }

    public char getGenderFromCheckBox() {
        Component[] comps = infoPanel.getComponents();
        Component[] aComps = ((JPanel) comps[1]).getComponents();
        if (((JCheckBox) (((JPanel) aComps[1]).getComponents()[1])).isSelected()) {
            return 'F';
        } else return 'M';
    }

    public void setEditable(boolean flag) {
        Component[] components = infoPanel.getComponents();
        ((JTextField) (((JPanel) components[0]).getComponents()[1])).setEditable(flag);
        ((JSpinner) ((JPanel) components[2]).getComponents()[1]).setEnabled(flag);
        ((JTextField) (((JPanel) ((JPanel) components[1]).getComponents()[0]).getComponents()[1])).setEditable(flag);
        ((JTextField) (((JPanel) ((JPanel) components[1]).getComponents()[2]).getComponents()[1])).setEditable(flag);

        Component[] aComps = ((JPanel) components[1]).getComponents();
        ((JCheckBox) (((JPanel) aComps[1]).getComponents()[1])).setEnabled(flag);
        ((JCheckBox) (((JPanel) aComps[1]).getComponents()[2])).setEnabled(flag);
    }

    private String validationValues() {
        Component[] comps = infoPanel.getComponents();

        StringBuilder message = new StringBuilder();

        String nameBook = (String) ((JTextField) ((JPanel) comps[0]).getComponents()[1]).getText();
        String authorName = ((JTextField) (((JPanel) ((JPanel) comps[1]).getComponents()[0]).getComponents()[1])).getText();
        String email = ((JTextField) (((JPanel) ((JPanel) comps[1]).getComponents()[2]).getComponents()[1])).getText();

        if (nameBook.isEmpty()) {
            message.append("Введите название книги\n");
        }
        if (authorName.isEmpty() || !authorName.matches("^[ ,а-яА-Я-]+$")) {
            message.append("Некорректное имя автора\n");
        }
        if (email.isEmpty()) {
            message.append("Введите email автора\n");
        }

        return message.toString();
    }

}
