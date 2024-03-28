package com.myproject.bookwebservice.service.impl;

import com.myproject.bookwebservice.dto.BookDto;
import com.myproject.bookwebservice.entity.Book;
import com.myproject.bookwebservice.mapper.BookMapper;
import com.myproject.bookwebservice.repository.BookRepository;
import com.myproject.bookwebservice.service.abstraction.BookService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);


    @Override
    public List<BookDto> getAll() {

        List<Book> bookList = bookRepository.findAll();

        return bookList.stream()
                .map(bookMapper::mapToBookDto)
                .toList();
    }

    @Override
    public BookDto getById(UUID id) {

        Book book = bookRepository.findById(id).orElse(null);

        return bookMapper.mapToBookDto(book);
    }

    @Override
    public BookDto create(BookDto bookDto) {

        Book book = bookMapper.mapToBook(bookDto);

        return bookMapper.mapToBookDto(bookRepository.save(book));
    }

    @Override
    public BookDto update(UUID id, BookDto bookDto) {

        Optional<Book> optionalCurrentBook = bookRepository.findById(id);

        if(optionalCurrentBook.isEmpty()) {
            return null;
        }
        Book currentBook = optionalCurrentBook.get();

        currentBook.setTitle(bookDto.getTitle());
        currentBook.setAuthor(bookDto.getAuthor());
        currentBook.setIsbn(bookDto.getIsbn());
        currentBook.setQuantity(bookDto.getQuantity());

        Book updatedBook = bookRepository.save(currentBook);

        return bookMapper.mapToBookDto(updatedBook);
    }

    @Override
    public void delete(UUID id) {
        bookRepository.deleteById(id);
    }
}
