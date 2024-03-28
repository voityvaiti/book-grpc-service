package com.myproject.bookwebservice.grpcService;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.myproject.bookwebservice.BookServiceGrpc;
import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.dto.BookDto;
import com.myproject.bookwebservice.mapper.BookMapper;
import com.myproject.bookwebservice.service.abstraction.BookService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test",
        "grpc.server.port=9092",
        "grpc.client.bookService.address=in-process:test"
})
public class BookGrpcServiceImplTest {

    @MockBean
    private BookService bookService;

    @GrpcClient("bookService")
    private BookServiceGrpc.BookServiceBlockingStub bookServiceBlockingStub;

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);


    private static UUID id;
    private static BookDto testBook;
    private static BookDto invalidTestBook;


    @BeforeAll
    static void setUp() {
        id = UUID.randomUUID();
        testBook = new BookDto(id, "someTitle", "someAuthor", "someIsbn", 512);
        invalidTestBook = new BookDto(null, "", "", "", 512);
    }


    @Test
    void getAllBooks_shouldReturnProperBookResponseList() {

        List<BookDto> expectedBookDtoList = List.of(
                new BookDto(UUID.randomUUID(), "title1", "author1", "isbn1", 1),
                new BookDto(UUID.randomUUID(), "title2", "author2", "isbn2", 2)
                );
        when(bookService.getAll()).thenReturn(expectedBookDtoList);

        BookServiceOuterClass.GetAllBooksRequest request = BookServiceOuterClass.GetAllBooksRequest.newBuilder().build();


        BookServiceOuterClass.GetAllBooksResponse response = bookServiceBlockingStub.getAllBooks(request);


        assertNotNull(response);

        List<BookDto> actualBookDtoList = response.getBooksList().stream().map(bookMapper::mapToBookDto).collect(Collectors.toList());
        assertEquals(expectedBookDtoList, actualBookDtoList);

        verify(bookService).getAll();
    }

    @Test
    void getBookById_shouldReturnProperBookResponse() {

        when(bookService.getById(id)).thenReturn(testBook);

        BookServiceOuterClass.GetBookByIdRequest request = BookServiceOuterClass.GetBookByIdRequest.newBuilder()
                .setId(id.toString())
                .build();


        BookServiceOuterClass.BookResponse response = bookServiceBlockingStub.getBookById(request);

        assertNotNull(response);

        BookDto receivedBook = new BookDto(UUID.fromString(response.getId()), response.getTitle(), response.getAuthor(), response.getIsbn(), response.getQuantity());
        assertEquals(testBook, receivedBook);

        verify(bookService).getById(id);
    }

    @Test
    void addBook_shouldCreateAndReturnBook_ifBookIsValid() {

        BookDto testBook = new BookDto(null, "someTitle", "someAuthor", "someIsbn", 512);
        BookDto createdBook = new BookDto(id, testBook.getTitle(), testBook.getAuthor(), testBook.getIsbn(), testBook.getQuantity());

        when(bookService.create(testBook)).thenReturn(createdBook);

        BookServiceOuterClass.AddBookRequest request = BookServiceOuterClass.AddBookRequest.newBuilder()
                .setTitle(testBook.getTitle())
                .setAuthor(testBook.getAuthor())
                .setIsbn(testBook.getIsbn())
                .setQuantity(testBook.getQuantity())
                .build();


        BookServiceOuterClass.BookResponse response = bookServiceBlockingStub.addBook(request);

        assertNotNull(response);
        assertEquals(createdBook, bookMapper.mapToBookDto(response));

        verify(bookService).create(testBook);
    }

    @Test
    void addBook_shouldNeverCreateBookAndThrowException_ifBookIsInvalid() {

        BookServiceOuterClass.AddBookRequest request = BookServiceOuterClass.AddBookRequest.newBuilder()
                .setTitle(invalidTestBook.getTitle())
                .setAuthor(invalidTestBook.getAuthor())
                .setIsbn(invalidTestBook.getIsbn())
                .setQuantity(invalidTestBook.getQuantity())
                .build();


        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceBlockingStub.addBook(request);
        });

        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());

        verify(bookService, never()).create(any());
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook_ifBookIsValidAndExists() {

        when(bookService.update(id, testBook)).thenReturn(testBook);

        BookServiceOuterClass.UpdateBookRequest request = BookServiceOuterClass.UpdateBookRequest.newBuilder()
                .setId(id.toString())
                .setTitle(testBook.getTitle())
                .setAuthor(testBook.getAuthor())
                .setIsbn(testBook.getIsbn())
                .setQuantity(testBook.getQuantity())
                .build();


        BookServiceOuterClass.BookResponse response = bookServiceBlockingStub.updateBook(request);

        assertNotNull(response);
        assertEquals(testBook, bookMapper.mapToBookDto(response));

        verify(bookService).update(id, testBook);
    }

    @Test
    void updateBook_shouldNeverUpdateBookAndThrowException_ifBookIsInvalid() {

        when(bookService.update(id, invalidTestBook)).thenReturn(invalidTestBook);

        BookServiceOuterClass.UpdateBookRequest request = BookServiceOuterClass.UpdateBookRequest.newBuilder()
                .setId(id.toString())
                .setTitle(invalidTestBook.getTitle())
                .setAuthor(invalidTestBook.getAuthor())
                .setIsbn(invalidTestBook.getIsbn())
                .setQuantity(invalidTestBook.getQuantity())
                .build();


        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceBlockingStub.updateBook(request);
        });

        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());

        verify(bookService, never()).update(any(), any());
    }

    @Test
    void updateBook_shouldThrowException_ifBookDoNotExist() {

        when(bookService.update(id, testBook)).thenReturn(null);

        BookServiceOuterClass.UpdateBookRequest request = BookServiceOuterClass.UpdateBookRequest.newBuilder()
                .setId(id.toString())
                .setTitle(testBook.getTitle())
                .setAuthor(testBook.getAuthor())
                .setIsbn(testBook.getIsbn())
                .setQuantity(testBook.getQuantity())
                .build();


        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceBlockingStub.updateBook(request);
        });

        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
    }

    @Test
    void deleteBook_shouldProperlyDeleteBook() {

        BookServiceOuterClass.DeleteBookRequest request = BookServiceOuterClass.DeleteBookRequest.newBuilder()
                .setId(id.toString())
                .build();

        bookServiceBlockingStub.deleteBook(request);

        verify(bookService).delete(id);
    }

}