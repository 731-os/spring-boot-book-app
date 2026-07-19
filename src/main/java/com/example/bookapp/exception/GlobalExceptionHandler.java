package com.example.bookapp.exception;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice: すべてのControllerを対象にした「例外の集約窓口」であることの目印。
// BookController以外にControllerが増えても、この1クラスだけで対応できる
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @ExceptionHandler: 「IllegalArgumentExceptionが投げられたら、このメソッドを呼んでね」という指定。
    // BookService.findByIdの中で投げていた例外が、ここでキャッチされる
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // なぜここでログを出さないのか: BookService.findById側で既にWARNログを
        // 出しているため、ここでも出すと同じ出来事が2回記録されてしまう

        // レスポンスの中身(本文)を、分かりやすいメッセージ入りのMapとして組み立てる
        Map<String, String> body = Map.of("error", e.getMessage());

        // ResponseEntityで、ステータスコード(404)と本文を両方まとめて指定する
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        // getFieldErrors(): 「どの項目が」「どんなメッセージで」エラーになったか、
        // 複数まとめて取得できる。1回のリクエストで複数項目がエラーでも、
        // 一度に全部の情報を返せるようにするため
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}