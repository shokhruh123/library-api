package com.example.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BookRequest(
        @NotBlank(message = "Название не может быть пустым") String title,
        @NotBlank(message = "Автор не может быть пустым") String author,
        @Min(value = 1400, message = "Год должен быть не раньше 1400")
        @Max(value = 2100, message = "Год должен быть не позже 2100")
        Integer year,
        @Pattern(regexp = "^[0-9-]{10,17}$", message = "Некорректный ISBN")
        String isbn,
        String publisher,
        String genre
) {
}