package com.cruisemobile.cruise.models;

import java.util.ArrayList;

public class AllHistoryItemsDTO {

    private ArrayList<RideForUserDTO> historyItems;
    private int ridePageCount;

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
