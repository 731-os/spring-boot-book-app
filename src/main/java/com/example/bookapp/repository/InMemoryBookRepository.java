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
    public List<Book> search(String title, String author, Boolean read) {
        List<Book> result = new ArrayList<>();

        // Mapの中身を1件ずつ確認していく素朴なやり方。
        // DBの`WHERE 1=1 AND ...`の考え方を、Javaのif文で再現するイメージ
        for (Book book : store.values()) {
            // 条件が指定されてなければ(null)無条件で「一致」扱いにする。
            // 指定されてたら、containsで部分一致するかチェックする
            // (SQLの LIKE '%○○%' と同じ意味)
            boolean titleMatches = title == null || book.getTitle().contains(title);
            boolean authorMatches = author == null || book.getAuthor().contains(author);

            // readはBoolean(オブジェクト型)で受け取ってるので、
            // 「指定されなかった(null)」と「falseが指定された」を区別できる。
            // もしプリミティブ型のbooleanで受け取ると、この区別ができなくなってしまう
            boolean readMatches = read == null || book.isRead() == read;

            if (titleMatches && authorMatches && readMatches) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> findPage(int limit, int offset) {
        List<Book> all = new ArrayList<>(store.values());
        // SQL側のORDER BY idと同じ意味。並び順を固定しておく
        all.sort(Comparator.comparing(Book::getId));

        // offsetが全件数より大きい(=そんなページは存在しない)場合は空リストを返す
        if (offset >= all.size()) {
            return new ArrayList<>();
        }

        // Math.min: 「offset + limit」が全件数を超えそうな時、
        // 全件数の方で頭打ちにする(最後のページで件数がはみ出るのを防ぐ)
        int toIndex = Math.min(offset + limit, all.size());
        return all.subList(offset, toIndex);
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}