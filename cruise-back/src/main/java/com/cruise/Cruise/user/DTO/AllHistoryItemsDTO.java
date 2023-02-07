package com.cruise.Cruise.user.DTO;

import com.cruise.Cruise.ride.DTO.RideForUserDTO;

import java.util.ArrayList;

public class AllHistoryItemsDTO {

    private ArrayList<RideForUserDTO> historyItems;
    private int ridePageCount;

    public AllHistoryItemsDTO() {
    }

    public AllHistoryItemsDTO(ArrayList<RideForUserDTO> historyItems, int ridePageCount) {
        this.historyItems = historyItems;
        this.ridePageCount = ridePageCount;
    }

    public ArrayList<RideForUserDTO> getHistoryItems() {
        return historyItems;
    }

    public void setHistoryItems(ArrayList<RideForUserDTO> historyItems) {
        this.historyItems = historyItems;
    }

    public int getRidePageCount() {
        return ridePageCount;
    }

    public void setRidePageCount(int ridePageCount) {
        this.ridePageCount = ridePageCount;
    }
}
