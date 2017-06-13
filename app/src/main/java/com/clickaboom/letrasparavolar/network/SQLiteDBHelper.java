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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by karen on 25/04/17.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    // Instance for singleton
    private static SQLiteDBHelper sInstance = null;
    public String tableName = "books";

    // DATABASE init
    public static final String DATABASE_NAME = "SQLiteDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String BOOKS_TABLE = "books";
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
    public static final String KEY_FAVORITO = "KEY_FAVORITO";
    public static final String KEY_TYPE = "type";

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
        db.execSQL("CREATE TABLE " + BOOKS_TABLE + "(" +
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
                KEY_FAVORITO + " TEXT, " +
                KEY_TYPE + " TEXT, " +
                librosRelacionados + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
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
        contentValues.put(KEY_FAVORITO, book.favorito ? 1 : 0);
        contentValues.put(KEY_TYPE, book.type);
        try {
            db.insertOrThrow(tableName, null, contentValues);
        } catch (SQLiteConstraintException ex) {
            Log.e("insertLabel", ex.getMessage());
        }
        return true;
    }

    public boolean updateFavBook(String key, int favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FAVORITO, favorite);
        db.update(tableName, contentValues, BOOK_KEY + " = ? ", new String[] { key } );
        return favorite == 1;
    }

    public ArrayList<Colecciones> getBookByePub(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " +
                BOOK_KEY + "=?", new String[] { key} );
        return arrayListFromCursor(rs);
    }

    public ArrayList<Colecciones> getBookById(String id, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " +
                KEY_ID + "=?" + " AND " + KEY_TYPE + "=?",
                new String[] {id, type} );
        return arrayListFromCursor(rs);
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
        if(isDataAlreadyInDB()) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor rs = db.rawQuery("SELECT * FROM " + tableName, null);
            return arrayListFromCursor(rs);
        } else return null;
    }

    public Integer deleteBook(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName,
                BOOK_KEY + " = ? ",
                new String[] { key });
    }

    public void eraseTableData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ tableName);
        db.execSQL("VACUUM");
    }

    private ArrayList<Colecciones> arrayListFromCursor(Cursor rs) {
        ArrayList<Colecciones> mArrayList = new ArrayList<>();

        Gson gson = new Gson();
        Type autType = new TypeToken<ArrayList<Autores>>() {}.getType();
        Type imgsType = new TypeToken<ArrayList<Imagen>>() {}.getType();
        Type etType = new TypeToken<ArrayList<Etiqueta>>() {}.getType();
        Type catType = new TypeToken<ArrayList<Categoria>>() {}.getType();
        Type colType = new TypeToken<ArrayList<Colecciones>>() {}.getType();

        while(rs.moveToNext()) {
            ArrayList<Autores> autoresList = gson.fromJson(rs.getString(rs.getColumnIndex(autores)), autType);
            ArrayList<Imagen> imagenesList = gson.fromJson(rs.getString(rs.getColumnIndex(imagenes)), imgsType);
            ArrayList<Etiqueta> etiquetasList = gson.fromJson(rs.getString(rs.getColumnIndex(etiquetas)), etType);
            ArrayList<Categoria> categoriasList = gson.fromJson(rs.getString(rs.getColumnIndex(categorias)), catType);
            ArrayList<Colecciones> librosRelacionadosList = gson.fromJson(rs.getString(rs.getColumnIndex(librosRelacionados)), colType);
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
                    rs.getString(rs.getColumnIndex(KEY_TYPE))
            )); //add the item
        }

        return mArrayList;
    }

    /*public void addLanguageLabels(List<Label> labels) {
        eraseTableData();
        for (Label label : labels) {
            insertLabel(label.code, label.title);
        }
        getAllBooks();
    }*/

    public boolean isDataAlreadyInDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + tableName, null );
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
