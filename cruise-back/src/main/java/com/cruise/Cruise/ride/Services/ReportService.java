package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.DTO.DayDTO;
import com.cruise.Cruise.driver.DTO.ReportDTO;
import com.cruise.Cruise.driver.DTO.ReportsDTO;
import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.models.Route;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.domain.Sort.by;

@Service
public class ReportService implements IReportService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IDriverRepository driverRepository;

    @Override
    public ReportsDTO getAllReports(String fromDate, String tillDate) {
        LocalDateTime dateFrom = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime dateTill = LocalDate.parse(tillDate).plusDays(1).atStartOfDay();
        if (dateFrom.isAfter(dateTill)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dates input");
        }
        ReportsDTO reportsDTO = new ReportsDTO();
        ReportDTO rideCountReport = new ReportDTO("Ride counts in period", "number of rides", 0, 0);
        ReportDTO kmCountReport = new ReportDTO("Kilometers counts in period", "number of km", 0, 0);
        ReportDTO priceCountReport = new ReportDTO("Money transactions in period", "money amount", 0, 0);
        List<Ride> rides = this.rideRepository.findRidesForAllReport(dateFrom, dateTill, by("startTime"));

        return prepareReports(reportsDTO, rideCountReport, kmCountReport, priceCountReport, rides);
    }

    @Override
    public ReportsDTO getUserReportsByEmail(String email, String fromDate, String tillDate) {
        Long id;
        Optional<Passenger> optionalPassenger = this.passengerRepository.findByEmail(email);
        if (optionalPassenger.isEmpty()) {
            Optional<Driver> optionalDriver = this.driverRepository.findByEmail(email);
            if (optionalDriver.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that email doesn't exist");
            }
            id = optionalDriver.get().getId();
            return getReports(id, fromDate, tillDate, "ROLE_DRIVER");
        } else {
            id = optionalPassenger.get().getId();
            return getReports(id, fromDate, tillDate, "ROLE_PASSENGER");
        }
    }

    @Override
    public ReportsDTO getReports(Long id, String fromDate, String tillDate, String role) {
        LocalDateTime dateFrom = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime dateTill = LocalDate.parse(tillDate).plusDays(1).atStartOfDay();
        if (dateFrom.isAfter(dateTill)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dates input");
        }
        ReportsDTO reportsDTO = new ReportsDTO();
        ReportDTO rideCountReport = new ReportDTO("Ride counts in period", "number of rides", 0, 0);
        ReportDTO kmCountReport = new ReportDTO("Kilometers counts in period", "number of km", 0, 0);
        ReportDTO priceCountReport = new ReportDTO("Money transactions in period", "money amount", 0, 0);
        List<Ride> rides;

        if (role.equalsIgnoreCase("ROLE_DRIVER")) {
            rides = this.rideRepository.findRidesForDriverReport(id, dateFrom, dateTill, by("startTime"));
        } else {
            rides = this.rideRepository.findRidesForPassengerReport(id, dateFrom, dateTill, by("startTime"));
        }

        return prepareReports(reportsDTO, rideCountReport, kmCountReport, priceCountReport, rides);
    }

    private ReportsDTO prepareReports(ReportsDTO reportsDTO,
                                      ReportDTO rideCountReport, ReportDTO kmCountReport, ReportDTO priceCountReport, Collection<Ride> rides) {
        int daysNum = 0;
        LocalDate currentDate = null;
        double rideCountValue = 0;
        double kmCountValue = 0;
        double priceCountValue = 0;
        for (Ride ride : rides) {
            if (currentDate == null) {
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();

                rideCountValue = 1;
                kmCountValue = getRideDistance(ride.getRoutes()) / 1000;
                priceCountValue = ride.getPrice();
            } else if (!currentDate.isEqual(ride.getStartTime().toLocalDate())) {
                rideCountReport.addDays(new DayDTO(currentDate.toString(), rideCountValue));
                kmCountReport.addDays(new DayDTO(currentDate.toString(), kmCountValue));
                priceCountReport.addDays(new DayDTO(currentDate.toString(), priceCountValue));

                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();

                rideCountValue = 1;
                kmCountValue = getRideDistance(ride.getRoutes()) / 1000;
                priceCountValue = ride.getPrice();
            } else {
                rideCountValue += 1;
                kmCountValue += getRideDistance(ride.getRoutes()) / 1000;
                priceCountValue += ride.getPrice();
            }

            rideCountReport.addSum(1);
            kmCountReport.addSum(getRideDistance(ride.getRoutes()) / 1000);
            priceCountReport.addSum(ride.getPrice());
        }

        if (currentDate != null) {
            rideCountReport.addDays(new DayDTO(currentDate.toString(), rideCountValue));
            kmCountReport.addDays(new DayDTO(currentDate.toString(), kmCountValue));
            priceCountReport.addDays(new DayDTO(currentDate.toString(), priceCountValue));

            rideCountReport.setAvg(rideCountReport.getSum() / daysNum);
            kmCountReport.setAvg(kmCountReport.getSum() / daysNum);
            priceCountReport.setAvg(priceCountReport.getSum() / daysNum);
        }

        List<ReportDTO> reportDTOS = new ArrayList<>();
        reportDTOS.add(rideCountReport);
        reportDTOS.add(kmCountReport);
        reportDTOS.add(priceCountReport);
        reportsDTO.setReports(reportDTOS);

        return reportsDTO;
    }

    @Override
    public ReportDTO getReportByType(Long id, String fromDate, String tillDate, String role, String type) {
        ReportDTO reportDTO = new ReportDTO();
        switch (type) {
            case "ride":
                reportDTO = getRideReport(id, fromDate, tillDate, role);
                break;
            case "km":
                reportDTO = getKmReport(id, fromDate, tillDate, role);
                break;
            case "money":
                reportDTO = getMoneyReport(id, fromDate, tillDate, role);
                break;
        }

        return reportDTO;
    }

    private ReportDTO getRideReport(Long id, String fromDate, String tillDate, String role) {
        LocalDateTime dateFrom = LocalDateTime.parse(fromDate);
        LocalDateTime dateTill = LocalDateTime.parse(tillDate);
        if (dateFrom.isAfter(dateTill)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dates input");
        }
        ReportDTO rideCountReport = new ReportDTO("Ride counts in period", "number of rides", 0, 0);
        List<Ride> rides;
        if (role.equalsIgnoreCase("ROLE_DRIVER")) {
            rides = this.rideRepository.findRidesForDriverReport(id, dateFrom, dateTill, by("startTime"));
        } else {
            rides = this.rideRepository.findRidesForPassengerReport(id, dateFrom, dateTill, by("startTime"));
        }

        int daysNum = 0;
        LocalDate currentDate = null;
        double rideCountValue = 0;
        for (Ride ride : rides) {
            if (currentDate == null) {
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                rideCountValue = 1;
            } else if (!currentDate.isEqual(ride.getStartTime().toLocalDate())) {
                rideCountReport.addDays(new DayDTO(currentDate.toString(), rideCountValue));
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                rideCountValue = 1;
            } else {
                rideCountValue += 1;
            }
            rideCountReport.addSum(1);
        }

        if (currentDate != null) {
            rideCountReport.setAvg(rideCountReport.getSum() / daysNum);
            rideCountReport.addDays(new DayDTO(currentDate.toString(), rideCountValue));
        }
        return rideCountReport;
    }

    private ReportDTO getKmReport(Long id, String fromDate, String tillDate, String role) {
        LocalDateTime dateFrom = LocalDateTime.parse(fromDate);
        LocalDateTime dateTill = LocalDateTime.parse(tillDate);
        if (dateFrom.isAfter(dateTill)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dates input");
        }
        ReportDTO kmCountReport = new ReportDTO("Kilometers counts in period", "number of km", 0, 0);
        List<Ride> rides;
        if (role.equalsIgnoreCase("ROLE_DRIVER")) {
            rides = this.rideRepository.findRidesForDriverReport(id, dateFrom, dateTill, by("startTime"));
        } else {
            rides = this.rideRepository.findRidesForPassengerReport(id, dateFrom, dateTill, by("startTime"));
        }
        int daysNum = 0;
        LocalDate currentDate = null;
        double kmCountValue = 0;
        for (Ride ride : rides) {
            if (currentDate == null) {
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                kmCountValue = getRideDistance(ride.getRoutes()) / 1000;
            } else if (!currentDate.isEqual(ride.getStartTime().toLocalDate())) {
                kmCountReport.addDays(new DayDTO(currentDate.toString(), kmCountValue));
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                kmCountValue = getRideDistance(ride.getRoutes()) / 1000;
            } else {
                kmCountValue += getRideDistance(ride.getRoutes()) / 1000;
            }
            kmCountReport.addSum(getRideDistance(ride.getRoutes()) / 1000);
        }

        if (currentDate != null) {
            kmCountReport.addDays(new DayDTO(currentDate.toString(), kmCountValue));
            kmCountReport.setAvg(kmCountReport.getSum() / daysNum);
        }

        return kmCountReport;
    }

    private ReportDTO getMoneyReport(Long id, String fromDate, String tillDate, String role) {
        LocalDateTime dateFrom = LocalDateTime.parse(fromDate);
        LocalDateTime dateTill = LocalDateTime.parse(tillDate);
        if (dateFrom.isAfter(dateTill)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dates input");
        }
        ReportDTO priceCountReport = new ReportDTO("Money transactions in period", "money amount", 0, 0);
        List<Ride> rides;
        if (role.equalsIgnoreCase("ROLE_DRIVER")) {
            rides = this.rideRepository.findRidesForDriverReport(id, dateFrom, dateTill, by("startTime"));
        } else {
            rides = this.rideRepository.findRidesForPassengerReport(id, dateFrom, dateTill, by("startTime"));
        }
        int daysNum = 0;
        LocalDate currentDate = null;
        double priceCountValue = 0;
        for (Ride ride : rides) {
            if (currentDate == null) {
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                priceCountValue = ride.getPrice();
            } else if (!currentDate.isEqual(ride.getStartTime().toLocalDate())) {
                priceCountReport.addDays(new DayDTO(currentDate.toString(), priceCountValue));
                daysNum += 1;
                currentDate = ride.getStartTime().toLocalDate();
                priceCountValue = ride.getPrice();
            } else {
                priceCountValue += ride.getPrice();
            }
            priceCountReport.addSum(ride.getPrice());
        }
        if (currentDate != null) {
            priceCountReport.addDays(new DayDTO(currentDate.toString(), priceCountValue));
            priceCountReport.setAvg(priceCountReport.getSum() / daysNum);
        }

        return priceCountReport;
    }

    private double getRideDistance(Set<Route> routes) {
        double distance = 0;
        for (Route route : routes) {
            distance += route.getDistance();
        }
        return distance;
    }

}
