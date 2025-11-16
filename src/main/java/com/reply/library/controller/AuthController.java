package com.reply.library.controller;

import com.reply.library.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "JWT Token generation for demonstration purposes")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Operation(summary = "Generate JWT Token", 
               description = "Generate a JWT token for demonstration purposes. In production, this would be handled by an OAuth2 authorization server.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generateToken(@RequestBody TokenRequest request) {
        String token = jwtUtil.generateToken(request.getUsername(), "USER");
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("expires_in", "86400");
        
        return ResponseEntity.ok(response);
    }
    
    public static class TokenRequest {
        private String username;
        
        public TokenRequest() {}
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}