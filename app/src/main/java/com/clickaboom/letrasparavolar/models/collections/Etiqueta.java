package com.clickaboom.letrasparavolar.models.collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 6/6/17.
 */

public class Etiqueta implements Serializable {
    @SerializedName("etiqueta")
    @Expose
    public String etiqueta;
    @SerializedName("tags_id")
    @Expose
    public String tagsId;
}
