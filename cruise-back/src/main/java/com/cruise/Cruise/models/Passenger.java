package com.cruise.Cruise.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "passenger")
public class Passenger extends User {
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "passengers",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"))
    private Set<Ride> rides = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "passengerFavouriteRides",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "favourite_ride_id", referencedColumnName = "id"))
    private Set<FavouriteRide> favouriteRides = new HashSet<>();


    public Passenger()
    {
    }

    public Passenger(Long id, String name, String surname, Picture profilePicture, String telephoneNumber, String email, String address, String password, Boolean active, Boolean blocked) {
        this.setId(id);
        this.setName(name);
        this.setSurname(surname);
        this.setProfilePicture(profilePicture);
        this.setTelephoneNumber(telephoneNumber);
        this.setEmail(email);
        this.setAddress(address);
        this.setPassword(password);
        this.setActive(active);
        this.setBlocked(blocked);
    }

    public Set<Ride> getRides() {
        return rides;
    }

    public void setRides(Set<Ride> rides) {
        this.rides = rides;
    }

    public Set<FavouriteRide> getFavouriteRides() {
        return favouriteRides;
    }

    public void setFavouriteRides(Set<FavouriteRide> favouriteRides) {
        this.favouriteRides = favouriteRides;
    }

    public void addFavouriteRide(FavouriteRide newFavouriteRide) {
        this.favouriteRides.add(newFavouriteRide);
    }

    public void removeFavouriteRide(FavouriteRide favouriteRideToRemove) {
        this.favouriteRides.remove(favouriteRideToRemove);
    }


}
