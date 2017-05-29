package com.clickaboom.letrasparavolar.models.collections.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 5/28/17.
 */

public class Subcategoria implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("categorias_id")
    @Expose
    public String categoriasId;
}
