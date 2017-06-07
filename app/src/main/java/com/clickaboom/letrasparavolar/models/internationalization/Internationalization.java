package com.clickaboom.letrasparavolar.models.internationalization;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 6/6/17.
 */

public class Internationalization implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("contenido")
    @Expose
    public String contenido;
}
