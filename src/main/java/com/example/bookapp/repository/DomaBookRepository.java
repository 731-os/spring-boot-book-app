package com.example.bookapp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.example.bookapp.dao.BookDao;
import com.example.bookapp.model.Book;

@Repository
public class DomaBookRepository implements BookRepository {

    private final BookDao bookDao;

    public DomaBookRepository(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public List<Book> findAll() {
        return bookDao.selectAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookDao.selectById(id);
    }

    @Override
    public List<Book> search(String title, String author, Boolean read) {
        return bookDao.search(title, author, read);
    }

    @Override
    public List<Book> findPage(int limit, int offset) {
        return bookDao.selectPage(limit, offset);
    }

    @Override
    public long count() {
        return bookDao.count();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            bookDao.insert(book);
        } else {
            bookDao.update(book);
        }
        return book;
    }

    @Override
    public void deleteById(Long id) {
        Book book = new Book();
        book.setId(id);
        bookDao.delete(book);
    }
}