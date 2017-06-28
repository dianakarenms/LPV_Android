package com.clickaboom.letrasparavolar.models.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by karen on 14/06/17.
 */

public class Respuesta implements Serializable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("respuesta")
    @Expose
    public String respuesta;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("is_correcta")
    @Expose
    public String isCorrecta;
    @SerializedName("preguntas_nahuatlismos_id")
    @Expose
    public String preguntasNahuatlismosId;
    @SerializedName("preguntas_curioseando_id")
    @Expose
    public String preguntasCurioseandoId;
    @SerializedName("resultados")
    @Expose
    public List<Resultado> resultados = null;
}
