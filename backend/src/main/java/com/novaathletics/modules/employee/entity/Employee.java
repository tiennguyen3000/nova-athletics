package com.novaathletics.modules.employee.entity;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="employees") public class Employee {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false, unique=true) private Long userId;
  @Column(name="employee_code", nullable=false, unique=true) private String employeeCode;
  private String department; private String position;
  @Column(name="hire_date") private LocalDate hireDate;
  private String status="ACTIVE";
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
  public String getEmployeeCode(){return employeeCode;} public void setEmployeeCode(String v){employeeCode=v;}
  public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
  public String getPosition(){return position;} public void setPosition(String v){position=v;}
  public LocalDate getHireDate(){return hireDate;} public void setHireDate(LocalDate v){hireDate=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
