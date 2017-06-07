package com.clickaboom.letrasparavolar.models.collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/28/17.
 */

public class ResCollections implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<List<Colecciones>> data = null;
    @SerializedName("leyendas")
    @Expose
    public List<Colecciones> leyendas = null;
    @SerializedName("colecciones")
    @Expose
    public List<Colecciones> colecciones = null;
}
