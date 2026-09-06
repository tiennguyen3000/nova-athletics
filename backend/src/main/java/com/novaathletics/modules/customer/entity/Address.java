package com.novaathletics.modules.customer.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="addresses") public class Address {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="customer_id", nullable=false) private Long customerId;
  private String label;
  @Column(name="recipient_name", nullable=false) private String recipientName;
  @Column(nullable=false) private String phone;
  @Column(nullable=false) private String line1;
  private String line2; private String ward; private String district;
  @Column(nullable=false) private String city;
  private String country="VN";
  @Column(name="is_default") private Boolean isDefault=false;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
  public String getLabel(){return label;} public void setLabel(String v){label=v;}
  public String getRecipientName(){return recipientName;} public void setRecipientName(String v){recipientName=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getLine1(){return line1;} public void setLine1(String v){line1=v;}
  public String getLine2(){return line2;} public void setLine2(String v){line2=v;}
  public String getWard(){return ward;} public void setWard(String v){ward=v;}
  public String getDistrict(){return district;} public void setDistrict(String v){district=v;}
  public String getCity(){return city;} public void setCity(String v){city=v;}
  public String getCountry(){return country;} public void setCountry(String v){country=v;}
  public Boolean getIsDefault(){return isDefault;} public void setIsDefault(Boolean v){isDefault=v;}
}
