package com.novaathletics.modules.customer;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.customer.repository.CustomerRepository;
import com.novaathletics.modules.customer.entity.Customer;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/customers")
public class AdminCustomerController {
  private final CustomerRepository repo;
  public AdminCustomerController(CustomerRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('CUSTOMER_READ')") public PageResponse<Customer> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100)); var pg=repo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
  @GetMapping("/{id}") @PreAuthorize("hasAuthority('CUSTOMER_READ')") public ApiResponse<Customer> one(@PathVariable Long id){ return new ApiResponse<>(repo.findById(id).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("CUSTOMER_NOT_FOUND","Not found"))); }
}
