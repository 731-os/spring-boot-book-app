package com.example.bookapp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookService {

    private final BookRepository bookRepository;
    // このServiceが出すログの目印。BookService.classを渡すことで、
    // ログに「どのクラスから出たログか」が自動で表示されるようになる
    private static final Logger log = LoggerFactory.getLogger(BookService.class);


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

    public List<Book> search(String title, String author, Boolean read) {
        // 検索条件を渡しただけの薄いメソッドだが、ここに「検索」という
        // 業務上の操作名を置いておくことで、Controllerからは
        // Repositoryの存在を意識せず呼び出せる(層の役割分担を保つ)
        return bookRepository.search(title, author, read);
    }

    public Book create(Book book) {
        book.setId(null); // 新規登録なのでidは自動採番に任せる

        // bookRepository.save()の戻り値(自動採番されたidを含むBook)を
        // savedという変数で受け取っておく。ここで受け取らないと、
        // 後のログ出力やreturnで参照できなくなる
        Book saved = bookRepository.save(book);

        // なぜ{}を使うのか: ログレベルがオフの時、文字列組み立て自体を
        // スキップできるため(パフォーマンス上の理由)
        log.info("本を登録しました: id={}, title={}", saved.getId(), saved.getTitle());

        return saved; // 最後に、まとめて返す
    }

    public Book update(Long id, Book book) {
        Book existing = findById(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setPublishedYear(book.getPublishedYear());
        existing.setRead(book.isRead());
        Book updated = bookRepository.save(existing);

        // 更新も登録と同じくINFOレベルで記録。「何が」「どう変わったか」が
        // 追えるように、idとtitleを埋め込んでおく
        log.info("本を更新しました: id={}, title={}", updated.getId(), updated.getTitle());
        return updated;
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);

        // 削除は戻り値がないので、idだけでも記録しておく。
        // なぜかというと、後で「あの本、誰がいつ消したんだっけ」を
        // 調査したい時に、この1行がないと痕跡が残らないため
        log.info("本を削除しました: id={}", id);
    }
}