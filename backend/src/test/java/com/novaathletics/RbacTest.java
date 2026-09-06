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
public class RbacTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  private String customerToken() throws Exception {
    String email="rbac"+System.nanoTime()+"@nova.local";
    var reg=Map.of("email",email,"password","Str0ng!Pass1");
    var res=mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isCreated()).andReturn();
    String body=res.getResponse().getContentAsString();
    return om.readTree(body).path("data").path("accessToken").asText();
  }

  @Test
  void customerCannotAccessAdmin() throws Exception {
    String token=customerToken();
    mvc.perform(get("/api/v1/admin/products").header("Authorization","Bearer "+token)).andExpect(status().isForbidden());
  }

  @Test
  void anonymousCannotAccessCartWithoutTokenButAllowed() throws Exception {
    // cart is auth optional -> should be 200 even without token (guest)
    mvc.perform(get("/api/v1/cart")).andExpect(status().isOk());
  }
}
