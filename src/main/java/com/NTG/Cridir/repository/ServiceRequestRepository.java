package com.NTG.Cridir.repository;

import com.NTG.Cridir.model.Customer;
import com.NTG.Cridir.model.Enum.Status;
import com.NTG.Cridir.model.Provider;
import com.NTG.Cridir.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCustomerCustomerId(Long customerId);
    List<ServiceRequest> findByStatusAndProviderIsNull(Status status);

    List<ServiceRequest> findByProviderProviderId(Long providerId);
}
