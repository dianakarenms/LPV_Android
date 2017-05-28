package com.clickaboom.letrasparavolar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/27/17.
 */

public class ResCollections implements Serializable{
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<List<Collections>> data = null;

}
