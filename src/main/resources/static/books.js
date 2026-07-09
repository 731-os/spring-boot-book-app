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
    // なぜここでクリアするのか: この関数は「登録後の再表示」でも呼び出すので、
    // 空にしておかないと前回の内容の下に、どんどん重複して追加されてしまう
    listElement.innerHTML = '';
    // 本の数だけ、<li>(リストの1項目)を1つずつ作って一覧に追加していく
    for (const book of books) {
        const li = document.createElement('li');
        li.textContent = `${book.title}(${book.author}, ${book.publishedYear}) ${book.read ? '読了' : '未読'}`;
        listElement.appendChild(li);
    }
}
function setupForm() {
    // as HTMLFormElement: 「これはinput要素と同じく、ただのHTMLElementじゃなくて、
    // .reset()などフォーム専用の機能を持つHTMLFormElementですよ」と明示する
    const form = document.getElementById('book-form');
    if (form === null) {
        console.error('book-form要素が見つかりません');
        return;
    }
    // addEventListener: 「submit(送信)イベントが起きたら、この処理を実行してね」という予約
    form.addEventListener('submit', async (event) => {
        console.log('フォーム送信イベントが発生しました');
        // event.preventDefault(): フォームの本来の動作(ページ全体を再読み込みして
        // サーバーに送信する、という昔ながらの挙動)を止める。
        // これを書かないと、ページがリロードされてfetchの結果が見られなくなる
        event.preventDefault();
        // "as HTMLInputElement": getElementByIdは「ただのHTMLElement」としか
        // 教えてくれないので、「これは値(value)を持つinput要素です」と
        // 明示的に型を教えてあげる必要がある(型アサーションと呼ぶ)
        const titleInput = document.getElementById('title');
        const authorInput = document.getElementById('author');
        const yearInput = document.getElementById('publishedYear');
        const readInput = document.getElementById('read');
        const newBook = {
            title: titleInput.value,
            author: authorInput.value,
            publishedYear: Number(yearInput.value), // 入力値は文字列なので数値に変換
            read: readInput.checked // チェックボックスは.checkedでtrue/falseを取得
        };
        await fetch('/api/books', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }, // 「JSON形式で送りますよ」という送り状
            body: JSON.stringify(newBook) // JavaScriptオブジェクトをJSON文字列に変換
        });
        form.reset(); // 入力欄を空に戻す
        await loadBooks(); // 一覧を再取得して、登録した本を画面に反映する
    });
}
// ページが読み込まれたら、すぐに本の一覧を取得しにいく
loadBooks();
setupForm(); // ページ読み込み時に、フォームの送信イベントを予約しておく
