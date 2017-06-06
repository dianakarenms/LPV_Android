package com.clickaboom.letrasparavolar.network;

/**
 * Created by clickaboom on 5/27/17.
 */

public class ApiConfig {
    public static final String baseUrl = "https://app.letrasparavolar.org";

    // Banners
    public static final String banners = baseUrl + "/api/banners/";

    // Libros / Leyendas
    public static final String legends = baseUrl + "/api/libros/";
    public static final String legendsCategories = baseUrl + "/api/categorias/";
    public static final String legendsDefaults = baseUrl + "/api/libros/defaults/";
    public static final String searchLegends = baseUrl + "/api/search/libros/";

    // Colecciones
    public static final String collections = baseUrl + "/api/colecciones/";
    public static final String collectionsCategories = baseUrl + "/api/categoriascolecciones/";
    public static final String collectionsDefaults = baseUrl + "/api/colecciones/defaults/";
    public static final String searchCollections = baseUrl + "/api/search/colecciones/";

    // Images URL
    public static final String collectionsImg = baseUrl + "/uploads/images/libros/thumb_";

    // Epubs URL
    public static final String epubs = baseUrl + "/uploads/epubs/";
}
