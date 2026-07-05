package com.example.bookapp.repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.example.bookapp.model.Book;

/*
InMemoryBookRepositoryをそのままにしておくとマズいのか
 → 前に予告してた「2つの実装を同時にBean登録すると、Springがどっちを注入すればいいか分からなくなる」という問題が、
まさに今起きるんだよね。今BookRepositoryを実装してるクラスが、InMemoryBookRepositoryとDomaBookRepositoryの2つになっちゃったから。
@Repositoryを削除(またはコメントアウト)
 */
//@Repository
public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> store = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(sequence.incrementAndGet());
        }
        store.put(book.getId(), book);
        return book;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}