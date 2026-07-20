package com.example.bookapp.service;

import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): 「このテストクラスではMockitoを使うよ」という
// JUnit5への目印。これが無いと@Mockが機能しない
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // @Mock: 本物のBookRepositoryの代わりになる「偽物」を自動生成してもらう。
    // 中身は空っぽで、何も命令しなければ何もしない(呼ばれたことだけ記録する)
    @Mock
    private BookRepository bookRepository;

    // 本物のBookService。ただしコンストラクタに渡すBookRepositoryは、
    // 上のモックが自動的に注入される(@InjectMocksの役割)
    private BookService bookService;

    // 各テストの前に、モックを手動で組み立てたBookServiceに差し込む
    // (Mockitoの@InjectMocksを使わず、コンストラクタインジェクションをそのまま使う書き方)
    private BookService createService() {
        return new BookService(bookRepository);
    }

    @Test
    void create_新規登録時にidがnullになる() {
        bookService = createService();
        Book input = new Book(999L, "テスト本", "テスト著者", 2020, false);

        // when(...).thenReturn(...): 「bookRepository.save()が呼ばれたら、
        // このBookをそのまま返してね」という偽物への指示(スタブと呼ぶ)
        when(bookRepository.save(any(Book.class))).thenReturn(input);

        bookService.create(input);

        // verify: 「本当にこの引数でsaveが呼ばれたか」を確認する。
        // ArgumentCaptorを使って、実際に渡されたBookの中身を検証する
        verify(bookRepository).save(argThat(book -> book.getId() == null));
    }

    @Test
    void findById_存在するidなら本を返す() {
        bookService = createService();
        Book book = new Book(1L, "リーダブルコード", "Dustin Boswell", 2012, true);

        // Optional.of: 「見つかった」状態を表すOptional
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findById(1L);

        // assertThat: 「結果はこうであるべき」という検証。isEqualTo等で比較する
        assertThat(result.getTitle()).isEqualTo("リーダブルコード");
    }

    @Test
    void findById_存在しないidなら例外を投げる() {
        bookService = createService();

        // Optional.empty: 「見つからなかった」状態を表すOptional
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // assertThatThrownBy: 「この処理を実行したら、例外が投げられるはず」を検証する
        assertThatThrownBy(() -> bookService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("本が見つかりません");
    }

    @Test
    void update_既存の内容を書き換えて保存する() {
        bookService = createService();
        Book existing = new Book(1L, "旧タイトル", "旧著者", 2000, false);
        Book newContent = new Book(null, "新タイトル", "新著者", 2024, true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.update(1L, newContent);

        assertThat(result.getTitle()).isEqualTo("新タイトル");
        assertThat(result.isRead()).isTrue();
    }

    @Test
    void delete_指定したidでリポジトリの削除を呼ぶ() {
        bookService = createService();

        bookService.delete(5L);

        // 戻り値がないメソッドは、assertThatで結果を確認できないので、
        // 代わりに「正しい引数で、ちゃんと呼ばれたか」をverifyで確認する
        verify(bookRepository).deleteById(5L);
    }

    @Test
    void search_リポジトリの検索結果をそのまま返す() {
        bookService = createService();
        List<Book> expected = List.of(
                new Book(1L, "Effective Java", "Joshua Bloch", 2018, false)
        );
        when(bookRepository.search("Java", null, null)).thenReturn(expected);

        List<Book> result = bookService.search("Java", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Effective Java");
    }
}