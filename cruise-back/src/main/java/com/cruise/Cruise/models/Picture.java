package com.cruise.Cruise.models;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;

@Entity
@Table(name = "picture")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] pictureContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPictureContent() {
        return "data:image/jpeg;base64," + Base64.toBase64String(this.pictureContent);
    }

    public void setPictureContent(String pictureContent) {
        String[] picture;
        try {
            picture = pictureContent.split(",");
        } catch (ArrayIndexOutOfBoundsException e) {
            picture = "data:image/jpeg;base64,".split(",");
        }

        if (picture.length >= 2) {
            byte[] content = Base64.decode(picture[1]);
            if (content.length > 5000000) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is bigger than 5mb!");
            }
            this.pictureContent = content;
        } else {
            this.pictureContent = Base64.decode(" ");
        }
    }
}
