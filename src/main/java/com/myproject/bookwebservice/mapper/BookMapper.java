package com.myproject.bookwebservice.mapper;

import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.entity.Book;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {

    BookServiceOuterClass.BookResponse mapToBookResponse(Book book);

    Book mapToBook(BookServiceOuterClass.AddBookRequest response);

}
