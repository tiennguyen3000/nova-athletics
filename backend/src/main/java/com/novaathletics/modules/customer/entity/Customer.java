package com.novaathletics.modules.customer.entity;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="customers") public class Customer {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false, unique=true) private Long userId;
  @Column(name="first_name") private String firstName;
  @Column(name="last_name") private String lastName;
  private String phone;
  @Column(name="date_of_birth") private LocalDate dateOfBirth;
  private String gender;
  @Column(name="avatar_url") private String avatarUrl;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
  public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;}
  public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
  public String getGender(){return gender;} public void setGender(String v){gender=v;}
  public Instant getCreatedAt(){return createdAt;}
}
