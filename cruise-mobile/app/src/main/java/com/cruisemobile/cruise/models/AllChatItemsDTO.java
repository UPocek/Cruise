package com.cruisemobile.cruise.models;

import java.util.ArrayList;

public class AllChatItemsDTO {
    private ArrayList<ChatItemDTO> chatItems;
    private int panicPageCount;
    private int ridePageCount;

    public AllChatItemsDTO() {
    }

    public AllChatItemsDTO(ArrayList<ChatItemDTO> chatItems, int panicPageCount, int ridePageCount) {
        this.chatItems = chatItems;
        this.panicPageCount = panicPageCount;
        this.ridePageCount = ridePageCount;
    }

    public ArrayList<ChatItemDTO> getChatItems() {
        return chatItems;
    }

    public void setChatItems(ArrayList<ChatItemDTO> chatItems) {
        this.chatItems = chatItems;
    }

    public int getPanicPageCount() {
        return panicPageCount;
    }

    public void setPanicPageCount(int panicPageCount) {
        this.panicPageCount = panicPageCount;
    }

    public int getRidePageCount() {
        return ridePageCount;
    }

    public void setRidePageCount(int ridePageCount) {
        this.ridePageCount = ridePageCount;
    }
}

