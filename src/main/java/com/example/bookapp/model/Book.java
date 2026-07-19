package com.example.bookapp.model;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Table(catalog = "", schema = "", name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @NotBlank: null・空文字・空白のみ、を全部エラーにする。
    // (似た@NotNullはnullしかチェックしない、@NotEmptyは空白だけの文字列を
    // 見逃してしまう、という違いがある。文字列の必須項目には@NotBlankが一番厳しい)
    @NotBlank(message = "タイトルは必須です")
    private String title;

    @NotBlank(message = "著者名は必須です")
    private String author;

    @Column(name = "published_year")
    // @Min/@Max: 数値の範囲チェック。出版年がマイナスや、
    // 未来すぎる年(例: 9999年)になるのを防ぐ
    @Min(value = 1000, message = "出版年は1000年以降で入力してください")
    @Max(value = 2100, message = "出版年は2100年以前で入力してください")
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