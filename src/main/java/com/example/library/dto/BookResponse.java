package com.example.library.dto;

public record BookResponse(
        Long id,
        String title,
        String author,
        Integer year,
        String isbn,
        String publisher,
        String genre
) {
}