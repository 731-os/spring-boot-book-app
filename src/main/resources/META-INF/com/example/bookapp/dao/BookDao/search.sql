SELECT id, title, author, published_year, read
FROM books
-- 1=1: 常にtrueな条件をダミーで置いておくことで、
-- 下の各条件を全部「AND ○○」の形に統一できる(先頭だけWHERE、以降ANDと
-- 書き分ける面倒がなくなる、DOMAでよく使う定番テクニック)
WHERE 1 = 1
/*%if title != null*/
-- %if/%end: DOMAの「条件によってSQLの一部を出し入れする」構文。
-- titleが渡された時だけ、この行がSQLに含まれる
  AND title LIKE '%' || /*title*/'sample' || '%'
/*%end*/
/*%if author != null*/
  AND author LIKE '%' || /*author*/'sample' || '%'
/*%end*/
/*%if read != null*/
  AND read = /*read*/true
/*%end*/
ORDER BY id