package com.clickaboom.letrasparavolar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by karen on 02/10/17.
 */

public class ImgUrlResponse implements Serializable {
    @SerializedName("url")
    @Expose
    public String url;
}
