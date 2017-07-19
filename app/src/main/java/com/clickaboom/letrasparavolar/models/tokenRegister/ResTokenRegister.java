package com.clickaboom.letrasparavolar.models.tokenRegister;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by karen on 19/07/17.
 */

public class ResTokenRegister implements Serializable {
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("data")
    @Expose
    public String data;
}
