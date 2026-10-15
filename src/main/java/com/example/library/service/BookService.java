package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Book;
import com.example.library.exception.ConflictException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll(Sort.by("id")).stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    public BookResponse findById(Long id) {
        return bookMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        ensureIsbnFree(request.isbn(), null);
        Book book = bookMapper.toEntity(request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = getOrThrow(id);
        ensureIsbnFree(request.isbn(), id);
        bookMapper.applyRequest(book, request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.delete(getOrThrow(id));
    }

    private Book getOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с id " + id + " не найдена"));
    }

    private void ensureIsbnFree(String isbn, Long ownId) {
        if (isbn == null || isbn.isBlank()) {
            return;
        }
        String clean = isbn.trim();
        boolean taken = ownId == null
                ? bookRepository.existsByIsbn(clean)
                : bookRepository.existsByIsbnAndIdNot(clean, ownId);
        if (taken) {
            throw new ConflictException("Книга с ISBN " + clean + " уже есть в справочнике");
        }
    }
}