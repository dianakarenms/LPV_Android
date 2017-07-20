package com.clickaboom.letrasparavolar.models.noticias;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by karen on 19/07/17.
 */

public class Noticia implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("category")
    @Expose
    public Object category;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("image")
    @Expose
    public String image;

}
