package com.qnguyendev.backendservice.repository;

import com.qnguyendev.backendservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByUserIdAndAddressType(Long userId, Integer addressType);
}
