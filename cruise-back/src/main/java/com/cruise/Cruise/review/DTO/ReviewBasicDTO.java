package com.cruise.Cruise.review.DTO;


import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ReviewBasicDTO {

    @NotNull(message = "is required!")
    @Min(value = 1, message = "must be in range 1 to 5")
    @Max(value = 5, message = "must be in range 1 to 5")
    private Integer rating;
    @NotNull(message = "is required!")
    @Length(min = 5, max = 300, message = "must have from 5 to 300 characters")
    private String comment;

    public ReviewBasicDTO() {
    }

    public ReviewBasicDTO(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
