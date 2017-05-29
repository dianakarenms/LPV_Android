package com.clickaboom.letrasparavolar.models.collections.by_category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by clickaboom on 5/27/17.
 */

public class Autores implements Serializable {
    @SerializedName("autor")
    @Expose
    public String autor;
}
