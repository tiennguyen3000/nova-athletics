package com.novaathletics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class AuthIntegrationTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Test
  void registerAndLogin() throws Exception {
    String email = "test"+System.nanoTime()+"@nova.local";
    var reg = Map.of("email", email, "password", "Str0ng!Pass1", "firstName", "Test", "lastName", "User");
    mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg)))
      .andExpect(status().isCreated()).andExpect(jsonPath("$.data.accessToken").exists());

    var login = Map.of("email", email, "password", "Str0ng!Pass1");
    mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(login)))
      .andExpect(status().isOk()).andExpect(jsonPath("$.data.accessToken").exists());

    // duplicate email -> 409
    mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg)))
      .andExpect(status().isConflict());

    // wrong password -> 401
    var bad = Map.of("email", email, "password", "wrong");
    mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(bad)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void meRequiresAuth() throws Exception {
    mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
  }
}
