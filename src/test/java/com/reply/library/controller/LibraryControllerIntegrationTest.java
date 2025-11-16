package com.reply.library.controller;

import com.reply.library.dto.BookDTO;
import com.reply.library.dto.MemberDTO;
import com.reply.library.service.BookService;
import com.reply.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class LibraryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    private BookDTO testBook;
    private MemberDTO testMember;
    private Long bookId;
    private Long memberId;

    @BeforeEach
    void setUp() {
        testBook = new BookDTO("Test Book", "Test Author", "123456789");
        testMember = new MemberDTO("John Doe", "john@example.com");
        
        // Create test entities
        BookDTO createdBook = bookService.createBook(testBook);
        MemberDTO createdMember = memberService.createMember(testMember);
        
        bookId = createdBook.getId();
        memberId = createdMember.getId();
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void borrowBook_Success() throws Exception {
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, memberId))
                .andExpect(status().isOk())
                .andExpect(content().string("Book borrowed successfully"));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void borrowBook_MemberNotFound() throws Exception {
        Long nonExistentMemberId = 999L;
        
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, nonExistentMemberId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Member not found with id: " + nonExistentMemberId));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void borrowBook_BookNotFound() throws Exception {
        Long nonExistentBookId = 999L;
        
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", nonExistentBookId, memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: " + nonExistentBookId));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void borrowBook_AlreadyBorrowed() throws Exception {
        // First, borrow the book
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, memberId))
                .andExpect(status().isOk());

        // Try to borrow the same book again
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, memberId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Book is already borrowed"));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void returnBook_Success() throws Exception {
        // First, borrow the book
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, memberId))
                .andExpect(status().isOk());

        // Then return it
        mockMvc.perform(post("/api/return/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string("Book returned successfully"));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void returnBook_NotCurrentlyBorrowed() throws Exception {
        mockMvc.perform(post("/api/return/{bookId}", bookId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Book is not currently borrowed"));
    }

    @Test
    @WithMockUser(username = "user", password = "password")
    void returnBook_BookNotFound() throws Exception {
        Long nonExistentBookId = 999L;
        
        mockMvc.perform(post("/api/return/{bookId}", nonExistentBookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: " + nonExistentBookId));
    }

    @Test
    void borrowBook_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/borrow/{bookId}/member/{memberId}", bookId, memberId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnBook_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/return/{bookId}", bookId))
                .andExpect(status().isUnauthorized());
    }
}