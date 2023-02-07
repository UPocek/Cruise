package com.cruisemobile.cruise.models;

import java.util.List;

public class AllMessagesDTO {
    private Integer totalCount;
    private List<MessageDTO> results;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<MessageDTO> getResults() {
        return results;
    }

    public void setResults(List<MessageDTO> results) {
        this.results = results;
    }
}
