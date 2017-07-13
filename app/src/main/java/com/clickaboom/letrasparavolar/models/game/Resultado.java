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
    public Integer id;
    @SerializedName("respuestas_curioseando_id")
    @Expose
    public Integer respuestasCurioseandoId;
    @SerializedName("resultados_curioseando_id")
    @Expose
    public Integer resultadosCurioseandoId;
    @SerializedName("valor")
    @Expose
    public Integer valor;
}
