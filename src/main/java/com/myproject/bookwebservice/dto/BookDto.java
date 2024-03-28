package com.myproject.bookwebservice.dto;

import com.myproject.bookwebservice.validation.annotation.BookAuthor;
import com.myproject.bookwebservice.validation.annotation.BookIsbn;
import com.myproject.bookwebservice.validation.annotation.BookTitle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
