package com.example.bookapp.dto;

import java.util.List;

// <T>: 「型引数」と呼ばれるもの。List<Book>のBookの部分を、
// 自分で自由に決められるようにする仕組み。
// これがあることで、PageResponseは本以外(著者・注文など)のページングにも
// そのまま使い回せる「汎用の入れ物」になる
public class PageResponse<T> {

    private List<T> content;      // このページの中身
    private int page;             // 今何ページ目か(0始まり)
    private int size;             // 1ページ何件か
    private long totalElements;   // 全部で何件あるか
    private int totalPages;       // 全部で何ページあるか

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;

        // Math.ceil: 切り上げ計算。例えば全23件・1ページ10件なら
        // 23 ÷ 10 = 2.3 → 切り上げで3ページ、という計算になる。
        // (double)にキャストしないと、Javaの整数同士の割り算(23/10=2)に
        // なってしまい、小数点以下が消えて切り上げが機能しなくなる
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}