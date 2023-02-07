package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.models.Route;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class RouteForRideDTO {
    @Valid
    @NotNull(message = "is required!")
    private LocationForRideDTO departure;
    @Valid
    @NotNull(message = "is required!")
    private LocationForRideDTO destination;

    public RouteForRideDTO() {
    }

    public RouteForRideDTO(Route route) {
        this.departure = new LocationForRideDTO(route.getStartLocation());
        this.destination = new LocationForRideDTO(route.getEndLocation());
    }

    public RouteForRideDTO(LocationForRideDTO departure, LocationForRideDTO destination) {
        this.departure = departure;
        this.destination = destination;
    }

    public RouteForRideDTO(Optional<Route> first) {
        if (first.isEmpty()) return;
        Route route = first.get();
        this.destination = new LocationForRideDTO(route.getEndLocation());
        this.departure = new LocationForRideDTO(route.getStartLocation());
    }

    public LocationForRideDTO getDeparture() {
        return departure;
    }

    public void setDeparture(LocationForRideDTO departure) {
        this.departure = departure;
    }

    public LocationForRideDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationForRideDTO destination) {
        this.destination = destination;
    }
}
