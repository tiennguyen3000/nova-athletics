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
public class ProductCartTest {
  @Autowired MockMvc mvc; @Autowired ObjectMapper om;

  @Test
  void productListAndDetail() throws Exception {
    mvc.perform(get("/api/v1/products").param("page","0").param("size","5")).andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
    var res=mvc.perform(get("/api/v1/products").param("size","1")).andExpect(status().isOk()).andReturn();
    String slug=om.readTree(res.getResponse().getContentAsString()).path("data").path(0).path("slug").asText();
    if(slug!=null && !slug.isBlank()){
      mvc.perform(get("/api/v1/products/"+slug)).andExpect(status().isOk());
    }
  }

  @Test
  void cartRequiresInventoryValidation() throws Exception {
    // add non-existent variant -> 404
    String email="cart"+System.nanoTime()+"@nova.local";
    var reg=Map.of("email",email,"password","Str0ng!Pass1");
    var r=mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isCreated()).andReturn();
    String token=om.readTree(r.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    mvc.perform(post("/api/v1/cart/items").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Map.of("variantId",999999,"quantity",1))))
      .andExpect(status().isNotFound());
  }
}
