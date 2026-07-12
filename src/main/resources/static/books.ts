// interface: データの「形の設計図」を定義するTypeScript独自の書き方。
// Java学習ノートで作ったBook.javaのフィールドと、意図的に対応させている
interface Book {
    id: number;
    title: string;
    author: string;
    publishedYear: number;
    read: boolean;
}

// async function: 「時間がかかる処理を含む関数」であることを示す目印。
// fetch(通信)は一瞬では終わらないので、この目印が必要になる
async function loadBooks(): Promise<void> {
    // fetch: ブラウザに標準で搭載されている、HTTPリクエストを送る機能。
    // 相対パス「/api/books」を指定(http://localhost:8080/を省略できる。
    // 前にstaticフォルダに置いた理由がここで活きる、同じオリジンだからOK)
    const response = await fetch('/api/books');

    // response.json(): レスポンスのJSON文字列を、JavaScriptのオブジェクトに変換する。
    // Book[](Bookの配列)という型を指定して、この先の操作でTypeScriptに
    // 「books配列の中身はBookの形をしてますよ」と教えておく
    const books: Book[] = await response.json();

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

        // 編集ボタン: クリックされたら、このbookの中身をフォームに詰め替える。
        // 「() => startEdit(book)」と書くことで、bookの中身をそのまま
        // クリック時の処理に持ち込める(クロージャという仕組み)
        const editButton = document.createElement('button');
        editButton.textContent = '編集';
        editButton.addEventListener('click', () => startEdit(book));
        li.appendChild(editButton);

        // 削除ボタン: クリックされたら、このbookのidでDELETEリクエストを送る
        const deleteButton = document.createElement('button');
        deleteButton.textContent = '削除';
        deleteButton.addEventListener('click', () => deleteBook(book.id));
        li.appendChild(deleteButton);

        listElement.appendChild(li);
    }
}
function setupForm(): void {
    // as HTMLFormElement: 「これはinput要素と同じく、ただのHTMLElementじゃなくて、
    // .reset()などフォーム専用の機能を持つHTMLFormElementですよ」と明示する
    const form = document.getElementById('book-form') as HTMLFormElement;
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
        // const titleInput = document.getElementById('title') as HTMLInputElement;
        // const authorInput = document.getElementById('author') as HTMLInputElement;
        // const yearInput = document.getElementById('publishedYear') as HTMLInputElement;
        // const readInput = document.getElementById('read') as HTMLInputElement;
        //
        // const newBook = {
        //     title: titleInput.value,
        //     author: authorInput.value,
        //     publishedYear: Number(yearInput.value), // 入力値は文字列なので数値に変換
        //     read: readInput.checked // チェックボックスは.checkedでtrue/falseを取得
        // };

        const editIdInput = document.getElementById('edit-id') as HTMLInputElement;
        const titleInput = document.getElementById('title') as HTMLInputElement;
        const authorInput = document.getElementById('author') as HTMLInputElement;
        const yearInput = document.getElementById('publishedYear') as HTMLInputElement;
        const readInput = document.getElementById('read') as HTMLInputElement;
        const submitButton = document.getElementById('submit-button') as HTMLButtonElement;

        const bookData = {
            title: titleInput.value,
            author: authorInput.value,
            publishedYear: Number(yearInput.value),
            read: readInput.checked
        };

// editIdInput.valueが空文字でなければ「編集モード」= PUTで更新
        if (editIdInput.value !== '') {
        await fetch('/api/books', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }, // 「JSON形式で送りますよ」という送り状
            body: JSON.stringify(bookData) // JavaScriptオブジェクトをJSON文字列に変換
        });
    } else {
        // 空文字なら「新規登録モード」= POSTで登録
        await fetch('/api/books', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bookData)
        });
    }
        form.reset();
        editIdInput.value = ''; // 編集モードを解除
        submitButton.textContent = '登録'; // ボタン表示を元に戻す
        await loadBooks(); // 一覧を再取得して画面に反映する(1回だけでOK)
});
}

// なぜbook全体(interface Bookの形)を受け取るのか: フォームに詰め替えるために、
// title・author・publishedYear・readの全項目が必要なため
function startEdit(book: Book): void {
    const editIdInput = document.getElementById('edit-id') as HTMLInputElement;
    const titleInput = document.getElementById('title') as HTMLInputElement;
    const authorInput = document.getElementById('author') as HTMLInputElement;
    const yearInput = document.getElementById('publishedYear') as HTMLInputElement;
    const readInput = document.getElementById('read') as HTMLInputElement;
    const submitButton = document.getElementById('submit-button') as HTMLButtonElement;

    // 隠し項目にidを入れておくことで、「今は編集モードである」ことを覚えておく
    editIdInput.value = String(book.id);
    titleInput.value = book.title;
    authorInput.value = book.author;
    yearInput.value = String(book.publishedYear);
    readInput.checked = book.read;

    // なぜボタンの文字を変えるのか: 「登録」のままだと、今から更新しようと
    // してることにユーザーが気づけないため
    submitButton.textContent = '更新';
}

async function deleteBook(id: number): Promise<void> {
    // confirm: ブラウザ標準の確認ダイアログ。OKなら true、キャンセルなら false が返る。
    // なぜ使うのか: うっかりクリックで大事なデータを消してしまわないための安全策
    const ok = confirm('本当に削除しますか?');
    if (!ok) {
        return;
    }

    await fetch(`/api/books/${id}`, { method: 'DELETE' });
    await loadBooks();
}

// ページが読み込まれたら、すぐに本の一覧を取得しにいく
loadBooks();
setupForm(); // ページ読み込み時に、フォームの送信イベントを予約しておく