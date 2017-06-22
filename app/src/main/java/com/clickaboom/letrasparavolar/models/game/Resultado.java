package com.clickaboom.letrasparavolar.models.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 6/16/17.
 */

public class Resultado implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("respuestas_curioseando_id")
    @Expose
    public String respuestasCurioseandoId;
    @SerializedName("resultados_curioseando_id")
    @Expose
    public String resultadosCurioseandoId;
    @SerializedName("valor")
    @Expose
    public String valor;
}