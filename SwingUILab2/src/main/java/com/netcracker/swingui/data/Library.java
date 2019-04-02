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
    private String sourceFileName = "";

    @JsonProperty("dataBase")
    @JsonDeserialize(keyUsing = Book.class)
    private Map<Book, List<Record>> dataBase;

    @JsonCreator
    public Library(String sourcefileName) {
        this.sourceFileName = sourcefileName;
        dataBase = new HashMap<>();
        downloadFromFile(new File(sourcefileName));
    }

    public void downloadFromFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(Book.class, new Book());
        mapper.registerModule(simpleModule);
        try {
            dataBase = mapper.readValue(file, new TypeReference<HashMap<Book, List<Record>>>() {
            });
        } catch (IOException e) {
            System.out.println("Something with downloading from file went wrong");
            e.printStackTrace();
        }
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
    }

    public List<Record> getBook(Book book){
        return dataBase.get(book);
    }
    public void deleteBook(Book book) {
        if (dataBase.get(book).isEmpty())
            dataBase.remove(book);
        else
            throw new IllegalArgumentException(book.toString() + " still has customers");
    }

    public void changeBook(Book oldBook, Book newBook) {
        List<Record> customers = dataBase.get(oldBook);
        if (customers != null) {
            dataBase.remove(oldBook);
            dataBase.put(newBook, customers);
        } else
            throw new IllegalArgumentException(oldBook.toString() + " doesn't exist in the book base");
    }

    public boolean giveBookToCustomer(Book book, Customer customer) {
        List<Record> customerList = dataBase.get(book);
        boolean result = false;
        if (customerList != null) {
            if (book.getQty() != 0) {
                result = true;
                dataBase.remove(book);

                customerList.add(
                        new Record(customer, Record.State.GOOD, Record.Status.GIVE, new Date()));
                book.setQty(book.getQty() - 1);

                dataBase.put(book, customerList);
            }
        } else
            throw new IllegalArgumentException(book + " doesn't exist in the book base");
        return result;
    }

    public boolean getBookFromCustomer(Book book, Customer customer, Record.State state) {
        boolean result = false;
        List<Record> customerList = dataBase.get(book);
        if (customerList != null) {
            for (Record record : customerList) {
                if (record.getCustomer().equals(customer)) {
                    record.setState(state);
                    record.setStatus(Record.Status.GET);
                    record.setEndDate(new Date());
                }
            }
            if (result) {
                dataBase.remove(book);
                book.setQty(book.getQty() + 1);
                dataBase.put(book, customerList);
            } else
                throw new IllegalArgumentException(customer + " didn't take this book");
        } else
            throw new IllegalArgumentException(book + " doesn't exist in the book base");
        return result;
    }

    public Customer findCustomerByName(final String customer) {
        Customer find = null;
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            for(Record record: entry.getValue()){
                if(record.getCustomer().getName().equals(customer))
                    return record.getCustomer();
            }
        }
        return find;
    }

    public Object[] findAllCustomersBooks(Customer customer){
        Set<Book> bookList = new HashSet<>();
        for (Map.Entry<Book, List<Record>> entry : dataBase.entrySet()) {
            for(Record record: entry.getValue()){
                if(record.getCustomer().equals(customer))
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
        // if(current!=null)
        switch (columnIndex) {
            case 0:
                return current.getKey().getName();
            case 1:
                return current.getKey().getAuthorNames();
            case 2:
                return current.getKey().getQty();
            case 3:
                return current.getValue().get(current.getValue().size() - 1).getCustomer().getName();
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

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, ANY);
        Map<Book, List<Record>> lib = new HashMap<Book, List<Record>>();
        Author[][] authors = new Author[][]{
                new Author[]{
                        new Author("A. A. Pushkin", "", 'M')
                },
                new Author[]{
                        new Author("A. N. Strugatsky", "", 'M'),
                        new Author("B. N. Strugatsky", "", 'M')
                },
                new Author[]{
                        new Author("R. D. Bradbury", "", 'M')
                },
                new Author[]{
                        new Author("R. D. Bradbury", "", 'M')
                }
        };
        Book[] books = new Book[]{
                new Book("Eugene Onegin", authors[0], 200),
                new Book("Eugene Onegin", authors[0], 200),
                new Book("Monday starts Saturday", authors[1], 500),
                new Book("451 degrees Fahrenheit", authors[2], 10),
                new Book("Wine from dandelions", authors[2], 11)
        };
        Customer[] customers = new Customer[]{
                new Customer("I :)", 'F', "")
        };
        List<Record> tmp = new LinkedList<Record>();
        tmp.add(new Record(customers[0], Record.State.GOOD, Record.Status.GIVE, new Date()));

        lib.put(books[0], tmp);
        try {
            System.out.println(mapper.writeValueAsString(lib));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
