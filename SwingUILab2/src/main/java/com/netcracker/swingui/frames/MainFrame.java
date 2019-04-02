package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainFrame extends JFrame {
    Library library;
    JTable table;

    JMenuBar menu;
    JPanel searchPanel;
    JPanel buttonsPanel;
    JScrollPane tablePanel;
    Font font = new Font("Verdana", Font.PLAIN, 12);

    public MainFrame() {
        super("Library");

        setLocation(270, 20);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 700));

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));

        menu = createMenu();
        searchPanel = createSearch();
        tablePanel = createTable();
        buttonsPanel = createButtons();

        setJMenuBar(menu);
        mainPanel.add(searchPanel);
        mainPanel.add(tablePanel);
        mainPanel.add(buttonsPanel);
        add(mainPanel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                searchPanel.setPreferredSize(new Dimension(getWidth() - 50, 35));
                tablePanel.setPreferredSize(new Dimension(getWidth() - 50, 3 * getHeight() / 5));
                buttonsPanel.setPreferredSize(new Dimension(getWidth() - 50, 35));
                repaint();
            }
        });

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

    public JPanel createButtons() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 150, 0));
        grid.setFont(font);
        grid.setPreferredSize(new Dimension(700, 30));
        JButton button1 = new JButton("Выдача книги");
        JButton button2 = new JButton("Возврат книги");
        JButton button3 = new JButton("Карта читателя");

        button1.addActionListener(e -> SwingUtilities.invokeLater(() -> new GivingBookFrame()));
        button2.addActionListener(e -> SwingUtilities.invokeLater(() -> new GettingBookFrame()));
        button3.addActionListener(e -> {
            String personName = JOptionPane.showInputDialog("Enter person name :");
            SwingUtilities.invokeLater(() -> new CustomerFrame(library, personName));
        });
        grid.add(button1);
        grid.add(button2);
        grid.add(button3);

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.add(grid);
        return flow;
    }

    public JScrollPane createTable() {
        library = new Library("result.json");
        library.addTableModelListener(e->repaint());
        table = new JTable(library);
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
                JMenuItem item = new JMenuItem("Редактировать");
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Book changed = library.getBookFromName(table.getValueAt(row, 0).toString());

                        SwingUtilities.invokeLater(() -> new BookFrame(library, changed, true));
                        library.saveToFile(new File("result.json"));
                    }
                });
                menu.add(item);
                menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }

        });
        JScrollPane jScrollPane = new JScrollPane(table);
        jScrollPane.setPreferredSize(new Dimension(700, 150));
        return jScrollPane;
    }

    public JPanel createSearch() {
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField sField = new JTextField();
        JButton sButton = new JButton("Поиск");
        sField.setPreferredSize(new Dimension(590, 25));
        sButton.setPreferredSize(new Dimension(100, 25));

        sButton.addActionListener(e -> searchInTable(sField.getText()));
        searchPanel.add(sField);
        searchPanel.add(sButton);
        return searchPanel;
    }

    public void searchInTable(final String q) {
        table.changeSelection(library.getBooksIndex(q), 1, false, false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
