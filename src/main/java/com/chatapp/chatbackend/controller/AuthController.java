package com.chatapp.chatbackend.controller;

import com.chatapp.chatbackend.model.ApiResponse;
import com.chatapp.chatbackend.model.AuthRequest;
import com.chatapp.chatbackend.model.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${security.api.url}")
    private String securityApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        String loginUrl = securityApiUrl + "/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);

        try {
            ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponse<AuthResponse>>() {}
            );

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(400, "Bad Request: " + e.getResponseBodyAsString(), null));
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(401, "Unauthorized: " + e.getResponseBodyAsString(), null));
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(403, "Forbidden: " + e.getResponseBodyAsString(), null));
            } else {
                // Catch-all for other 4xx
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(e.getStatusCode().value(), "Client Error: " + e.getResponseBodyAsString(), null));
            }
        } catch (HttpServerErrorException e) {
            // 5xx error from security API
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Server error: " + e.getMessage(), null));

        } catch (Exception e) {
            // Other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Unexpected error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody AuthRequest signupRequest) {
        String signupUrl = securityApiUrl + "/api/auth/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> request = new HttpEntity<>(signupRequest, headers);

        try {
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    signupUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<ApiResponse<String>>() {}
            );

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(400, "Bad Request: " + e.getResponseBodyAsString(), null));
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(401, "Unauthorized: " + e.getResponseBodyAsString(), null));
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(403, "Forbidden: " + e.getResponseBodyAsString(), null));
            } else {
                // Catch-all for other 4xx
                return ResponseEntity.status(e.getStatusCode())
                        .body(new ApiResponse<>(e.getStatusCode().value(), "Client Error: " + e.getResponseBodyAsString(), null));
            }
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Security API error: " + e.getResponseBodyAsString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Unexpected error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        String validateUrl = securityApiUrl + "/api/auth/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    validateUrl,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<ApiResponse<String>>() {}
            );

            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getStatusCode().value(), "Unauthorized or Invalid Token", null));
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Token validation error: " + e.getResponseBodyAsString(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Unexpected error: " + e.getMessage(), null));
        }
    }
}