package com.example.bookapp.dao;

import java.util.List;
import java.util.Optional;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;
import org.seasar.doma.Delete;
import org.seasar.doma.boot.ConfigAutowireable;
import com.example.bookapp.model.Book;

@ConfigAutowireable
@Dao
public interface BookDao {

    @Select
    List<Book> selectAll();

    @Select
    Optional<Book> selectById(Long id);

    @Select
    List<Book> search(String title, String author, Boolean read);

    @Select
    List<Book> selectPage(int limit, int offset);

    @Select
    long count();

    @Insert
    int insert(Book book);

    @Update
    int update(Book book);

    @Delete
    int delete(Book book);
}