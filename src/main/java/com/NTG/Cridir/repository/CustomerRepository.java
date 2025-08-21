package com.NTG.Cridir.repository;

import com.NTG.Cridir.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long > {

    void deleteByUserUserId(Long userId);
}
