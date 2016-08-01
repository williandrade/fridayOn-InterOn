package me.williandrade.dto;


import android.net.Uri;

public class UserDTO {

    private String uid;
    private String displayName;
    private String email;
    private String doing;
    private String photo;
    private Double credits;

    public UserDTO() {
    }

    public UserDTO(String uid, String displayName, String email, String doing, String photo) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.doing = doing;
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }
}
