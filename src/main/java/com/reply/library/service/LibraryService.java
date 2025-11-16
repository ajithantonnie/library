package com.reply.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryService {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private MemberService memberService;
    
    public void borrowBook(Long bookId, Long memberId) {
        // Rule 2: Check if member exists
        if (!memberService.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        
        // Rule 1 & 3: Check if book exists and is not borrowed
        // This will be handled in bookService.borrowBook
        bookService.borrowBook(bookId);
    }
    
    public void returnBook(Long bookId) {
        // Rule 1 & 2: Check if book exists and is currently borrowed
        // This will be handled in bookService.returnBook
        bookService.returnBook(bookId);
    }
}