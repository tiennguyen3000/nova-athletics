package com.novaathletics.modules.auth.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min=8,max=64) String password, String firstName, String lastName, String phone){}
