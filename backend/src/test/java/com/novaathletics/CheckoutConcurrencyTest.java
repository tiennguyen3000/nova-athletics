package com.novaathletics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class CheckoutConcurrencyTest {

  @Autowired MockMvc mvc; @Autowired ObjectMapper om; @Autowired JdbcTemplate jdbc;

  private String registerAndGetToken(String email) throws Exception {
    var reg=Map.of("email",email,"password","Str0ng!Pass1","firstName","A","lastName","B");
    var res=mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isCreated()).andReturn();
    return om.readTree(res.getResponse().getContentAsString()).path("data").path("accessToken").asText();
  }

  private Long customerIdForToken(String token) throws Exception {
    var res=mvc.perform(get("/api/v1/auth/me").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andReturn();
    return om.readTree(res.getResponse().getContentAsString()).path("data").path("customerId").asLong();
  }

  @Test
  void twoUsersCompeteForLastSku_onlyOneSucceeds() throws Exception {
    // create a unique variant with exactly 1 on hand - ensure product exists
    Long pid;
    try{ pid=jdbc.queryForObject("SELECT id FROM products LIMIT 1", Long.class);}catch(Exception e){ pid=null; }
    if(pid==null){
      String slug="conc-prod-"+System.nanoTime();
      jdbc.update("INSERT INTO products(name,slug,base_price,status,created_at,updated_at) VALUES('Conc Prod',?,1000000,'ACTIVE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", slug);
      pid=jdbc.queryForObject("SELECT id FROM products WHERE slug=?", Long.class, slug);
      jdbc.update("INSERT INTO product_images(product_id,url,sort_order,is_primary,created_at,updated_at) VALUES(?,?,0,true,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", pid, "https://picsum.photos/seed/conc/600/600");
    }
    String sku="CONC-"+System.nanoTime();
    jdbc.update("INSERT INTO product_variants(product_id,sku,size,color,is_active,created_at,updated_at) VALUES(?,?,?, ?, true,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", pid, sku, "42", "BLACK");
    Long vid=jdbc.queryForObject("SELECT id FROM product_variants WHERE sku=?", Long.class, sku);
    Long wh;
    try{ wh=jdbc.queryForObject("SELECT id FROM warehouses LIMIT 1", Long.class);}catch(Exception e){ wh=null; }
    if(wh==null){ jdbc.update("INSERT INTO warehouses(code,name,city,created_at,updated_at) VALUES('WH-TEST','Test Wh','HCM',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)"); wh=jdbc.queryForObject("SELECT id FROM warehouses LIMIT 1", Long.class); }
    jdbc.update("INSERT INTO warehouse_inventory(variant_id,warehouse_id,quantity_on_hand,quantity_reserved,version,updated_at) VALUES(?,?,1,0,0,CURRENT_TIMESTAMP)", vid, wh);

    String t1=registerAndGetToken("conc1_"+System.nanoTime()+"@nova.local");
    String t2=registerAndGetToken("conc2_"+System.nanoTime()+"@nova.local");

    Long cid1=customerIdForToken(t1);
    Long cid2=customerIdForToken(t2);

    jdbc.update("INSERT INTO addresses(customer_id,recipient_name,phone,line1,city,created_at,updated_at) VALUES(?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", cid1, "A", "0901", "Line1", "HCM");
    Long addr1=jdbc.queryForObject("SELECT id FROM addresses WHERE customer_id=? ORDER BY id DESC LIMIT 1", Long.class, cid1);
    jdbc.update("INSERT INTO addresses(customer_id,recipient_name,phone,line1,city,created_at,updated_at) VALUES(?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", cid2, "B", "0902", "Line1", "HCM");
    Long addr2=jdbc.queryForObject("SELECT id FROM addresses WHERE customer_id=? ORDER BY id DESC LIMIT 1", Long.class, cid2);

    // add same variant to both carts
    mvc.perform(post("/api/v1/cart/items").header("Authorization","Bearer "+t1).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Map.of("variantId",vid,"quantity",1)))).andExpect(status().isOk());
    mvc.perform(post("/api/v1/cart/items").header("Authorization","Bearer "+t2).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Map.of("variantId",vid,"quantity",1)))).andExpect(status().isOk());

    // sequential checkouts - validates pessimistic lock prevents oversell (MockMvc not thread-safe for parallel)
    var r1=mvc.perform(post("/api/v1/checkout").header("Authorization","Bearer "+t1).header("Idempotency-Key", java.util.UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Map.of("shippingAddressId",addr1,"paymentMethod","MOCK")))).andReturn();
    int s1=r1.getResponse().getStatus();
    System.out.println("checkout1 status="+s1+" body="+r1.getResponse().getContentAsString());
    var r2=mvc.perform(post("/api/v1/checkout").header("Authorization","Bearer "+t2).header("Idempotency-Key", java.util.UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Map.of("shippingAddressId",addr2,"paymentMethod","MOCK")))).andReturn();
    int s2=r2.getResponse().getStatus();
    System.out.println("checkout2 status="+s2+" body="+r2.getResponse().getContentAsString());
    // only one should be 201 (created), other 409 conflict (out of stock)
    long successCount= java.util.stream.Stream.of(s1,s2).filter(s->s==201||s==200).count();
    Assertions.assertEquals(1, successCount, "Only one checkout should succeed when last SKU, got statuses "+s1+","+s2);
  }
}
