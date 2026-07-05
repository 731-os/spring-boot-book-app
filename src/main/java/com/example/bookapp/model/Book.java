package com.example.bookapp.model;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Column;

@Entity
@Table(catalog = "", schema = "", name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @Column(name = "published_year")
    private int publishedYear;

    private boolean read;

    public Book() {    }


//    private Long id;
//    private String title;
//    private String author;
//    private int publishedYear;
//    private boolean read;

    public Book(Long id, String title, String author, int publishedYear, boolean read) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.read = read;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}