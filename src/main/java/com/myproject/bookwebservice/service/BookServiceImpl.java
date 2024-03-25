package com.myproject.bookwebservice.service;

import com.myproject.bookwebservice.BookServiceGrpc;
import com.myproject.bookwebservice.BookServiceOuterClass;
import com.myproject.bookwebservice.entity.Book;
import com.myproject.bookwebservice.mapper.BookMapper;
import com.myproject.bookwebservice.repository.BookRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.mapstruct.factory.Mappers;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);


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
            Book book = bookRepository.findById(UUID.fromString(request.getId()))
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(book);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
        }
    }

    @Override
    public void addBook(BookServiceOuterClass.AddBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {

        Book book = bookRepository.save(bookMapper.mapToBook(request));

        BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(book);

        responseObserver.onNext(bookResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(BookServiceOuterClass.UpdateBookRequest request, StreamObserver<BookServiceOuterClass.BookResponse> responseObserver) {
        try {
            UUID bookId = UUID.fromString(request.getId());
            Book bookToUpdate = bookRepository.findById(bookId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            bookToUpdate.setTitle(request.getTitle());
            bookToUpdate.setAuthor(request.getAuthor());
            bookToUpdate.setIsbn(request.getIsbn());
            bookToUpdate.setQuantity(request.getQuantity());

            Book updatedBook = bookRepository.save(bookToUpdate);
            BookServiceOuterClass.BookResponse bookResponse = bookMapper.mapToBookResponse(updatedBook);

            responseObserver.onNext(bookResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
        }
    }

    @Override
    public void deleteBook(BookServiceOuterClass.DeleteBookRequest request, StreamObserver<BookServiceOuterClass.DeleteBookResponse> responseObserver) {
        try {
            UUID bookId = UUID.fromString(request.getId());
            bookRepository.deleteById(bookId);

            responseObserver.onNext(BookServiceOuterClass.DeleteBookResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
        }
    }
}
