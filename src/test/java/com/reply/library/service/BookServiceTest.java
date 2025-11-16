package com.reply.library.service;

import com.reply.library.dto.BookDTO;
import com.reply.library.entity.Book;
import com.reply.library.exception.ResourceNotFoundException;
import com.reply.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Title", "Test Author", "123456789");
        testBook.setId(1L);
        
        testBookDTO = new BookDTO("Test Title", "Test Author", "123456789");
        testBookDTO.setId(1L);
    }

    @Test
    void createBook_Success() {
        // Given
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        BookDTO result = bookService.createBook(testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        assertEquals(testBookDTO.getAuthor(), result.getAuthor());
        assertEquals(testBookDTO.getIsbn(), result.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getAllBooks_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.getAllBooks(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testBook.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void getBookById_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        BookDTO result = bookService.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_NotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(1L);
        });
        verify(bookRepository).findById(1L);
    }

    @Test
    void updateBook_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        BookDTO result = bookService.updateBook(1L, testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(1L, testBookDTO);
        });
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_Success() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(1L);
        });
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void borrowBook_Success() {
        // Given
        testBook.setBorrowed(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        bookService.borrowBook(1L);

        // Then
        assertTrue(testBook.isBorrowed());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    void borrowBook_AlreadyBorrowed() {
        // Given
        testBook.setBorrowed(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            bookService.borrowBook(1L);
        });
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void borrowBook_NotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.borrowBook(1L);
        });
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_Success() {
        // Given
        testBook.setBorrowed(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        bookService.returnBook(1L);

        // Then
        assertFalse(testBook.isBorrowed());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    void returnBook_NotCurrentlyBorrowed() {
        // Given
        testBook.setBorrowed(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            bookService.returnBook(1L);
        });
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_NotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.returnBook(1L);
        });
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }
}