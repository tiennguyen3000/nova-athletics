package com.novaathletics.modules.customer;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.customer.entity.*;
import com.novaathletics.modules.customer.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController @RequestMapping("/api/v1/account")
public class CustomerController {
  private final CustomerRepository custRepo; private final AddressRepository addrRepo;
  public CustomerController(CustomerRepository cr, AddressRepository ar){this.custRepo=cr; this.addrRepo=ar;}
  private Long cid(){ return SecurityUtils.current().orElseThrow(()->new com.novaathletics.common.error.BusinessException("UNAUTHORIZED","Unauthorized",org.springframework.http.HttpStatus.UNAUTHORIZED)).getCustomerId(); }
  @GetMapping("/profile") public ApiResponse<Customer> profile(){ Long c=cid(); return new ApiResponse<>(custRepo.findByUserId(SecurityUtils.current().get().getUserId()).orElseThrow()); }
  @PutMapping("/profile") public ApiResponse<Customer> update(@RequestBody Map<String,Object> body){ var cust=custRepo.findByUserId(SecurityUtils.current().get().getUserId()).orElseThrow(); if(body.get("firstName")!=null) cust.setFirstName(body.get("firstName").toString()); if(body.get("lastName")!=null) cust.setLastName(body.get("lastName").toString()); if(body.get("phone")!=null) cust.setPhone(body.get("phone").toString()); return new ApiResponse<>(custRepo.save(cust)); }
  @GetMapping("/addresses") public ApiResponse<List<Address>> addrs(){ return new ApiResponse<>(addrRepo.findByCustomerId(cid())); }
  @PostMapping("/addresses") public ApiResponse<Address> create(@RequestBody Address a){ a.setCustomerId(cid()); return new ApiResponse<>(addrRepo.save(a)); }
  @PutMapping("/addresses/{id}") public ApiResponse<Address> upd(@PathVariable Long id,@RequestBody Address a){ var e=addrRepo.findById(id).orElseThrow(); if(!e.getCustomerId().equals(cid())) throw new com.novaathletics.common.error.BusinessException("FORBIDDEN","Forbidden",org.springframework.http.HttpStatus.FORBIDDEN); e.setLine1(a.getLine1()); e.setCity(a.getCity()); return new ApiResponse<>(addrRepo.save(e)); }
  @DeleteMapping("/addresses/{id}") public ApiResponse<Map<String,String>> del(@PathVariable Long id){ var e=addrRepo.findById(id).orElseThrow(); if(!e.getCustomerId().equals(cid())) throw new com.novaathletics.common.error.BusinessException("FORBIDDEN","Forbidden",org.springframework.http.HttpStatus.FORBIDDEN); addrRepo.delete(e); return new ApiResponse<>(Map.of("message","deleted")); }
}
