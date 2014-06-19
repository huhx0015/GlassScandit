package com.mirasense.demos;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SearchResponse {

    public List<Result> results;

    @SerializedName("title")
    public String titleID;

    @SerializedName("link")
    public String url;

    @SerializedName("description")
    public String descript;

    @SerializedName("price")
    public String priceValue;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("store")
    public String storeName;

    public String query;

}