package com.clickaboom.letrasparavolar.models.banners;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clickaboom on 5/28/17.
 */

public class ResBanners implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("data")
    @Expose
    public List<Banner> data = null;
    @SerializedName("path_banner")
    @Expose
    public String pathBanner;
}
