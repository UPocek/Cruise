package com.cruise.Cruise.driver.DTO;

import java.util.List;

public class DriversRideListDTO {
    private int totalCount;
    private List<DriversRideDTO> results;

    public DriversRideListDTO() {
    }

    public DriversRideListDTO(int totalCount, List<DriversRideDTO> results) {
        this.totalCount = totalCount;
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<DriversRideDTO> getResults() {
        return results;
    }

    public void setResults(List<DriversRideDTO> results) {
        this.results = results;
    }
}
