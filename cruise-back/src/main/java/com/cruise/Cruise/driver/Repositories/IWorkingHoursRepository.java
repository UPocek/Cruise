package com.cruise.Cruise.driver.Repositories;

import com.cruise.Cruise.driver.DTO.WorkingHoursDTO;
import com.cruise.Cruise.models.WorkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IWorkingHoursRepository extends JpaRepository<WorkingTime, Long> {
    @Query("SELECT NEW com.cruise.Cruise.driver.DTO.WorkingHoursDTO(w) FROM WorkingTime w WHERE w.driver.id = :driverId")
    List<WorkingHoursDTO> findAllWorkingHoursByDriverId(@Param("driverId") Long driverId);

    List<WorkingTime> findAllByDriverId(Long id);

    @Query("SELECT max(w.id) FROM WorkingTime w WHERE w.driver.active = true GROUP BY w.driver.id")
    List<Object[]> findAllWorkingHoursToBeUpdated();

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE WorkingTime w SET w.endTime = :endTime WHERE w.id IN (:workingHoursToBeUpdated)")
    void updateDriverWorkingHours(@Param("workingHoursToBeUpdated") List<Long> workingHoursToBeUpdated, @Param("endTime") LocalDateTime endTime);

}
