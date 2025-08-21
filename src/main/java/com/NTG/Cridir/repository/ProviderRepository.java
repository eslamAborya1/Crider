package com.NTG.Cridir.repository;

import com.NTG.Cridir.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProviderRepository extends JpaRepository<Provider,Long > {
    List<Provider> findByAvailabilityStatusTrue();

    void deleteByUserUserId(Long userId);
}
