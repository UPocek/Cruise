package com.cruise.Cruise.review.DTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ReviewResponseDTO {
    @NotNull
    private int totalCount;
    @NotNull
    private List<ReviewDTO> results;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ReviewDTO> getResults() {
        return results;
    }

    public void setResults(List<ReviewDTO> results) {
        this.results = results;
    }


}
