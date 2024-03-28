package com.myproject.bookwebservice.entity;

import com.myproject.bookwebservice.validation.annotation.BookAuthor;
import com.myproject.bookwebservice.validation.annotation.BookIsbn;
import com.myproject.bookwebservice.validation.annotation.BookTitle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "book")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    @BookTitle
    private String title;

    @Column(name = "author")
    @BookAuthor
    private String author;

    @Column(name = "isbn")
    @BookIsbn
    private String isbn;

    @Column(name = "quantity")
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn) && Objects.equals(quantity, book.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, isbn, quantity);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
