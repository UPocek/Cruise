package com.cruise.Cruise.panic.Repositories;

import com.cruise.Cruise.models.Panic;
import com.cruise.Cruise.models.Ride;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPanicRepository extends JpaRepository<Panic, Long> {
    @NotNull
    List<Panic> findAll();

    List<Panic> findAllByUserIdAndCurrentRideId(Long userId, Long currentRideId, Sort sort);

    @Query("SELECT DISTINCT p.currentRide FROM Panic p WHERE p.user.id = :userId")
    Page<Ride> findDistinctPanicByRide(Long userId, Pageable pageable);
}
