package com.myproject.bookwebservice.mapper;

import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.dto.BookDto;
import com.myproject.bookwebservice.entity.Book;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {

    BookDto mapToBookDto(Book book);

    BookDto mapToBookDto(BookServiceOuterClass.AddBookRequest request);

    BookDto mapToBookDto(BookServiceOuterClass.UpdateBookRequest request);

    BookDto mapToBookDto(BookServiceOuterClass.BookResponse response);

    Book mapToBook(BookDto bookDto);

    BookServiceOuterClass.BookResponse mapToBookResponse(BookDto bookDto);


}
