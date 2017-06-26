package com.clickaboom.letrasparavolar.models.collections;

import com.clickaboom.letrasparavolar.models.collections.categories.Subcategoria;
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
    public Integer id;
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

    @SerializedName("categorias_colecciones_id")
    @Expose
    public String categoriasColeccionesId;
    @SerializedName("categoria")
    @Expose
    public String categoria;

    @SerializedName("categorias_id")
    @Expose
    public String categoriasId;

    public boolean active = false;
    public String categoryType;

    public Categoria(Integer id, String nombre, String descripcion, String icono, List<Subcategoria> subcategorias, String categoriasColeccionesId, String categoria, String categoriasId, String categoryType) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.subcategorias = subcategorias;
        this.categoriasColeccionesId = categoriasColeccionesId;
        this.categoria = categoria;
        this.categoriasId = categoriasId;
        this.categoryType = categoryType;
    }
}
