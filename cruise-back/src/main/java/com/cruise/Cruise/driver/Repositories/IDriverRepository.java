package com.cruise.Cruise.driver.Repositories;

import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);

    Collection<Driver> findByActive(boolean activityStatus);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Driver d SET d.active = false WHERE d.id IN (:drivers_list)")
    void deactivateLongWorkingDrivers(@Param("drivers_list") List<Long> driversList);

    @Query("SELECT d.reviews FROM Driver d WHERE d.id = :driverId")
    Set<Review> findAllDriverReviews(Long driverId);

}
