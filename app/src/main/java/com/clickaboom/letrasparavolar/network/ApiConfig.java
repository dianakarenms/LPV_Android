package com.clickaboom.letrasparavolar.network;

/**
 * Created by clickaboom on 5/27/17.
 */

public class ApiConfig {
    public static final String baseUrl = "http://app.letrasparavolar.org";

    // Banners
    public static final String banners = baseUrl + "/api/banners/";

    // Images Path Categorias
    public static final String catImgPath = baseUrl + "/uploads/images/categorias/";

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
    public static final String interImg = baseUrl + "/uploads/images/juegos/";

    // Epubs URL
    public static final String epubs = baseUrl + "/uploads/epubs/";

    // Internacionalización
    public static final String internacionalization = baseUrl + "/api/internacionalizacion/";

    // Mapa
    public static final String mapaMarkers = baseUrl + "/api/mapa/";

    // Games
    public static final String nahuatlismosGame = baseUrl + "/api/nahuatlismos/";
    public static final String curioseandoTests = baseUrl + "/api/curioseando/tests/";
    public static final String curioseandoTestQuestions = baseUrl + "/api/curioseando/preguntas/";

    // Participa
    public static final String participa = baseUrl + "/api/participa/save/";

    // Gacetita
    public static final String gacetitas = baseUrl + "/api/gacetita/";
    public static final String gacetitaImg = baseUrl + "/uploads/images/pdfportadas/";
    public static final String gacetitaPdf = baseUrl + "/uploads/pdfs/";

}
