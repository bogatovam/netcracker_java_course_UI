package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Author;
import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private Boolean editableFlag;
    private Boolean isAddingBook = false;

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
        RecordModel tableModel = new RecordModel(library, book, editableFlag);
        JTable table = new JTable(tableModel);
        for (int i = 0; i < 9; ++i)
            table.getColumnModel().getColumn(i).setCellRenderer(new TableChecker());
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(font);
        table.setRowHeight(25);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);

                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    Book changed = library.getBookFromName(table.getValueAt(row, 0).toString());
                    SwingUtilities.invokeLater(() -> new BookFrame(library, changed, false));
                }
                if (mouseEvent.isPopupTrigger() && table.getSelectedRow() != -1) {
                    doPop(mouseEvent);
                }
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (mouseEvent.isPopupTrigger() && table.getSelectedRow() != -1)
                    doPop(mouseEvent);
            }

            private void doPop(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item2 = new JMenuItem("Удалить");

                item2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(editableFlag) {
                            library.deleteRecord(book, tableModel.getRecord(row));
                            tableModel.fireTableDataChanged();
                            library.saveToFile(sourceFile);
                        }
                    }
                });
                menu.add(item2);
                menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        });

        JScrollPane panel = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.setPreferredSize(new Dimension(750, 240));

        return panel;
    }

    public void button1ActionListener(JButton button1, JButton button2) {
        if (button1.getText().equals("Редактировать")) {
            button1.setText("Сохранить");
            button2.setText("Отмена");
            editableFlag = true;
            setEditable(true);
        } else {
            if (TableChecker.resultM) {
                editableFlag = false;
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
                    library.saveToFile(sourceFile);

                    button1.setText("Редактировать");
                    button2.setText("Выход");
                    setEditable(false);
                } else JOptionPane.showMessageDialog(null, "Некорректный ввод:\n" + message.toString());
            } else JOptionPane.showMessageDialog(this, "Введите корректные значения в подсвечиваемые поля");
        }
    }

    public void button2ActionListener(JButton button1, JButton button2) {
        if (button2.getText().equals("Выход")) {
            setVisible(false);
            dispose();
        } else {
            editableFlag = false;
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

class TableChecker extends DefaultTableCellRenderer {
    public static Boolean resultM = true;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        String text = label.getText();
        boolean result = true;
        if (col == 0)
            result = text != null && !text.equals("") && text.matches("^[ а-яА-Я]+$");
        else if (col == 3) {
            result = text != null && (text.charAt(0) == 'F' || text.charAt(0) == 'M');
        } else if (col == 1 || col == 2) {
            result = text != null && !text.equals("");
        } else if (col == 4) {
            result = text != null && text.matches("^((\\+7))?(\\(?\\d{3}\\)?)?[\\d]{7}$");
        }
        if (hasFocus) label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        else if (result) label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        else label.setBorder(BorderFactory.createLineBorder(Color.RED));
        resultM = resultM & result;
        return label;
    }
}

