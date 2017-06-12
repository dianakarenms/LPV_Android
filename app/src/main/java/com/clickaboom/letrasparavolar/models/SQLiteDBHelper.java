package com.clickaboom.letrasparavolar.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.google.gson.Gson;

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

    public static final String id = "id";
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
    public static final String favorito = "favorito";

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
                id + " TEXT, " +
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
                favorito + " BOOLEAN, " +
                librosRelacionados + " TEXT)"
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
        contentValues.put(id, String.valueOf(book.id));
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
        contentValues.put(favorito, book.favorito);
        contentValues.put(librosRelacionados, gson.toJson(book.librosRelacionados));
        try {
            db.insertOrThrow(tableName, null, contentValues);
        } catch (SQLiteConstraintException ex) {
            Log.e("insertLabel", ex.getMessage());
        }
        return true;
    }

    public boolean updateBook(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(BOOK_TITLE, value);
        db.update(tableName, contentValues, BOOK_KEY + " = ? ", new String[] { key } );
        return true;
    }

    public Cursor getBook(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " +
                BOOK_KEY + "=?", new String[] { key} );
        /*rs.moveToFirst();
        String value = key;
        try {
            value = rs.getString(rs.getColumnIndex(titulo));
            if (!rs.isClosed()) {
                rs.close();
            }
        } catch (IndexOutOfBoundsException ex) {
            Log.d("Db getLabel", ex.getMessage());
        }*/
        return rs;
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

    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + tableName, null );
        return res;
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
    
    /*public void addLanguageLabels(List<Label> labels) {
        eraseTableData();
        for (Label label : labels) {
            insertLabel(label.code, label.title);
        }
        getAllBooks();
    }*/

    public boolean isDataAlreadyInDB() {
        Cursor cursor = getAllBooks();
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
