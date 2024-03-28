package com.myproject.bookwebservice.grpcService;

import com.myproject.bookwebservice.BookServiceGrpc;
import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.dto.BookDto;
import com.myproject.bookwebservice.mapper.BookMapper;
import com.myproject.bookwebservice.service.abstraction.BookService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class BookGrpcServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    private final Validator validator;


    @Override
    public void getAllBooks(BookServiceOuterClass.GetAllBooksRequest request, StreamObserver<BookServiceOuterClass.GetAllBooksResponse> responseObserver) {
        try {
            List<BookDto> allBooks = bookService.getAll();
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

            BookDto bookDto = bookService.getById(UUID.fromString(request.getId()));

            if(bookDto == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
                return;
            }

            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(bookDto);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void addBook(BookServiceOuterClass.AddBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            BookDto bookDto = bookMapper.mapToBookDto(request);
            Set<ConstraintViolation<BookDto>> violations = validator.validate(bookDto);

            if (!violations.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(printViolations(violations)).asRuntimeException());
                return;
            }

            BookDto createdBook = bookService.create(bookDto);
            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(createdBook);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void updateBook(BookServiceOuterClass.UpdateBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            Set<ConstraintViolation<BookDto>> violations = validator.validate(bookMapper.mapToBookDto(request));

            if (!violations.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(printViolations(violations)).asRuntimeException());
                return;
            }

            UUID bookId = UUID.fromString(request.getId());
            BookDto updatedBook = bookService.update(bookId, bookMapper.mapToBookDto(request));

            if(updatedBook == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
                return;
            }
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
            bookService.delete(bookId);

            responseObserver.onNext(BookServiceOuterClass.DeleteBookResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    private String printViolations(Set<ConstraintViolation<BookDto>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<BookDto> violation : violations) {
            sb.append(violation.getMessage())
                    .append(" ");
        }
        return sb.toString();
    }
}
