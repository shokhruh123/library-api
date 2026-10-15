package com.example.library.controller;

import com.example.library.dto.BookResponse;
import com.example.library.exception.ConflictException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    private static final String VALID_BODY = """
            {"title":"Мастер и Маргарита","author":"Булгаков","year":1967,"isbn":"978-5-17-000001-1","publisher":"АСТ","genre":"Роман"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void create_returns201() throws Exception {
        when(bookService.create(any()))
                .thenReturn(new BookResponse(1L, "Мастер и Маргарита", "Булгаков", 1967,
                        "978-5-17-000001-1", "АСТ", "Роман"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Мастер и Маргарита"));
    }

    @Test
    void create_returns400_whenTitleBlank() throws Exception {
        String body = VALID_BODY.replace("Мастер и Маргарита", "");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Название")));
    }

    @Test
    void create_returns409_whenIsbnExists() throws Exception {
        when(bookService.create(any()))
                .thenThrow(new ConflictException("Книга с ISBN 978-5-17-000001-1 уже есть в справочнике"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        when(bookService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Книга с id 999 не найдена"));

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).delete(1L);
    }
}