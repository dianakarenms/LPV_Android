package com.clickaboom.letrasparavolar.models.collections.categories;

import com.clickaboom.letrasparavolar.models.collections.Categoria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/28/17.
 */

public class ResCategories implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<List<Categoria>> data = null;
    @SerializedName("path_iconos")
    @Expose
    public String pathIconos;
}
