package com.mirasense.demos;

import java.util.Date;
import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Michael Yoon Huh on 6/10/2014.
 */

public class Post {

    @SerializedName("id")
    public long ID;
    public String title;
    public String author;
    public String url;
    @SerializedName("date")
    public Date dateCreated;
    public String body;

    public List tags;

    public Post() {

    }
}