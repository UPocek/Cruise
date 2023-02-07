package com.cruisemobile.cruise.models;

import java.util.List;

public class AllUserRidesDTO {

    private Integer totalCount;
    private List<RideForUserDTO> results;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<RideForUserDTO> getResults() {
        return results;
    }

    public void setResults(List<RideForUserDTO> results) {
        this.results = results;
    }
}
