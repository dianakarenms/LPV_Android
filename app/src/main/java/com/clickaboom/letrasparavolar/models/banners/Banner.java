package com.clickaboom.letrasparavolar.models.banners;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 5/28/17.
 */

public class Banner implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("idImagen")
    @Expose
    public String idImagen;
    @SerializedName("extension")
    @Expose
    public String extension;
    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("libros_id")
    @Expose
    public String librosId;
    @SerializedName("colecciones_id")
    @Expose
    public String coleccionesId;}
