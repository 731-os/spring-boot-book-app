package com.example.bookapp.service;

import com.example.bookapp.model.Book;
import com.example.bookapp.repository.InMemoryBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceSpyTest {

    // @Spy: InMemoryBookRepositoryの「本物の動き」をベースにする。
    // 何も指示しなければ、本当にMapへの登録・取得が動く
    @Spy
    private InMemoryBookRepository bookRepository = new InMemoryBookRepository();

    @Test
    void create_本物のリポジトリに実際に保存される() {
        BookService bookService = new BookService(bookRepository);
        Book input = new Book(null, "スパイのテスト本", "テスト著者", 2024, false);

        Book created = bookService.create(input);

        // 本物のfindByIdを実際に呼んで、本当に保存されているか確認できる
        // (Mockだったら、ここでnullやエラーになる。Spyだから本物の動きが生きている)
        Book found = bookService.findById(created.getId());
        assertThat(found.getTitle()).isEqualTo("スパイのテスト本");
    }

    @Test
    void delete_本物の削除は動くが呼び出し自体も検証できる() {
        BookService bookService = new BookService(bookRepository);
        // まずSpy越しに本物へ1件保存しておく(本物のMapに実際に入る)
        Book saved = bookService.create(new Book(null, "削除対象", "著者", 2020, false));

        bookService.delete(saved.getId());

        // verify: Spyでも「本当に呼ばれたか」の検証はMockと同じようにできる
        verify(bookRepository).deleteById(saved.getId());
        // 本物の動きも生きているので、実際に消えていることも確認できる
        assertThatThrownBy(() -> bookService.findById(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}