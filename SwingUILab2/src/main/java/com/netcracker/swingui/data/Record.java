package com.netcracker.swingui.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.util.Pair;
import sun.util.calendar.BaseCalendar;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Record {
    public static enum Status {
        GIVE,
        GET
    }

    ;

    public static enum State {
        BAD,
        GOOD,
        EXCELLENT
    }

    ;
    private Customer customer;
    private State state;
    private Status status;
    private GregorianCalendar beginDate;
    private GregorianCalendar endDate;

    public Record() {
    }

    public Record(Customer customer, State state, Status status, GregorianCalendar date) {
        this.customer = customer;
        this.state = state;
        this.status = status;
        this.beginDate = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GregorianCalendar getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(GregorianCalendar beginDate) {
        this.beginDate = beginDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (customer != null ? !customer.equals(record.customer) : record.customer != null) return false;
        if (state != record.state) return false;
        if (status != record.status) return false;
        if (beginDate != null ? !beginDate.equals(record.beginDate) : record.beginDate != null) return false;
        return endDate != null ? endDate.equals(record.endDate) : record.endDate == null;
    }

    @Override
    public int hashCode() {
        int result = customer != null ? customer.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (beginDate != null ? beginDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "customer=" + customer +
                ", state=" + state +
                ", status=" + status +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                '}';
    }
}
