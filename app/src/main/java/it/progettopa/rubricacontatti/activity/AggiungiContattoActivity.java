package it.progettopa.rubricacontatti.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.progettopa.rubricacontatti.R;

/**
 * Created on 25/03/16.
 */
public class AggiungiContattoActivity extends AppCompatActivity {

    private Button btnInserisci;
    private InserisciContattoTask task;
    private JSONObject nuovoContattoJson;
    private EditText etNome, etCognome, etTelefono, etEmail;
    private String nome, cognome, telefono, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_contatto);
        btnInserisci = (Button) findViewById(R.id.btnInserisci);
        etCognome = (EditText) findViewById(R.id.etCognome);
        etNome = (EditText) findViewById(R.id.etNome);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etEmail = (EditText) findViewById(R.id.etEmail);

        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button", "inserisci contatto");
                nuovoContattoJson = new JSONObject();
                try {
                    nuovoContattoJson.put("nome", etNome.getText().toString());
                    nuovoContattoJson.put("cognome", etCognome.getText().toString());
                    nuovoContattoJson.put("telefono", etTelefono.getText().toString());
                    nuovoContattoJson.put("email", etEmail.getText().toString());

                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Errore", e);
                }

                task = new InserisciContattoTask();
                task.execute(getString(R.string.url_contatti));

            }
        });
    }

    //task per inserire un contatto
    private class InserisciContattoTask extends AsyncTask<String, Void, Integer> {

        protected void onPreExecute() {
        }

        protected Integer doInBackground(String... urls) {

            int response = -1;
            try {
                response = requestPut(urls[0]);

            } catch (IOException e) {
                //facciamo qualcosa in caso di errore
                Log.e(getLocalClassName(), "Errore", e);
            }

            return response;
        }

        protected void onPostExecute(Integer result) {
            Log.d("response code server", "-> " + result);
            if (result == 200) {
                //ok il contatto è stato inserito
                CharSequence text = "Contatto inserito";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(AggiungiContattoActivity.this, text, duration);
                toast.show();
            }
        }
    }


    private int requestPut(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        //passiamo il body
        String str = nuovoContattoJson.toString();
        byte[] outputInBytes = str.getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(outputInBytes);
        os.close();
        // Starts the query
        conn.connect();
        return conn.getResponseCode();
    }

}
