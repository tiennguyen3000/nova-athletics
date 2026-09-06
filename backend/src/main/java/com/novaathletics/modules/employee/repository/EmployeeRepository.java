package com.novaathletics.modules.employee.repository;
import com.novaathletics.modules.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EmployeeRepository extends JpaRepository<Employee,Long>{ Optional<Employee> findByUserId(Long uid); }
