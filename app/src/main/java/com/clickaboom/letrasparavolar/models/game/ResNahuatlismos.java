package com.clickaboom.letrasparavolar.models.game;

import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 6/6/17.
 */

public class ResNahuatlismos implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<Pregunta> data = null;
}