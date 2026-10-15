package com.example.library.mapper;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequest request) {
        Book book = new Book();
        applyRequest(book, request);
        return book;
    }

    public void applyRequest(Book book, BookRequest request) {
        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setYear(request.year());
        book.setIsbn(trimOrNull(request.isbn()));
        book.setPublisher(trimOrNull(request.publisher()));
        book.setGenre(trimOrNull(request.genre()));
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getIsbn(),
                book.getPublisher(),
                book.getGenre()
        );
    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}