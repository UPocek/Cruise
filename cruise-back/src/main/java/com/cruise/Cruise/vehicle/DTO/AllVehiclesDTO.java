package com.cruise.Cruise.vehicle.DTO;

import java.util.Collection;

public class AllVehiclesDTO {

    private int totalCount;
    private Collection<VehicleToDriveDTO> results;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Collection<VehicleToDriveDTO> getResults() {
        return results;
    }

    public void setResults(Collection<VehicleToDriveDTO> results) {
        this.results = results;
    }
}
