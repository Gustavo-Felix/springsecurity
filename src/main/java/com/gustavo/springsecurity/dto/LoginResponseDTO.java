package com.gustavo.springsecurity.dto;

public record LoginResponseDTO(String accessToken, Long expiresIn) {
}
