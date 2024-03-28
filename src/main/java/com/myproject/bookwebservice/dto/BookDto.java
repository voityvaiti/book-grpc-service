package com.myproject.bookwebservice.dto;

import com.myproject.bookwebservice.validation.annotation.BookAuthor;
import com.myproject.bookwebservice.validation.annotation.BookIsbn;
import com.myproject.bookwebservice.validation.annotation.BookTitle;
import lombok.Data;

import java.util.UUID;

@Data
public class BookDto {

    private UUID id;

    @BookTitle
    private String title;

    @BookAuthor
    private String author;

    @BookIsbn
    private String isbn;
    
    private Integer quantity;

}
