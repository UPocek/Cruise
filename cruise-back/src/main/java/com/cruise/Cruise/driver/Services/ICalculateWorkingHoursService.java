package com.cruise.Cruise.driver.Services;

import com.cruise.Cruise.driver.DTO.WorkingHourEndDTO;
import com.cruise.Cruise.driver.DTO.WorkingHourStartDTO;
import com.cruise.Cruise.driver.DTO.WorkingHoursDTO;
import com.cruise.Cruise.driver.DTO.WorkingTimeDurationDTO;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.WorkingTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ICalculateWorkingHoursService {
    Map<String, Object> getAllWorkingHoursByDriverId(Long driverId);

    WorkingHoursDTO getWorkingHourById(Long id);

    public List<Long> getDriverIdsToDeactivate();

    public WorkingTime createWorkingHour(Driver driver, LocalDateTime startTime);

    public boolean canDriverCanActivate(Long id);

    WorkingHoursDTO addDriversWorkingHours(Long id, WorkingHourStartDTO start);

    WorkingHoursDTO updateDriversWorkingHour(Long workingHoursId, WorkingHourEndDTO workingHourEndDTO);

    long getHowMuchHasDriverWorkedToday(Long driverId);

    WorkingTimeDurationDTO getDriverWorkingTime(Long id);
}
