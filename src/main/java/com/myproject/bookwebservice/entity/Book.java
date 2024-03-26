package com.myproject.bookwebservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title field size can't be bigger than 100.")
    private String title;

    @Column(name = "author")
    @NotBlank(message = "Author is required.")
    @Size(max = 100, message = "Author field size can't be bigger than 100.")
    private String author;

    @Column(name = "isbn")
    @NotBlank(message = "ISBN is required.")
    @Size(max = 13, message = "ISBN field size can't be bigger than 13.")
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
