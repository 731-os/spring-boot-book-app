package com.example.bookapp.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public Book create(@RequestBody Book book) {
        return bookService.create(book);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody Book book) {
        return bookService.update(id, book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<Book> books = bookService.findAll();

        // StringWriter: メモリ上の「文字列を書き込んでいく先」。
        // ファイルには保存せず、レスポンスとして直接返すのでこれで十分
        StringWriter sw = new StringWriter();

        // try-with-resources: ()の中で作ったものは、
        // ブロックを抜ける時に自動でclose()される仕組み。
        // CSVPrinterは「開いたら閉じる」必要があるリソースなので、
        // 閉じ忘れを防げるこの書き方が定番
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                .setHeader("id", "title", "author", "publishedYear", "read") // 1行目の見出し
                .get())) {

            for (Book book : books) {
                // 1冊分を1行として書き込む。値にカンマや改行が含まれていても、
                // CSVPrinterが自動で適切にダブルクォートで囲んでくれる
                printer.printRecord(book.getId(), book.getTitle(), book.getAuthor(),
                        book.getPublishedYear(), book.isRead());
            }
        } catch (IOException e) {
            // StringWriterへの書き込みでIOExceptionが起きることは通常ないが、
            // メソッドの宣言上catchが必要なため、実行時例外に変換して投げ直す
            throw new RuntimeException("CSV生成に失敗しました", e);
        }

        // UTF-8のBOM(EF BB BF)を先頭に付けてから、本文をバイト列として連結する。
        // これがないと、Excelが文字コードをUTF-8だと認識できず文字化けする
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] body = sw.toString().getBytes(StandardCharsets.UTF_8);
        byte[] csvBytes = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, csvBytes, 0, bom.length);
        System.arraycopy(body, 0, csvBytes, bom.length, body.length);

        return ResponseEntity.ok()
                // Content-Disposition: ブラウザに「これは表示せず、ファイルとして
                // ダウンロードしてね、名前はbooks.csvね」と伝えるヘッダー
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}