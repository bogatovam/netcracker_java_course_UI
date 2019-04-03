package com.netcracker.swingui.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

public class Library extends AbstractTableModel {
    private File sourceFile;
    @JsonProperty("dataBase")
    @JsonDeserialize(keyUsing = Book.class)
    private Map<Book, List<Record>> dataBase;

    @JsonCreator
    public Library(String sourcefileName) {
        sourceFile = new File(sourcefileName);
        dataBase = new HashMap<>();
        downloadFromFile(sourceFile);
    }

    public void downloadFromFile(File file) {
        sourceFile = file;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(Book.class, new Book());
        mapper.registerModule(simpleModule);
        try {
            dataBase = mapper.readValue(file, new TypeReference<HashMap<Book, List<Record>>>() {
            });
        } catch (IOException e) {
            System.out.println("Something with downloading from file went wrong");
            throw new RuntimeException(e);
        }
        fireTableDataChanged();
    }

    public void saveToFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, dataBase);
        } catch (IOException e) {
            System.out.println("Something with save to file went wrong");
            e.printStackTrace();
        }
    }

    public Book[] getBooks() {
        return ((Book[]) dataBase.keySet().toArray()).clone();
    }

    public Book getBookFromName(final String name) {
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            if (entry.getKey().getName().equals(name))
                return entry.getKey();
        }
        return null;
    }

    public int getIndexFromBook(Book book) {
        int currentIndex = 0;
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            if (entry.getKey() == book) return currentIndex;
            currentIndex++;
        }
        return -1;
    }

    public Map.Entry<Book, List<Record>> getBookFromIndex(int index) {
        int currentIndex = 0;
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            if (currentIndex == index) return entry;
            currentIndex++;
        }
        return null;
    }


    public void addBook(Book book) {
        dataBase.computeIfAbsent(book, k -> new LinkedList<Record>());
        fireTableDataChanged();
    }

    public List<Record> getBook(Book book) {
        return dataBase.get(book);
    }

    public void deleteBook(Book book) {
        dataBase.remove(book);
        fireTableDataChanged();
    }

    public void changeBook(Book oldBook, Book newBook) {
        List<Record> customers = dataBase.get(oldBook);
        if (customers != null) {
            dataBase.remove(oldBook);
            dataBase.put(newBook, customers);
        } else
            throw new IllegalArgumentException(oldBook.toString() + " doesn't exist in the book base");
        fireTableDataChanged();
    }

    public boolean giveBookToCustomer(Book book, Customer customer, Record.State state) {
        List<Record> customerList = dataBase.get(book);
        boolean result = false;
        if (customerList != null) {
            if (book.getQty() != 0) {
                result = true;
                dataBase.remove(book);

                customerList.add(
                        new Record(customer, state, Record.Status.GIVE, new GregorianCalendar()));
                book.setQty(book.getQty() - 1);

                dataBase.put(book, customerList);
            }
        } else
            throw new IllegalArgumentException(book + " doesn't exist in the book base");
        saveToFile(sourceFile);
        fireTableDataChanged();
        return result;
    }

    public boolean getBookFromCustomer(Book book, String customer, Record.State state, GregorianCalendar date) {
        boolean result = false;
        List<Record> customerList = dataBase.get(book);
        if (customerList != null) {
            for (Record record : customerList) {
                if (record.getCustomer().getName().equals(customer) &&
                        record.getStatus() == Record.Status.GIVE &&
                        record.getEndDate() == null) {
                    record.setStatus(Record.Status.GET);
                    record.setState(state);
                    record.setEndDate(date);
                    result = true;
                }
            }
            if (result) {
                dataBase.remove(book);
                book.setQty(book.getQty() + 1);
                dataBase.put(book, customerList);
            } else
                throw new IllegalArgumentException("Указанный читатель не брал эту книгу");
        } else
            throw new IllegalArgumentException("Указанная книга отсутствует в базе");
        fireTableDataChanged();
        return result;
    }

    public Customer findCustomerByName(final String customer) {
        Customer find = null;
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            for (Record record : entry.getValue()) {
                if (record.getCustomer().getName().equals(customer))
                    return record.getCustomer();
            }
        }
        return find;
    }

    public Object[] findAllCustomersBooks(Customer customer) {
        Set<Book> bookList = new HashSet<>();
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            for (Record record : entry.getValue()) {
                if (record.getCustomer().getName().equals(customer.getName()))
                    bookList.add(entry.getKey());
            }
        }
        return bookList.toArray();
    }

    public int size() {
        return dataBase.size();
    }

    @Override
    public int getRowCount() {
        return size();
    }

    public int getBooksIndex(final String bookName) {
        return getIndexFromBook(getBookFromName(bookName));
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map.Entry<Book, List<Record>> current = getBookFromIndex(rowIndex);
        if (current != null) {
            switch (columnIndex) {
                case 0:
                    return current.getKey().getName();
                case 1:
                    return current.getKey().getAuthors().getName();
                case 2:
                    return current.getKey().getQty();
                case 3:
                    return current.getValue().isEmpty() ? "Нет" :
                            current.getValue().get(current.getValue().size() - 1).getCustomer().getName();
            }
        }
        return null;
    }

    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Название книги";
            case 1:
                return "ФИО автора";
            case 2:
                return "Количество";
            case 3:
                return "Последний читатель";
        }
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return String.class;
        }
        return Object.class;
    }

    public void deleteRecord(Book book, Record record) {
        List<Record> customerList = dataBase.get(book);
        dataBase.remove(book);

        book.setQty(book.getQty() + 1);
        customerList.remove(record);

        dataBase.put(book, customerList);
        fireTableDataChanged();
        saveToFile(sourceFile);
    }
}
