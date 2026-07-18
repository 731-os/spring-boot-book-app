package com.example.bookapp.repository;

import java.util.List;
import java.util.Optional;
import com.example.bookapp.model.Book;

public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book save(Book book);
    void deleteById(Long id);

    List<Book> search(String title, String author, Boolean read);
}