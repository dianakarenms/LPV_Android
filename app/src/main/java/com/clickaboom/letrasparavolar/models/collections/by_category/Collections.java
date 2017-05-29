package com.clickaboom.letrasparavolar.models.collections.by_category;

import com.clickaboom.letrasparavolar.models.collections.categories.Categoria;
import com.clickaboom.letrasparavolar.models.Imagen;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/27/17.
 */

public class Collections implements Serializable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("fecha")
    @Expose
    public String fecha;
    @SerializedName("epub")
    @Expose
    public String epub;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("editorial")
    @Expose
    public String editorial;
    @SerializedName("length")
    @Expose
    public String length;
    @SerializedName("creditos")
    @Expose
    public String creditos;
    @SerializedName("autores")
    @Expose
    public List<Autores> autores = null;
    @SerializedName("latitud")
    @Expose
    public String latitud;
    @SerializedName("longitud")
    @Expose
    public String longitud;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("imagenes")
    @Expose
    public List<Imagen> imagenes = null;
    @SerializedName("colecciones_id")
    @Expose
    public String coleccionesId;
    @SerializedName("categorias")
    @Expose
    public List<Categoria> categorias = null;
    @SerializedName("etiquetas")
    @Expose
    public List<Object> etiquetas = null;
    @SerializedName("libros_relacionados")
    @Expose
    public List<Collections> librosRelacionados = null;

    @SerializedName("0")
    @Expose
    public String _0;
    @SerializedName("1")
    @Expose
    public String _1;
    @SerializedName("2")
    @Expose
    public String _2;
    @SerializedName("3")
    @Expose
    public String _3;
    @SerializedName("4")
    @Expose
    public String _4;
    @SerializedName("5")
    @Expose
    public String _5;
    @SerializedName("6")
    @Expose
    public String _6;
    @SerializedName("7")
    @Expose
    public String _7;
    @SerializedName("8")
    @Expose
    public String _8;
    @SerializedName("9")
    @Expose
    public String _9;
}