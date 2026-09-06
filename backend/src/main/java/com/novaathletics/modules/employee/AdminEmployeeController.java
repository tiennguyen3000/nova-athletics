package com.novaathletics.modules.employee;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.employee.entity.Employee;
import com.novaathletics.modules.employee.repository.EmployeeRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/employees")
public class AdminEmployeeController {
  private final EmployeeRepository repo;
  public AdminEmployeeController(EmployeeRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('EMPLOYEE_READ')") public PageResponse<Employee> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100)); var pg=repo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
  @PostMapping @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')") public ApiResponse<Employee> create(@RequestBody Employee e){ return new ApiResponse<>(repo.save(e)); }
  @PutMapping("/{id}") @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')") public ApiResponse<Employee> update(@PathVariable Long id,@RequestBody Employee in){ var e=repo.findById(id).orElseThrow(); e.setDepartment(in.getDepartment()); e.setPosition(in.getPosition()); return new ApiResponse<>(repo.save(e)); }
}
