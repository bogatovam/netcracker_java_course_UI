package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BookFrame extends JFrame {
    private Book book;
    private Library library;

    private JTextField name;
    private JTextField authors;
    private JSpinner qty;

    private JPanel infoPanel;
    private JScrollPane table;
    private JPanel buttons;
    private Font font = new Font("Verdana", Font.PLAIN, 12);
    private boolean editableFlag = true;

    public BookFrame(Library library) {
        this.library = library;
        this.editableFlag = true;
        setLocation(270, 20);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 700));

        infoPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        JPanel mainPanel = new JPanel(new FlowLayout());

        infoPanel.add(createField("Название книги: ", ""));
        infoPanel.add(createField("Автор(ы): ", ""));
        infoPanel.add(createSpinner("Количество: ", 0));

        table = createTable();
        buttons = createButtons("Сохранить", "Выход");
        mainPanel.add(infoPanel);
        mainPanel.add(table);
        mainPanel.add(buttons);
        mainPanel.setPreferredSize(new Dimension(500, 500));
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 700));

        infoPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        JPanel mainPanel = new JPanel(new FlowLayout());

        infoPanel.add(createField("Название книги: ", book.getName()));
        infoPanel.add(createField("Автор(ы): ", book.getAuthorNames()));
        infoPanel.add(createSpinner("Количество: ", book.getQty()));

        table = createTable();
        buttons = createButtons("Редактировать", "Выход");
        mainPanel.add(infoPanel);
        mainPanel.add(table);
        mainPanel.add(buttons);
        mainPanel.setPreferredSize(new Dimension(500, 500));
        add(mainPanel);
        pack();
        setVisible(true);
    }

    public JPanel createField(String sourceLabel, String sourceText) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 100, 0));
        JTextField field = new JTextField(sourceText);
        JLabel label = new JLabel(sourceLabel);
        field.setEditable(editableFlag);
        field.setFont(font);
        label.setFont(font);

        panel.add(label);
        panel.add(field);
        return panel;
    }

    public JPanel createSpinner(String sourceLabel, Integer sourceValue) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 100, 0));
        SpinnerModel model = new SpinnerNumberModel();
        ((SpinnerNumberModel) model).setMinimum(0);
        ((SpinnerNumberModel) model).setMaximum(1000);
        model.setValue(sourceValue);
        JSpinner spinner = new JSpinner(model);
        JLabel label = new JLabel(sourceLabel);
        spinner.setEnabled(editableFlag);
        spinner.setFont(font);
        label.setFont(font);

        panel.add(label);
        panel.add(spinner);
        return panel;
    }

    public JPanel createButtons(final String text1, final String text2) {
        JPanel grid = new JPanel(new GridLayout(1, 3, 150, 0));
        grid.setFont(font);
        grid.setPreferredSize(new Dimension(700, 30));
        JButton button1 = new JButton(text1);
        JButton button2 = new JButton(text2);

        button1.addActionListener(e -> {
            if (button1.getText().equals("Редактировать")) {
                button1.setText("Сохранить");
                button2.setText("Отмена");
                setEditable(true);
            } else {
                Book newBook = new Book(book);
                newBook.setQty((int) ((JSpinner) ((JPanel) infoPanel.getComponents()[2]).getComponents()[1]).getValue());
                newBook.setName((String) ((JTextField) ((JPanel) infoPanel.getComponents()[0]).getComponents()[1]).getText());
                library.changeBook(book, newBook);
                book = newBook;
                library.saveToFile(new File("result.json"));
                button1.setText("Редактировать");
                button2.setText("Выход");
                setEditable(false);
            }
        });
        button2.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            if (button2.getText().equals("Выход")) {
                setVisible(false);
                dispose();
            } else {
                button1.setText("Редактировать");
                button2.setText("Выход");
                setEditable(false);
            }
        }));

        grid.add(button1);
        grid.add(button2);

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.add(grid);
        return flow;
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
                            return current.get(rowIndex).getBeginDate();
                        case 8:
                            return current.get(rowIndex).getEndDate();
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
                        return Date.class;
                    case 8:
                        return Date.class;
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

    public void setEditable(boolean flag) {
        Component[] components = infoPanel.getComponents();
        ((JTextField) (((JPanel) components[0]).getComponents()[1])).setEditable(flag);
        ((JSpinner) ((JPanel) components[2]).getComponents()[1]).setEnabled(flag);
    }
}
