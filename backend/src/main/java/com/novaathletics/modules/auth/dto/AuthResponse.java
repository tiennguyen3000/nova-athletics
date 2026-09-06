package com.novaathletics.modules.auth.dto;
public record AuthResponse(Long id,String email,Long customerId,String accessToken,String refreshToken,long expiresIn){}
