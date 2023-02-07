package com.cruise.Cruise.driver.Services;

import com.cruise.Cruise.driver.DTO.WorkingHourEndDTO;
import com.cruise.Cruise.driver.DTO.WorkingHourStartDTO;
import com.cruise.Cruise.driver.DTO.WorkingHoursDTO;
import com.cruise.Cruise.driver.DTO.WorkingTimeDurationDTO;
import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Repositories.IWorkingHoursRepository;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.WorkingTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CalculateWorkingHoursService implements ICalculateWorkingHoursService {
    @Autowired
    private IWorkingHoursRepository workingHoursRepository;
    @Autowired
    private IDriverRepository driverRepository;

    @Override
    public Map<String, Object> getAllWorkingHoursByDriverId(Long driverId) {
        Optional<Driver> driverOptional = driverRepository.findById(driverId);
        if (driverOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist.");
        }
        Map<String, Object> result = new HashMap<>();
        List<WorkingHoursDTO> driversWorkingHours = workingHoursRepository.findAllWorkingHoursByDriverId(driverId);
        result.put("totalCount", driversWorkingHours.size());
        result.put("results", driversWorkingHours);

        return result;
    }

    @Override
    public WorkingHoursDTO getWorkingHourById(Long id) {
        Optional<WorkingTime> responseWorkingHour = workingHoursRepository.findById(id);
        if (responseWorkingHour.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour does not exist!");
        }
        WorkingTime workingTime = responseWorkingHour.get();
        return new WorkingHoursDTO(workingTime);
    }

    @Override
    public WorkingHoursDTO addDriversWorkingHours(Long id, WorkingHourStartDTO start) {
        Optional<Driver> response = driverRepository.findById(id);
        if (response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver doesn't exist");
        }
        Driver driver = response.get();
        if (driver.getVehicle() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start shift because the vehicle is not defined!");
        }
        if (!canDriverCanActivate(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start shift because you exceeded the 8 hours limit in last 24 hours!");
        }
        if (driver.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift already ongoing!");
        }

        WorkingTime workingTime = createWorkingHour(driver, LocalDateTime.parse(start.getStart()));
        return new WorkingHoursDTO(workingTime);
    }

    @Override
    public WorkingTime createWorkingHour(Driver driver, LocalDateTime startTime) {
        WorkingTime workingTime = new WorkingTime();
        workingTime.setDriver(driver);
        workingTime.setStartTime(startTime);
        workingTime.setEndTime(startTime);
        workingHoursRepository.save(workingTime);
        workingHoursRepository.flush();
        return workingTime;
    }

    @Override
    public WorkingHoursDTO updateDriversWorkingHour(Long workingHoursId, WorkingHourEndDTO workingHourEndDTO) {
        Optional<WorkingTime> workingTimeResponse = workingHoursRepository.findById(workingHoursId);
        if (workingTimeResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Working hour does not exist!");
        }
        WorkingTime workingTime = workingTimeResponse.get();
        if (!workingTime.getDriver().isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No shift is ongoing!");
        }
        if (workingTime.getDriver().getVehicle() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot end shift because the vehicle is not defined!");
        }

        workingTime.setEndTime(LocalDateTime.parse(workingHourEndDTO.getEnd()));
        workingHoursRepository.save(workingTime);
        workingHoursRepository.flush();
        return new WorkingHoursDTO(workingTime.getId(), workingTime.getStartTime(), workingTime.getEndTime());
    }

    @Override
    public List<Long> getDriverIdsToDeactivate() {
        List<Long> shouldBeDeactivatedDrivers = new ArrayList<>();
        List<WorkingTime> workingTimes = workingHoursRepository.findAll(Sort.by("driver"));
        long workingDay = Duration.ofHours(8).toMillis();
        Long currentDriverId = 0L;
        long duration = 0L;
        for (WorkingTime w : workingTimes) {
            if (!w.getDriver().getId().equals(currentDriverId)) {
                currentDriverId = w.getDriver().getId();
                duration = 0L;
            }
            if (!w.getStartTime().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
                duration += Duration.between(w.getStartTime(), w.getEndTime()).toMillis();
                if (duration >= workingDay) {
                    shouldBeDeactivatedDrivers.add(w.getDriver().getId());
                }
            }
        }
        return shouldBeDeactivatedDrivers;
    }

    @Override
    public boolean canDriverCanActivate(Long driverId) {
        long workingDay = Duration.ofHours(8).toMillis();
        long duration = getHowMuchHasDriverWorkedToday(driverId);
        return duration < workingDay;
    }

    public long getHowMuchHasDriverWorkedToday(Long driverId) {
        List<WorkingTime> workingTimes = workingHoursRepository.findAllByDriverId(driverId);
        long duration = 0L;
        for (WorkingTime workingTime : workingTimes) {
            if (!workingTime.getStartTime().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
                duration += Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toMillis();
            }
        }
        return duration;
    }

    @Override
    public WorkingTimeDurationDTO getDriverWorkingTime(Long driverId) {
        return new WorkingTimeDurationDTO(getHowMuchHasDriverWorkedToday(driverId));
    }

    @Async
    @Scheduled(fixedRate = 60000, initialDelayString = "${timing.initialScheduledDelay}")
    public void updateDriversWorkingHours() {
        LocalDateTime endTime = LocalDateTime.now();
        List<Object[]> workingHoursToBeUpdated = workingHoursRepository.findAllWorkingHoursToBeUpdated();
        List<Long> workingHoursIdsToBeUpdated = new ArrayList<>();

        for (Object[] workingHourId : workingHoursToBeUpdated) {
            workingHoursIdsToBeUpdated.add((Long) workingHourId[0]);
        }
        workingHoursRepository.updateDriverWorkingHours(workingHoursIdsToBeUpdated, endTime);
    }
}
