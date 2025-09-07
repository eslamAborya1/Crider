package com.NTG.Cridir.repository;

import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCustomerCustomerId(Long customerId);
    List<ServiceRequest> findByStatusAndProviderIsNull(Status status);
    Optional<ServiceRequest> findByProvider_ProviderId(Long providerId);
    List<ServiceRequest> findByProviderProviderId(Long providerId);
}
