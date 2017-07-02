package com.clickaboom.letrasparavolar.models.gacetita;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 7/1/17.
 */

public class ResGacetita implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<List<Gacetita>> data = null;
}
