package com.reply.library.controller;

import com.reply.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class LibraryController {
    
    @Autowired
    private LibraryService libraryService;
    
    @Operation(summary = "Borrow a book", description = "Allow a member to borrow a book from the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
        @ApiResponse(responseCode = "400", description = "Member not found"),
        @ApiResponse(responseCode = "404", description = "Book not found"), 
        @ApiResponse(responseCode = "409", description = "Book already borrowed")
    })
    @PostMapping("/borrow/{bookId}/member/{memberId}")
    public ResponseEntity<String> borrowBook(
            @Parameter(description = "ID of the book to borrow") @PathVariable Long bookId,
            @Parameter(description = "ID of the member borrowing the book") @PathVariable Long memberId) {
        
        libraryService.borrowBook(bookId, memberId);
        return ResponseEntity.ok("Book borrowed successfully");
    }
    
    @Operation(summary = "Return a book", description = "Return a borrowed book to the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book returned successfully"),
        @ApiResponse(responseCode = "400", description = "Book is not currently borrowed"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(
            @Parameter(description = "ID of the book to return") @PathVariable Long bookId) {
        
        libraryService.returnBook(bookId);
        return ResponseEntity.ok("Book returned successfully");
    }
}