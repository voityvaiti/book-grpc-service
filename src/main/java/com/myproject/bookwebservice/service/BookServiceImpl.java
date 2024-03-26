package com.myproject.bookwebservice.service;

import com.myproject.bookwebservice.BookServiceGrpc;
import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.entity.Book;
import com.myproject.bookwebservice.mapper.BookMapper;
import com.myproject.bookwebservice.repository.BookRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    private final Validator validator;


    @Override
    public void getAllBooks(BookServiceOuterClass.GetAllBooksRequest request, StreamObserver<BookServiceOuterClass.GetAllBooksResponse> responseObserver) {
        try {
            List<Book> allBooks = bookRepository.findAll();
            List<BookServiceOuterClass.BookResponse> bookResponses = allBooks.stream()
                    .map(bookMapper::mapToBookResponse)
                    .collect(Collectors.toList());

            BookServiceOuterClass.GetAllBooksResponse response = BookServiceOuterClass.GetAllBooksResponse.newBuilder()
                    .addAllBooks(bookResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Error fetching all books").asRuntimeException());
        }
    }

    @Override
    public void getBookById(BookServiceOuterClass.GetBookByIdRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            Optional<Book> optionalBook = bookRepository.findById(UUID.fromString(request.getId()));

            if(optionalBook.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
                return;
            }

            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(optionalBook.get());

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void addBook(BookServiceOuterClass.AddBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            Book book = bookMapper.mapToBook(request);
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            if (!violations.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(printViolations(violations)).asRuntimeException());
                return;
            }

            Book savedBook = bookRepository.save(book);
            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(savedBook);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void updateBook(BookServiceOuterClass.UpdateBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            Set<ConstraintViolation<Book>> violations = validator.validate(bookMapper.mapToBook(request));

            if (!violations.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(printViolations(violations)).asRuntimeException());
                return;
            }

            UUID bookId = UUID.fromString(request.getId());
            Optional<Book> optionalBookToUpdate = bookRepository.findById(bookId);

            if(optionalBookToUpdate.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
                return;
            }
            Book bookToUpdate = optionalBookToUpdate.get();

            bookToUpdate.setTitle(request.getTitle());
            bookToUpdate.setAuthor(request.getAuthor());
            bookToUpdate.setIsbn(request.getIsbn());
            bookToUpdate.setQuantity(request.getQuantity());

            Book updatedBook = bookRepository.save(bookToUpdate);
            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(updatedBook);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void deleteBook(BookServiceOuterClass.DeleteBookRequest request, StreamObserver<BookServiceOuterClass.DeleteBookResponse> responseObserver) {
        try {
            UUID bookId = UUID.fromString(request.getId());
            Optional<Book> optionalBook = bookRepository.findById(bookId);

            if(optionalBook.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
                return;
            }
            bookRepository.deleteById(bookId);

            responseObserver.onNext(BookServiceOuterClass.DeleteBookResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    private String printViolations(Set<ConstraintViolation<Book>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<Book> violation : violations) {
            sb.append(violation.getMessage())
                    .append(" ");
        }
        return sb.toString();
    }
}
