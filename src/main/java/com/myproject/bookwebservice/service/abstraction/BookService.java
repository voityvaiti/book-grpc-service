package com.myproject.bookwebservice.service.abstraction;

import com.myproject.bookwebservice.dto.BookDto;

import java.util.List;
import java.util.UUID;

public interface BookService {

    List<BookDto> getAll();

    BookDto getById(UUID id);

    BookDto create(BookDto bookDto);

    BookDto update(UUID id, BookDto bookDto);

    void delete(UUID id);
}
