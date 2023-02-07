package com.cruise.Cruise.driver.Repositories;

import com.cruise.Cruise.models.DriverInfoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverInfoRequestRepository extends JpaRepository<DriverInfoRequest, Long> {
    void deleteById(Long id);
}
