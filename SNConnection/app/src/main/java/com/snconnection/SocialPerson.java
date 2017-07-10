package com.snconnection;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joe on 6/29/2017.
 */

public class SocialPerson implements Serializable {
    /*** Id of social person from chosen social network.*/
    private String id;
    /*** Name of social person from social network.*/
    private String name;
    /*** Profile picture url of social person from social network.*/
    private String avatarURL;
    /*** Profile URL of social person from social network.*/
    private String profileURL;
    /*** Email of social person from social network if exist.*/
    private String email;

    public SocialPerson() {
    }

    public SocialPerson(JSONObject object){
        try {
            this.setName(object.getString("firstName")+" "+object.getString("lastName"));
            this.avatarURL=object.getString("pictureUrl");
            this.email=object.getString("emailAddress");
        }catch (Exception  e){
            e.printStackTrace();
        }

    }
    public SocialPerson(String name, String avatarURL, String profileURL, String email) {
        this.name = name;
        this.avatarURL = avatarURL;
        this.profileURL = profileURL;
        this.email = email;
    }

    public SocialPerson(String id, String name, String avatarURL, String profileURL, String email) {
        this.id = id;
        this.name = name;
        this.avatarURL = avatarURL;
        this.profileURL = profileURL;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
