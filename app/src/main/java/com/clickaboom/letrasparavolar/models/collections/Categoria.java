package com.clickaboom.letrasparavolar.models.collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 5/28/17.
 */

public class Categoria implements Serializable {
    @SerializedName("categorias_colecciones_id")
    @Expose
    public String categoriasColeccionesId;
    @SerializedName("categoria")
    @Expose
    public String categoria;
    @SerializedName("icono")
    @Expose
    public String icono;
}
