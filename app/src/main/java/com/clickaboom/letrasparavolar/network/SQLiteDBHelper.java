package com.clickaboom.letrasparavolar.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clickaboom.letrasparavolar.models.Imagen;
import com.clickaboom.letrasparavolar.models.collections.Autores;
import com.clickaboom.letrasparavolar.models.collections.Categoria;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.Etiqueta;
import com.clickaboom.letrasparavolar.models.collections.categories.Subcategoria;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karen on 25/04/17.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    // Instance for singleton
    private static SQLiteDBHelper sInstance = null;

    // DATABASE init
    public static final String DATABASE_NAME = "SQLiteDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_BOOKS = "books";
    public static final String BOOK_KEY = "ID_epubCodeName";

    public static final String KEY_ID = "id";
    public static final String titulo = "titulo";
    public static final String fecha = "fecha";
    public static final String epub = "epub";
    public static final String descripcion = "descripcion";
    public static final String editorial = "editorial";
    public static final String length = "length";
    public static final String autores = "autores";
    public static final String imagen = "imagen";
    public static final String imagenes = "imagenes";
    public static final String categorias = "categorias";
    public static final String etiquetas = "etiquetas";
    public static final String librosRelacionados = "librosRelacionados";
    public static final String KEY_FAVORITO = "favorito";
    public static final String KEY_DOWNLOADED = "descargado";
    public static final String KEY_TYPE = "bookType";

    private SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    public static SQLiteDBHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SQLiteDBHelper.class) {
                if (sInstance == null)
                    sInstance = new SQLiteDBHelper(context);
            }
        }
        return sInstance;
    }

    // Called whenever a new db is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        createBooksTable(db);
        createCategoriesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

        // create new tables
        onCreate(db);
    }

    /**
     * Books Table Methods
     */
    private void createBooksTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_BOOKS + "(" +
                BOOK_KEY + " TEXT PRIMARY KEY, " +
                KEY_ID + " TEXT, " +
                titulo + " TEXT, " +
                fecha + " TEXT, " +
                epub + " TEXT, " +
                descripcion + " TEXT, " +
                editorial + " TEXT, " +
                length + " TEXT, " +
                autores + " TEXT, " +
                imagen + " TEXT, " +
                imagenes + " TEXT, " +
                categorias + " TEXT, " +
                etiquetas + " TEXT, " +
                KEY_TYPE + " TEXT, " +
                KEY_FAVORITO + " TEXT, " +
                KEY_DOWNLOADED + " TEXT, " +
                librosRelacionados + " TEXT);"
        );
    }

    public boolean insertBook(Colecciones book) {
        Gson gson = new Gson();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_KEY, book.epub);
        contentValues.put(KEY_ID, String.valueOf(book.id));
        contentValues.put(titulo, book.titulo);
        contentValues.put(fecha, book.fecha);
        contentValues.put(epub, book.epub);
        contentValues.put(descripcion, book.descripcion);
        contentValues.put(editorial, book.editorial);
        contentValues.put(length, book.length);
        contentValues.put(autores, gson.toJson(book.autores));
        contentValues.put(imagen, book.imagen);
        contentValues.put(imagenes, gson.toJson(book.imagenes));
        contentValues.put(categorias, gson.toJson(book.categorias));
        contentValues.put(etiquetas, gson.toJson(book.etiquetas));
        contentValues.put(librosRelacionados, gson.toJson(book.librosRelacionados));
        contentValues.put(KEY_TYPE, book.mBookType);
        contentValues.put(KEY_FAVORITO, book.favorito ? 1 : 0);
        contentValues.put(KEY_DOWNLOADED, book.descargado ? 1 : 0);
        try {
            db.insertOrThrow(TABLE_BOOKS, null, contentValues);
        } catch (SQLiteConstraintException ex) {
            Log.e("insertLabel", ex.getMessage());
        }
        return true;
    }

    public boolean updateFavBook(String key, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FAVORITO, value);
        db.update(TABLE_BOOKS, contentValues, BOOK_KEY + " = ? ", new String[] { key } );
        return value == 1;
    }

    public ArrayList<Colecciones> getBookByePub(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + TABLE_BOOKS + " WHERE " +
                BOOK_KEY + "=?", new String[] { key} );
        return getBookDetails(rs);
    }

    public ArrayList<Colecciones> getBookById(String id, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + TABLE_BOOKS + " WHERE " +
                KEY_ID + "=?" + " AND " + KEY_TYPE + "=?",
                new String[] {id, type} );
        return getBookDetails(rs);
    }

    /*public String getBook(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " +
                BOOK_KEY + "=?", new String[] { key} );
        rs.moveToFirst();
        String value = key;
        try {
            value = rs.getString(rs.getColumnIndex(titulo));
            if (!rs.isClosed()) {
                rs.close();
            }
        } catch (IndexOutOfBoundsException ex) {
            Log.d("Db getLabel", ex.getMessage());
        }
        return value;
    }*/

    public ArrayList<Colecciones> getAllBooks() {
        if(isDataAlreadyInBooks()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor rs = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
            return getBookDetails(rs);
        } else return new ArrayList<>();
    }

    public Integer deleteBook(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKS,
                BOOK_KEY + " = ? ",
                new String[] { key });
    }

    public void eraseTableData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_BOOKS);
        db.execSQL("VACUUM");
    }

    private ArrayList<Colecciones> getBookDetails(Cursor rs) {
        ArrayList<Colecciones> mArrayList = new ArrayList<>();

        Gson gson = new Gson();
        Type autType = new TypeToken<ArrayList<Autores>>() {}.getType();
        Type imgsType = new TypeToken<ArrayList<Imagen>>() {}.getType();
        Type etType = new TypeToken<ArrayList<Etiqueta>>() {}.getType();
        Type catType = new TypeToken<ArrayList<Categoria>>() {}.getType();
        Type colType = new TypeToken<ArrayList<Colecciones>>() {}.getType();

        while(rs.moveToNext()) {
            ArrayList<Autores> autoresList = gson.fromJson(rs.getString(rs.getColumnIndex(autores)), autType);
            if(autoresList == null)
                autoresList = new ArrayList<>();

            ArrayList<Imagen> imagenesList = gson.fromJson(rs.getString(rs.getColumnIndex(imagenes)), imgsType);
            if(imagenesList == null)
                imagenesList = new ArrayList<>();

            ArrayList<Etiqueta> etiquetasList = gson.fromJson(rs.getString(rs.getColumnIndex(etiquetas)), etType);
            if(etiquetasList == null)
                etiquetasList = new ArrayList<>();

            ArrayList<Categoria> categoriasList = gson.fromJson(rs.getString(rs.getColumnIndex(categorias)), catType);
            if(categoriasList == null)
                categoriasList = new ArrayList<>();

            ArrayList<Colecciones> librosRelacionadosList = gson.fromJson(rs.getString(rs.getColumnIndex(librosRelacionados)), colType);
            if(librosRelacionadosList == null)
                librosRelacionadosList = new ArrayList<>();

            mArrayList.add(new Colecciones(
                    Integer.valueOf(rs.getString(rs.getColumnIndex(KEY_ID))),
                    rs.getString(rs.getColumnIndex(titulo)),
                    rs.getString(rs.getColumnIndex(fecha)),
                    rs.getString(rs.getColumnIndex(epub)),
                    rs.getString(rs.getColumnIndex(descripcion)),
                    rs.getString(rs.getColumnIndex(editorial)),
                    rs.getString(rs.getColumnIndex(length)),
                    autoresList,
                    rs.getString(rs.getColumnIndex(imagen)),
                    imagenesList,
                    categoriasList,
                    etiquetasList,
                    librosRelacionadosList,
                    rs.getInt(rs.getColumnIndex(KEY_FAVORITO)) > 0,
                    rs.getInt(rs.getColumnIndex(KEY_DOWNLOADED)) > 0,
                    rs.getString(rs.getColumnIndex(KEY_TYPE))
            )); //add the item
        }

        return mArrayList;
    }

    public void addAllBooks(List<Colecciones> books, String type) {
        //eraseTableData();
        for (Colecciones book : books) {
            book.favorito = false;
            book.descargado = false;
            book.mBookType = type;
            insertBook(book);
        }
        getAllBooks();
    }

    public boolean isDataAlreadyInBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + TABLE_BOOKS, null );
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Categories Table Methods
     */
    public static final String TABLE_CATEGORIES = "categories";
    public static final String KEY_CAT_ID = "id";
    public static final String KEY_CAT_NAME = "nombre";
    public static final String KEY_CAT_DESCRIPTION = "descripcion";
    public static final String KEY_CAT_ICON = "icono";
    public static final String KEY_CAT_SUBCATEGORIES = "subcategorias";
    public static final String KEY_CAT_COLID = "categorias_colecciones_id";
    public static final String KEY_CAT_CATEGORY = "categoria";
    public static final String KEY_CAT_CATID = "categorias_id";
    public static final String KEY_CAT_TYPE = "cat_type";

    private void createCategoriesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + "(" +
                KEY_CAT_ID + " TEXT PRIMARY KEY, " +
                KEY_CAT_NAME + " TEXT, " +
                KEY_CAT_DESCRIPTION + " TEXT, " +
                KEY_CAT_ICON + " TEXT, " +
                KEY_CAT_SUBCATEGORIES + " TEXT, " +
                KEY_CAT_COLID + " TEXT, " +
                KEY_CAT_CATEGORY + " TEXT, " +
                KEY_CAT_TYPE + " TEXT, " +
                KEY_CAT_CATID + " TEXT);"
        );
    }

    public ArrayList<Categoria> getAllCategories() {
        if(isDataAlreadyInCategories()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor rs = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
            return getCategoryDetails(rs);
        } else return new ArrayList<>();
    }

    public boolean isDataAlreadyInCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + TABLE_CATEGORIES, null );
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void addAllCategories(List<Categoria> categorias, String type) {
        //eraseTableData();
        for (Categoria categoria : categorias) {
            categoria.categoryType = type;
            insertCategory(categoria);
        }
        getAllBooks();
    }

    public boolean insertCategory(Categoria categoria) {
        Gson gson = new Gson();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CAT_ID, String.valueOf(categoria.id));
        contentValues.put(KEY_CAT_NAME, categoria.nombre);
        contentValues.put(KEY_CAT_DESCRIPTION, categoria.descripcion);
        contentValues.put(KEY_CAT_ICON, categoria.icono);
        contentValues.put(KEY_CAT_SUBCATEGORIES, gson.toJson(categoria.subcategorias));
        contentValues.put(KEY_CAT_COLID, categoria.categoriasColeccionesId);
        contentValues.put(KEY_CAT_CATEGORY, categoria.categoria);
        contentValues.put(KEY_CAT_CATID, categoria.categoriasId);
        contentValues.put(KEY_CAT_TYPE, categoria.categoryType);

        try {
            db.insertOrThrow(TABLE_CATEGORIES, null, contentValues);
        } catch (SQLiteConstraintException ex) {
            Log.e("insertLabel", ex.getMessage());
        }
        return true;
    }

    private ArrayList<Categoria> getCategoryDetails(Cursor rs) {
        ArrayList<Categoria> mArrayList = new ArrayList<>();

        Gson gson = new Gson();
        Type subCatType = new TypeToken<ArrayList<Subcategoria>>() {}.getType();

        while(rs.moveToNext()) {
            ArrayList<Subcategoria> subcategorias = gson.fromJson(rs.getString(rs.getColumnIndex(KEY_CAT_SUBCATEGORIES)), subCatType);
            if(subcategorias == null)
                subcategorias = new ArrayList<>();

            mArrayList.add(new Categoria(
                    Integer.valueOf(rs.getString(rs.getColumnIndex(KEY_ID))),
                    rs.getString(rs.getColumnIndex(KEY_CAT_NAME)),
                    rs.getString(rs.getColumnIndex(KEY_CAT_DESCRIPTION)),
                    rs.getString(rs.getColumnIndex(KEY_CAT_ICON)),
                    subcategorias,
                    rs.getString(rs.getColumnIndex(KEY_CAT_COLID)),
                    rs.getString(rs.getColumnIndex(KEY_CAT_CATEGORY)),
                    rs.getString(rs.getColumnIndex(KEY_CAT_COLID)),
                    rs.getString(rs.getColumnIndex(KEY_CAT_TYPE)))
            ); //add the item
        }

        return mArrayList;
    }
}
