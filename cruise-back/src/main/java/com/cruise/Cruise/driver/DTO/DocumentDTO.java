package com.cruise.Cruise.driver.DTO;

import com.cruise.Cruise.models.Document;

public class DocumentDTO {
    private Long id;
    private String name;
    private Long driverId;
    private String documentImage;

    public DocumentDTO() {
    }

    public DocumentDTO(Document document) {
        this.id = document.getId();
        this.name = document.getName();
        this.documentImage = document.getDocumentImage().getPictureContent();
        this.driverId = document.getDriver().getId();
    }

    public DocumentDTO(Long id, String name, String image, Long driverId) {
        this.id = id;
        this.name = name;
        this.documentImage = image;
        this.driverId = driverId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

}
