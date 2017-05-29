package com.clickaboom.letrasparavolar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 5/28/17.
 */

public class Imagen implements Serializable {
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("size")
    @Expose
    public String size;
}
