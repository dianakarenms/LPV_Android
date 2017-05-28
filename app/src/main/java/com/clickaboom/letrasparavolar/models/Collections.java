package com.clickaboom.letrasparavolar.models;

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
    @SerializedName("0")
    @Expose
    public String _0;
    @SerializedName("titulo")
    @Expose
    public String titulo;
    @SerializedName("1")
    @Expose
    public String _1;
    @SerializedName("fecha")
    @Expose
    public String fecha;
    @SerializedName("2")
    @Expose
    public String _2;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("3")
    @Expose
    public String _3;
    @SerializedName("epub")
    @Expose
    public String epub;
    @SerializedName("4")
    @Expose
    public String _4;
    @SerializedName("latitud")
    @Expose
    public String latitud;
    @SerializedName("5")
    @Expose
    public String _5;
    @SerializedName("longitud")
    @Expose
    public String longitud;
    @SerializedName("6")
    @Expose
    public String _6;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("7")
    @Expose
    public String _7;
    @SerializedName("editorial")
    @Expose
    public String editorial;
    @SerializedName("8")
    @Expose
    public String _8;
    @SerializedName("creditos")
    @Expose
    public String creditos;
    @SerializedName("9")
    @Expose
    public String _9;
    @SerializedName("autores")
    @Expose
    public List<Autores> autores = null;
}
