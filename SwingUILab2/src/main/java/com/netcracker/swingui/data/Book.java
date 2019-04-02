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
    private Author[] authors;
    private int qty = 0;

    public Book() {}
    public Book(Book book){
        this.name = book.name;
        this.authors = book.authors.clone();
        this.qty = book.qty;
    }
    @JsonCreator
    public Book(@JsonProperty("name") String name, @JsonProperty("authors") Author[] authors, @JsonProperty("qty") int qty) {
        this.name = name;
        this.authors = authors.clone();
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public Author[] getAuthors() {
        return authors.clone();
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
                "\", \"authors\":" + Arrays.toString(authors) +
                ", \"qty\":\"" + qty +
                "\"}";
    }

    public String getAuthorNames() {
        StringBuilder sb = new StringBuilder();
        for (Author a : authors) {
            sb.append(a.getName() + ",");
        }
        return sb.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Book book = (Book) obj;

        return book.qty == qty &&
                name.equals(book.name) &&
                Arrays.equals(book.authors, authors);
    }

    @Override
    public int hashCode() {
        int result = 14;
        result += name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(authors);
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
        Author[] authors = new Author[authorsArray.size()];
        int i = 0;
        for (JsonNode authorNode : authorsArray) {
            authors[i] = new Author(authorNode.get("name").toString(),
                    authorNode.get("email").toString(),
                    authorNode.get("gender").toString().charAt(0));
        }
        return (new Book(name, new Author[]{
                new Author("R. D. Bradbury", "", 'M')
        }, node.get("qty").asInt()));
    }
}