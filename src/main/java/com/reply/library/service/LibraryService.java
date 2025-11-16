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
        if (!memberService.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        
        bookService.borrowBook(bookId);
    }
    
    public void returnBook(Long bookId) {
        bookService.returnBook(bookId);
    }
}