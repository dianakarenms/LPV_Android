package com.clickaboom.letrasparavolar.models.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by karen on 15/06/17.
 */

public class ResResultadoTest implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<ResultadoTest> data = null;
}
