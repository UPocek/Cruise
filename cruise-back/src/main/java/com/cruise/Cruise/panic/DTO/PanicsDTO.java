package com.cruise.Cruise.panic.DTO;

import java.util.List;

public class PanicsDTO {
    private int totalCount;
    private List<PanicDTO> results;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<PanicDTO> getResults() {
        return results;
    }

    public void setResults(List<PanicDTO> results) {
        this.results = results;
    }
}
