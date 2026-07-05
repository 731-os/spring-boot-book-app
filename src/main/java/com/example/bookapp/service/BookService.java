package com.example.bookapp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // コンストラクタインジェクション: SpringがBookRepositoryの実装を自動で渡してくれる
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("本が見つかりません: id=" + id));
    }

    public Book create(Book book) {
        book.setId(null); // 新規登録なのでidは自動採番に任せる
        return bookRepository.save(book);
    }

    public Book update(Long id, Book book) {
        Book existing = findById(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setPublishedYear(book.getPublishedYear());
        existing.setRead(book.isRead());
        return bookRepository.save(existing);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}