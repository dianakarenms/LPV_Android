package com.clickaboom.letrasparavolar.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.clickaboom.letrasparavolar.models.collections.Colecciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.clickaboom.letrasparavolar.activities.MainActivity.db;

/**
 * Created by clickaboom on 2/23/17.
 */

// new DownloadFile().execute("http://escolar.udg.mx/sites/default/files/Guia%20de%20estudios%20PAA.pdf", "guia_udg.pdf");
public class DownloadFile extends AsyncTask<String, Void, Void> {

    private static final String TAG = "Ext_Storage_Permission";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final Colecciones mEpub;
    private Boolean success, localStored, permissionNotGranted;
    private ProgressDialog barProgressDialog;
    private Context context;
    private Activity activity;
    private boolean showProgress;

    private Intent intent;

    File pdfFile;
    File folder;
    String fileUrl;   // -> http://maven.apache.org/maven-1.x/maven.pdf
    String fileName;  // -> maven.pdf

    private OnTaskCompleted listener;

    public DownloadFile(OnTaskCompleted listener, Context context, Activity activity, boolean showProgress, Colecciones ePub){
        this.listener = listener;
        this.context = context;
        this.activity = activity;
        this.showProgress = showProgress;
        mEpub = ePub;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        barProgressDialog = new ProgressDialog(context);
        barProgressDialog.setMessage("Descargando...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.setCancelable(false);
        if(showProgress)
            barProgressDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
        String fileFolder = strings[1];   // -> maven
        String fileName = strings[2];  // -> maven.pdf
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        //folder = new File(extStorageDirectory, "LPV_eBooks");
        folder = new File(extStorageDirectory, "LPV_eBooks/epub_reader/epubs/" + fileFolder);

        if(isStoragePermissionGranted(activity)) {
            if (folder.exists()) {
                pdfFile = new File(folder, fileName);
                if (pdfFile.exists()) {
                    /*intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(pdfFile);
                    intent.setDataAndType(uri, "application/pdf");
                    context.startActivity(intent);*/
                    success = true;
                    localStored = true;
                    permissionNotGranted = false;
                    return null;
                }
            } else {
                folder = new File(extStorageDirectory, "LPV_eBooks/epub_reader/epubs/" + fileFolder);
                folder.mkdir();
            }

            //folder = new File(extStorageDirectory, "LPV_eBooks/epub_reader/epubs/" + fileFolder);
            folder.mkdir();
            pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            success = downloadFile(fileUrl, pdfFile);
            localStored = false;
            permissionNotGranted = false;
        } else {
            permissionNotGranted = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(barProgressDialog.isShowing())
            barProgressDialog.dismiss();

        if(!permissionNotGranted) {
            if (success && !localStored) {

                mEpub.descargado = true;
                mEpub.favorito = false;
                if(db.insertBook(mEpub)) {
                    Log.d("ebookContent", "stored in db");
                }

            } else if (!success && !localStored) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Descarga fallida");
                alert.setMessage("Verifique su conexión a internet y que exista espacio suficiente en el dispositivo.");
                alert.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();

                DeleteRecursive(folder);
            }

            // Return to calling activity
            listener.onTaskCompleted();
        }
    }

    private void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            {
                child.delete();
                DeleteRecursive(child);
            }

        fileOrDirectory.delete();
    }

    public Boolean downloadFile(String fileUrl, File directory){
        final int  MEGABYTE = 1024 * 1024;
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while((bufferLength = inputStream.read(buffer))>0 ){
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 23)
            {
                if (checkPermission(activity))
                {
                    return true;
                } else {
                    requestPermission(activity); // Code for permission
                    return false;
                }
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
        return false;
    }

    public static boolean checkPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(context, "Habilita el permiso para guardar los archivos en tu dispositivo.", Toast.LENGTH_LONG).show();
            showExplanation("Permiso requerido", "Habilita el permiso para abrir los epubs en tu dispositivo.", android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE, activity);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public static void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{permission}, permissionRequestCode);
                    }
                });
        builder.create().show();
    }


}
