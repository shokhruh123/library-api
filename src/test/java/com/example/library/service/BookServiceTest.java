package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Book;
import com.example.library.exception.ConflictException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Spy
    private BookMapper bookMapper = new BookMapper();

    @InjectMocks
    private BookService bookService;

    private final BookRequest request = new BookRequest(
            "Мастер и Маргарита", "Булгаков", 1967, "978-5-17-000001-1", "АСТ", "Роман");

    @Test
    void create_savesBookAndReturnsResponse() {
        when(bookRepository.existsByIsbn("978-5-17-000001-1")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        BookResponse response = bookService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Мастер и Маргарита");
    }

    @Test
    void create_throwsConflict_whenIsbnAlreadyExists() {
        when(bookRepository.existsByIsbn("978-5-17-000001-1")).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("978-5-17-000001-1");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void findById_throwsNotFound_whenBookMissing() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}