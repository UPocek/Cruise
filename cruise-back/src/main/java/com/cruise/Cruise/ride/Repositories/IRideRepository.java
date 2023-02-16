package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.driver.DTO.DriversRideDTO;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.ride.DTO.RideForUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface IRideRepository extends JpaRepository<Ride, Long> {
    //1
    @Query("SELECT NEW com.cruise.Cruise.ride.DTO.RideForUserDTO(r1) FROM Ride r1 WHERE r1.id in (SELECT DISTINCT r.id FROM Ride r LEFT JOIN r.passengers p WHERE (r.driver.id = :driverId OR passenger_id = :passengerId) AND (r.startTime >= :from AND r.startTime <= :to))")
    List<RideForUserDTO> findByPassengersIdOrDriverId(@Param("passengerId") Long passengerId, @Param("driverId") Long driverId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //2
    @Query("SELECT r FROM Ride r LEFT JOIN r.passengers p WHERE r.driver.id = :userId OR passenger_id = :userId")
    Page<Ride> findAllUserHistoryItems(Long userId, Pageable pageable);

    //3
    @Query("SELECT DISTINCT r FROM Ride r LEFT JOIN r.passengers p WHERE (r.driver.id = :driverId OR passenger_id = :passengerId) AND r.rideState != 'REJECTED'")
    Page<Ride> findByPassengersIdOrDriverIdChatItems(@Param("passengerId") Long passengerId, @Param("driverId") Long driverId, Pageable pageable);

    //4
    @Query("SELECT NEW com.cruise.Cruise.ride.DTO.RideForUserDTO(r) FROM Ride r LEFT JOIN r.passengers p WHERE (passenger_id = :passengerId) AND (r.startTime >= :from AND r.startTime <=  :to)")
    List<RideForUserDTO> findAllPassengersRides(@Param("passengerId") Long passengerId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //5
    @Query("SELECT NEW com.cruise.Cruise.driver.DTO.DriversRideDTO(r) FROM Ride r WHERE (r.driver.id = :driverId) AND (r.startTime >= :from AND r.startTime <=  :to)")
    List<DriversRideDTO> findAllDriverRides(@Param("driverId") Long driverId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //6
    @Query("SELECT r FROM Ride r WHERE (r.driver.id = :driverId) AND (r.startTime >= :from AND r.startTime <= :to)")
    List<Ride> findRidesForDriverReport(@Param("driverId") Long driverId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //7
    @Query("SELECT r FROM Ride r LEFT JOIN r.passengers p WHERE (passenger_id = :passengerId) AND (r.startTime >= :from AND r.startTime <=  :to)")
    List<Ride> findRidesForPassengerReport(@Param("passengerId") Long passengerId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //8
    @Query("SELECT r FROM Ride r WHERE r.startTime >= :from AND r.startTime <= :to")
    List<Ride> findRidesForAllReport(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Sort sort);

    //9
    Set<Ride> findByRideState(String state);

    //10
    Set<Ride> findByRideStateAndDriver(String state, Driver driver);

    //11
    Set<Ride> findByRideStateAndDriverId(String state, Long driverId, Sort sort);

    //12
    Set<Ride> findByRideStateAndPassengers(String state, Passenger passenger);

    //13
    Set<Ride> findByRideStateAndPassengersId(String state, Long passengerId, Sort sort);

}
