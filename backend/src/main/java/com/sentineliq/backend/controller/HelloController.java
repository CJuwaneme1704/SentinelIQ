package com.sentineliq.backend.controller;

//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.sentineliq.backend.config.JwtUtil;

@RestController
public class HelloController {

   // private final JwtUtil jwtUtil;

    // public HelloController(JwtUtil jwtUtil) {
    //     this.jwtUtil = jwtUtil;
    // }



    @GetMapping("/hello")
    public String sayHello() {
        return "Hey Jerome 👑";
    }

    // // TEMPORARY TEST ENDPOINT
    // @GetMapping("/api/test-jwt")
    // public ResponseEntity<String> testJwt() {
    //     Long sampleUserId = 123L; // Simulate a user ID from DB
    //     String token = jwtUtil.generateToken(sampleUserId);
    //     return ResponseEntity.ok(token); // Should return a JWT in the browser or Postman
    // }




}
