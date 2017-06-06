package com.clickaboom.letrasparavolar.models.defaults;

import com.clickaboom.letrasparavolar.models.collections.Collections;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 6/6/17.
 */

public class ResDefaults implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<Collections> data = null;

}
