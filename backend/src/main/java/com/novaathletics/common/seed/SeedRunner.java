package com.novaathletics.common.seed;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;
@Component
public class SeedRunner implements ApplicationRunner {
  private final JdbcTemplate jdbc; private final PasswordEncoder encoder;
  @Value("${SUPER_ADMIN_EMAIL:admin@nova.local}") String adminEmail;
  @Value("${SUPER_ADMIN_PASSWORD:Admin123!}") String adminPass;
  public SeedRunner(JdbcTemplate j, PasswordEncoder e){this.jdbc=j; this.encoder=e;}
  @Override public void run(org.springframework.boot.ApplicationArguments args){
    try{
      // roles & permissions
      Integer roleCnt=jdbc.queryForObject("SELECT COUNT(*) FROM roles", Integer.class);
      if(roleCnt!=null && roleCnt==0){
        jdbc.update("INSERT INTO roles(id,name,description,created_at,updated_at) VALUES(1,'CUSTOMER','Customer', now(), now())");
        jdbc.update("INSERT INTO roles(id,name,description,created_at,updated_at) VALUES(2,'EMPLOYEE','Employee', now(), now())");
        jdbc.update("INSERT INTO roles(id,name,description,created_at,updated_at) VALUES(3,'MANAGER','Manager', now(), now())");
        jdbc.update("INSERT INTO roles(id,name,description,created_at,updated_at) VALUES(4,'ADMIN','Admin', now(), now())");
        jdbc.update("INSERT INTO roles(id,name,description,created_at,updated_at) VALUES(5,'SUPER_ADMIN','Super Admin', now(), now())");
        String[] perms={"PRODUCT_READ","PRODUCT_CREATE","PRODUCT_UPDATE","PRODUCT_DELETE","CATEGORY_MANAGE","COLLECTION_MANAGE","ORDER_READ","ORDER_UPDATE","ORDER_CANCEL","ORDER_REFUND","INVENTORY_READ","INVENTORY_UPDATE","INVENTORY_TRANSFER","CUSTOMER_READ","CUSTOMER_UPDATE","EMPLOYEE_READ","EMPLOYEE_MANAGE","PROMOTION_MANAGE","REVIEW_MANAGE","REPORT_READ","AUDIT_READ"};
        for(int i=0;i<perms.length;i++) jdbc.update("INSERT INTO permissions(id,name,created_at,updated_at) VALUES(?,?, now(), now())", i+1, perms[i]);
        // role_permissions: SUPER_ADMIN all
        for(int i=1;i<=perms.length;i++) jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(5,?)", i);
        // CUSTOMER
        jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(1,1)");
        jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(1,7)");
        // EMPLOYEE
        for(int p: new int[]{1,7,8,11,14}) jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(2,?)", p);
        // MANAGER
        for(int p: new int[]{1,2,3,5,6,7,8,9,10,11,12,13,14,16,18,19,20}) jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(3,?)", p);
        // ADMIN all
        for(int i=1;i<=perms.length;i++) jdbc.update("INSERT INTO role_permissions(role_id,permission_id) VALUES(4,?)", i);
      }
      // super admin user if not exists
      Integer cnt=jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email=?", Integer.class, adminEmail);
      if(cnt!=null && cnt==0){
        String hash=encoder.encode(adminPass);
        jdbc.update("INSERT INTO users(email,password_hash,status,email_verified,created_at,updated_at) VALUES(?,?, 'ACTIVE', true, now(), now())", adminEmail, hash);
        Long uid=jdbc.queryForObject("SELECT id FROM users WHERE email=?", Long.class, adminEmail);
        jdbc.update("INSERT INTO user_roles(user_id,role_id) VALUES(?,?)", uid, 5);
        jdbc.update("INSERT INTO customers(user_id,first_name,last_name,created_at,updated_at) VALUES(?,?,?, now(), now())", uid, "Super", "Admin");
      }
      // warehouses
      Integer whCnt=jdbc.queryForObject("SELECT COUNT(*) FROM warehouses", Integer.class);
      if(whCnt!=null && whCnt==0){
        for(int i=1;i<=3;i++) jdbc.update("INSERT INTO warehouses(code,name,city,created_at,updated_at) VALUES(?,?,?, now(), now())", "WH-0"+i, "Warehouse "+i, "HCM");
      }
      // categories
      Integer catCnt=jdbc.queryForObject("SELECT COUNT(*) FROM categories", Integer.class);
      if(catCnt!=null && catCnt==0){
        jdbc.update("INSERT INTO categories(name,slug,is_active,created_at,updated_at) VALUES('Running','running',true, now(), now())");
        jdbc.update("INSERT INTO categories(name,slug,is_active,created_at,updated_at) VALUES('Basketball','basketball',true, now(), now())");
        jdbc.update("INSERT INTO categories(name,slug,is_active,created_at,updated_at) VALUES('Training','training',true, now(), now())");
      }
      // collections
      Integer colCnt=jdbc.queryForObject("SELECT COUNT(*) FROM collections", Integer.class);
      if(colCnt!=null && colCnt==0){
        jdbc.update("INSERT INTO collections(name,slug,created_at,updated_at) VALUES('New Arrivals','new-arrivals', now(), now())");
        jdbc.update("INSERT INTO collections(name,slug,created_at,updated_at) VALUES('Sale','sale', now(), now())");
      }
      // products small seed
      Integer prodCnt=jdbc.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
      if(prodCnt!=null && prodCnt==0){
        List<Long> whIds=jdbc.queryForList("SELECT id FROM warehouses", Long.class);
        Random rnd=new Random(42);
        String[] genders={"MEN","WOMEN","KIDS","UNISEX"};
        String[] sports={"RUNNING","BASKETBALL","FOOTBALL","TRAINING","LIFESTYLE"};
        String[] colors={"BLACK","WHITE","RED","BLUE"};
        for(int i=1;i<=50;i++){
          String slug="nova-product-"+i;
          String gender=genders[rnd.nextInt(genders.length)];
          String sport=sports[rnd.nextInt(sports.length)];
          BigDecimal base=BigDecimal.valueOf(1500000 + rnd.nextInt(2000000));
          jdbc.update("INSERT INTO products(name,slug,subtitle,description,brand,gender,sport,base_price,sale_price,status,is_featured,created_at,updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?, now(), now())",
            "NOVA Product "+i, slug, "Subtitle "+i, "Description for product "+i, "NOVA", gender, sport, base, null, "ACTIVE", i<=5);
          Long pid=jdbc.queryForObject("SELECT id FROM products WHERE slug=?", Long.class, slug);
          jdbc.update("INSERT INTO product_images(product_id,url,sort_order,is_primary,created_at,updated_at) VALUES(?,?,0,true, now(), now())", pid, "https://picsum.photos/seed/"+i+"/600/600");
          // variants
          for(int v=0; v<4; v++){
            String size=String.valueOf(38 + rnd.nextInt(7));
            String color=colors[rnd.nextInt(colors.length)];
            String sku=String.format("NVA-%05d-%02d-%s-%s", i, v, color.substring(0,3), size);
            jdbc.update("INSERT INTO product_variants(product_id,sku,size,color,color_hex,is_active,created_at,updated_at) VALUES(?,?,?,?,?,true, now(), now())", pid, sku, size, color, "#000000");
            Long vid=jdbc.queryForObject("SELECT id FROM product_variants WHERE sku=?", Long.class, sku);
            for(Long wid: whIds){
              int qty= 20 + rnd.nextInt(30);
              jdbc.update("INSERT INTO warehouse_inventory(variant_id,warehouse_id,quantity_on_hand,quantity_reserved,version,updated_at) VALUES(?,?,?,0,0, now())", vid, wid, qty);
            }
          }
        }
        // coupon
        jdbc.update("INSERT INTO coupons(code,name,discount_type,discount_value,is_active,usage_limit,created_at,updated_at) VALUES('NOVA10','10% off','PERCENTAGE',10,true,1000, now(), now())");
      }
    }catch(Exception e){ e.printStackTrace(); }
  }
}
