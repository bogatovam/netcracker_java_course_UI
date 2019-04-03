package com.netcracker.swingui.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

public class Book extends KeyDeserializer {
    private String name;
    private Author authors;
    private int qty = 0;

    public Book() {
    }

    public Book(Book book) {
        this.name = book.name;
        this.authors = book.authors;
        this.qty = book.qty;
    }

    @JsonCreator
    public Book(@JsonProperty("name") String name, @JsonProperty("authors") Author authors, @JsonProperty("qty") int qty) {
        this.name = name;
        this.authors = authors;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public Author getAuthors() {
        return authors;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name +
                "\", \"authors\":" + authors +
                ", \"qty\":\"" + qty +
                "\"}";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthors(Author authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Book book = (Book) obj;

        return book.qty == qty &&
                name.equals(book.name) &&
                book.authors.equals(authors);
    }

    @Override
    public int hashCode() {
        int result = 14;
        result += name != null ? name.hashCode() : 0;
        result = 31 * result + authors.hashCode();
        result = 31 * result + qty;
        return result;
    }

    @Override
    public Book deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(key);
        JsonNode authorsArray = mapper.readTree(node.get("authors").toString());
        String name = node.get("name").toString();
        name = name.substring(1, name.length() - 1);
        Author authors = null;
        int i = 0;
        String aName = authorsArray.get("name").toString();
        String email = authorsArray.get("email").toString();
        authors = new Author(aName.substring(1, aName.length() - 1),
                email.substring(1, email.length() - 1),
                authorsArray.get("gender").toString().charAt(1));
        return (new Book(name, authors, node.get("qty").asInt()));
    }
}