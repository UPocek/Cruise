package com.cruise.Cruise.driver.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class CreateDocumentDTO {
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String name;
    private String documentImage;

    public CreateDocumentDTO() {
    }

    public CreateDocumentDTO(String name, String documentImage) {
        this.name = name;
        this.documentImage = documentImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

}
