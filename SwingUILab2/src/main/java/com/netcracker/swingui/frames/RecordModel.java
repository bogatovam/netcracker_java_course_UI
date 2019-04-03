package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.table.AbstractTableModel;
import java.util.GregorianCalendar;
import java.util.List;

public class RecordModel extends AbstractTableModel {
    private Library library;
    private Book book;
    private Boolean editableFlag;

    public RecordModel(Library library, Book book, Boolean editableFlag) {
        this.library = library;
        this.book = book;
        this.editableFlag = editableFlag;

    }

    public Record getRecord(int row) {
        return library.getBook(book).get(row);
    }

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
        return current.get(rowIndex);
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return editableFlag;
            case 1:
                return editableFlag;
            case 2:
                return editableFlag;
            case 3:
                return editableFlag;
            case 4:
                return editableFlag;
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        List<Record> current = library.getBook(book);
        if (current != null) {
            switch (columnIndex) {
                case 0:
                    current.get(rowIndex).getCustomer().setName((String) aValue);
                    break;
                case 1:
                    current.get(rowIndex).getCustomer().setAddress((String) aValue);
                    break;

                case 2:
                    current.get(rowIndex).getCustomer().setEmail((String) aValue);
                    break;

                case 3:
                    current.get(rowIndex).getCustomer().setGender(((String) aValue).charAt(0));
                    break;
                case 4:
                    current.get(rowIndex).getCustomer().setPhone((String) aValue);
                    break;
            }
        }
        TableChecker.resultM = true;
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
}

