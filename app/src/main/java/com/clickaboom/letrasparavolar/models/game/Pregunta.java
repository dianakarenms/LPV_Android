package com.clickaboom.letrasparavolar.models.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by karen on 14/06/17.
 */

public class Pregunta implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("pregunta")
    @Expose
    public String pregunta;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("respuestas")
    @Expose
    public List<Respuesta> respuestas = null;
}
