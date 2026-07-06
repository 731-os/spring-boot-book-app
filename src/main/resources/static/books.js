"use strict";
// async function: 「時間がかかる処理を含む関数」であることを示す目印。
// fetch(通信)は一瞬では終わらないので、この目印が必要になる
async function loadBooks() {
    // fetch: ブラウザに標準で搭載されている、HTTPリクエストを送る機能。
    // 相対パス「/api/books」を指定(http://localhost:8080/を省略できる。
    // 前にstaticフォルダに置いた理由がここで活きる、同じオリジンだからOK)
    const response = await fetch('/api/books');
    // response.json(): レスポンスのJSON文字列を、JavaScriptのオブジェクトに変換する。
    // Book[](Bookの配列)という型を指定して、この先の操作でTypeScriptに
    // 「books配列の中身はBookの形をしてますよ」と教えておく
    const books = await response.json();
    // HTMLの中から、id="book-list"の要素(さっきindex.htmlに用意した<ul>)を取得
    const listElement = document.getElementById('book-list');
    // なぜnullチェックが必要なのか: getElementByIdは「見つかるかもしれないし、
    // 見つからないかもしれない」という型を返すため。もしIDのタイプミスなどで
    // 要素が見つからなかった場合に備えて、ここで安全に処理を止めておく
    if (listElement === null) {
        console.error('book-list要素が見つかりません');
        return;
    }
    // 本の数だけ、<li>(リストの1項目)を1つずつ作って一覧に追加していく
    for (const book of books) {
        const li = document.createElement('li');
        li.textContent = `${book.title}(${book.author}, ${book.publishedYear}) ${book.read ? '読了' : '未読'}`;
        listElement.appendChild(li);
    }
}
// ページが読み込まれたら、すぐに本の一覧を取得しにいく
loadBooks();
