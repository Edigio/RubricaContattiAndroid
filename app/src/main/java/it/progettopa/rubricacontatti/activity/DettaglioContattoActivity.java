package it.progettopa.rubricacontatti.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.progettopa.rubricacontatti.R;

/**
 * Created on 14/03/16.
 */
public class DettaglioContattoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;


    private TextView tvNome, tvCognome, tvEmail, tvTelefono;
    private ImageView ivContatto;
    private String nome, cognome, email, telefono;
    private long id;
    //bottone per l'eliminazione del contatto
    private Button btnCancella;
    private String mCurrentPhotoPath;
    //task
    private CancellaContattoTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_contatto);
        tvNome = (TextView) findViewById(R.id.tvNome);
        tvCognome = (TextView) findViewById(R.id.tvCognome);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvTelefono = (TextView) findViewById(R.id.tvTelefono);
        btnCancella = (Button) findViewById(R.id.btnCancella);
        ivContatto = (ImageView) findViewById(R.id.ivContatto);

        //preniamo i parametri dall'intent
        if (getIntent() != null) {
            id = getIntent().getLongExtra(MainActivity.KEY_ID, -1);
            email = getIntent().getStringExtra(MainActivity.KEY_EMAIL);
            telefono = getIntent().getStringExtra(MainActivity.KEY_TELEFONO);
            nome = getIntent().getStringExtra(MainActivity.KEY_NOME);
            cognome = getIntent().getStringExtra(MainActivity.KEY_COGNOME);
            // e li settiamo nelle textview
            tvNome.setText(nome);
            tvCognome.setText(cognome);
            tvEmail.setText(email);
            tvTelefono.setText(telefono);
        }
        //controllo sull'id
        if (id != -1) {
            btnCancella.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Button", "elimina contatto");
                    task = new CancellaContattoTask();
                    task.execute(getString(R.string.url_contatti));
                }
            });
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mCurrentPhotoPath = sharedPref.getString(id + "", "");
        //se abbiamo l'immagine in memoria la settiamo altrimenti diamo la possibilità di scattare una foto
        if (!"".equals(mCurrentPhotoPath)) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ivContatto.setImageBitmap(Bitmap.createScaledBitmap(myBitmap,myBitmap.getWidth()/4,myBitmap.getHeight()/4,false));
        } else {
            //settiamo un click sull'immagine
            ivContatto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });
        }

    }

    //metodo che lancia un intent per la fotocamera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // assicuriamoci che il device ha la possibilità di scattare una foto
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(getLocalClassName(), "Errore", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crea un file jpeg
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.v("path img", "-> " + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //salviamo la foto e poi settiamola nell'immagine
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(id + "", mCurrentPhotoPath);
            editor.commit();

            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ivContatto.setImageBitmap(Bitmap.createScaledBitmap(myBitmap,myBitmap.getWidth()/4,myBitmap.getHeight()/4,false));
        }
    }


    //task per l'eliminazione di un contatto
    private class CancellaContattoTask extends AsyncTask<String, Void, Integer> {

        protected void onPreExecute() {
        }

        protected Integer doInBackground(String... urls) {

            int response = -1;
            try {
                response = requestDelete(urls[0]);

            } catch (IOException e) {
                //facciamo qualcosa in caso di errore
                Log.e(getLocalClassName(), "Errore", e);
            }

            return response;
        }

        protected void onPostExecute(Integer result) {
            Log.d("response code server", "-> " + result);
            if (result == 200) {
                //ok il contatto è stato eliminato
                Intent resultData = new Intent();
                resultData.putExtra(MainActivity.KEY_ID, id);
                setResult(Activity.RESULT_OK, resultData);
                finish();
            }
        }
    }


    private int requestDelete(String urlString) throws IOException {
        //aggiungiamo l'id alla URL
        URL url = new URL(urlString + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("DELETE");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getResponseCode();
    }

}
