package com.clickaboom.letrasparavolar.models.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by karen on 13/07/17.
 */

public class ResultadoTest implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("resultado")
    @Expose
    public String resultado;
    @SerializedName("imagen")
    @Expose
    public String imagen;
    @SerializedName("descripcion")
    @Expose
    public String descripcion;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("tests_id")
    @Expose
    public String testsId;
}
