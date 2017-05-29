package com.clickaboom.letrasparavolar.models.collections.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/28/17.
 */

public class Categoria implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("icono")
    @Expose
    public String icono;
    @SerializedName("subcategorias")
    @Expose
    public List<Subcategoria> subcategorias = null;

    public boolean active = false;
}
