package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainFrame extends AbstractFrame {
    private Library library;
    private JTable table;

    private JMenuBar menu;
    private JPanel searchPanel;
    private JPanel buttonsPanel;
    private JScrollPane tablePanel;
    Font font = new Font("Verdana", Font.PLAIN, 12);

    public MainFrame() {
        super("Библиотека");

        setLocation(270, 20);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 700));

        JPanel mainPanel = new JPanel(new GridBagLayout());

        menu = createMenu();
        searchPanel = createSearch();
        tablePanel = createTable();
        buttonsPanel = createButtons("Выдача книги", "Возврат книги", "Карта читателя");

        setJMenuBar(menu);
        addItem(mainPanel, searchPanel, 2, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(mainPanel, tablePanel, 2, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addItem(mainPanel, buttonsPanel, 2, 3, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public JMenuBar createMenu() {
        Font fontMenu = new Font("Verdana", Font.PLAIN, 14);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenu editMenu = new JMenu("Правка");

        fileMenu.setFont(fontMenu);
        editMenu.setFont(fontMenu);
        JMenuItem downMenu = new JMenuItem("Загрузить...");
        downMenu.setFont(fontMenu);
        fileMenu.add(downMenu);

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setFont(fontMenu);
        fileMenu.add(exitItem);

        downMenu.addActionListener(e -> {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                library.downloadFromFile(fileopen.getSelectedFile());
            }
        });
        exitItem.addActionListener(e -> System.exit(0));

        JMenuItem addMenu = new JMenuItem("Добавить книгу");
        addMenu.setFont(fontMenu);
        editMenu.add(addMenu);

        addMenu.addActionListener(e -> SwingUtilities.invokeLater(() -> new BookFrame(library)));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    public void button1ActionListener() {
        SwingUtilities.invokeLater(() -> new GivingBookFrame(library));
    }

    public void button2ActionListener() {
        SwingUtilities.invokeLater(() -> new GettingBookFrame(library));
    }

    public void button3ActionListener() {
        String personName = JOptionPane.showInputDialog("Введите имя читателя: ");
        if (personName.isEmpty() || !personName.matches("^[a-zA-Z]+$")) {
            JOptionPane.showMessageDialog(null, "Некорректный ввод имени читателя\n" +
                    "Имя должно быть непустым, не сожержать цифр и начинаться с заглавной буквы");
            return;
        } else {
            if (library.findCustomerByName(personName) == null) {
                JOptionPane.showMessageDialog(null, "Читатель отсутствует в базе ");
            } else SwingUtilities.invokeLater(() -> new CustomerFrame(library, personName));

        }
    }

    public JScrollPane createTable() {
        library = new Library("result.json");
        table = new JTable(library);
        table.setFont(font);
        table.setRowHeight(25);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
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
                JMenuItem item = new JMenuItem("Редактировать");
                JMenuItem item2 = new JMenuItem("Удалить");

                item2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Book deleted = library.getBookFromName(table.getValueAt(row, 0).toString());
                        library.deleteBook(deleted);
                        library.saveToFile(new File("result.json"));
                    }
                });
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Book changed = library.getBookFromName(table.getValueAt(row, 0).toString());

                        SwingUtilities.invokeLater(() -> new BookFrame(library, changed, true));
                        library.saveToFile(new File("result.json"));
                    }
                });
                menu.add(item);
                menu.add(item2);
                menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        });
        JScrollPane jScrollPane = new JScrollPane(table);
        return jScrollPane;
    }

    public JPanel createSearch() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        JTextField sField = new JTextField();
        JButton sButton = new JButton("Поиск");
        sField.setPreferredSize(new Dimension(300, 25));
        sButton.setPreferredSize(new Dimension(100, 25));

        sButton.addActionListener(e -> searchInTable(sField.getText()));
        addItem(searchPanel, sField, 1, 3, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(searchPanel, sButton, 5, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        return searchPanel;
    }

    public void searchInTable(final String q) {
        int book = library.getBooksIndex(q);
        if (book < 0)
            JOptionPane.showMessageDialog(null,
                    "Книга с таким названием не найдена, попробуйте еще раз ");
        else
            table.changeSelection(book, 1, false, false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
