package com.cruise.Cruise.models;

import com.cruise.Cruise.driver.DTO.CreateDriverDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "driver")
public class Driver extends User {
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Document> documents = new HashSet<>();
    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Vehicle vehicle;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    private List<Ride> rides = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    private String status;

    public Driver() {
    }

    public Driver(CreateDriverDTO newDriver, String encodedPassword) {
        this.setName(newDriver.getName());
        this.setSurname(newDriver.getSurname());
        this.setAddress(newDriver.getAddress());
        this.setEmail(newDriver.getEmail());
        this.setTelephoneNumber(newDriver.getTelephoneNumber());
        this.setPassword(encodedPassword);

        Picture profilePicture = new Picture();
        profilePicture.setPictureContent(newDriver.getProfilePicture());
        this.setProfilePicture(profilePicture);
        this.setDocuments(new HashSet<>());
        this.setVehicle(null);
        this.setRides(new ArrayList<>());
        this.setReviews(new HashSet<>());
        this.setActive(false);
        this.setBlocked(false);
        this.setStatus("FREE");
    }

    public Driver(Long id, String name, String surname, Picture profilePicture, String telephoneNumber, String email, String address, String password, Boolean active, Boolean blocked) {
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
        this.setStatus("FREE");
        this.setDocuments(new HashSet<>());
        this.setVehicle(null);
        this.setRides(new ArrayList<>());
        this.setReviews(new HashSet<>());
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }
}
