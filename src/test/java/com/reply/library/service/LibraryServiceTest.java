package com.reply.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void borrowBook_Success() {
        Long bookId = 1L;
        Long memberId = 1L;
        when(memberService.existsById(memberId)).thenReturn(true);
        doNothing().when(bookService).borrowBook(bookId);

        libraryService.borrowBook(bookId, memberId);

        verify(memberService).existsById(memberId);
        verify(bookService).borrowBook(bookId);
    }

    @Test
    void borrowBook_MemberNotFound() {
        Long bookId = 1L;
        Long memberId = 1L;
        when(memberService.existsById(memberId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libraryService.borrowBook(bookId, memberId);
        });
        
        assertEquals("Member not found with id: " + memberId, exception.getMessage());
        verify(memberService).existsById(memberId);
        verify(bookService, never()).borrowBook(anyLong());
    }

    @Test
    void returnBook_Success() {
        // Given
        Long bookId = 1L;
        doNothing().when(bookService).returnBook(bookId);

        // When
        libraryService.returnBook(bookId);

        // Then
        verify(bookService).returnBook(bookId);
    }
}