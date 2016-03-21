package it.progettopa.rubricacontatti.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import it.progettopa.rubricacontatti.R;
import it.progettopa.rubricacontatti.adapter.ContattiAdapter;
import it.progettopa.rubricacontatti.model.ContattoVO;
import it.progettopa.rubricacontatti.parser.ContattiParser;

public class MainActivity extends AppCompatActivity {

    static final int DELETE_CONTACT_REQUEST = 100;  // The request code

    public static final String KEY_NOME = "key_nome";
    public static final String KEY_COGNOME = "key_cognome";
    public static final String KEY_TELEFONO = "key_telefono";
    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_ID = "key_id";

    private ListView lvContatti;
    private ContattiAdapter contattiAdapter;
    private List<ContattoVO> listaContatti;
    private DownloadContattiTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //riferimento alla listView
        lvContatti = (ListView) findViewById(R.id.lvContatti);
        //listener sul click di un elemento
        lvContatti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //prendiamo il contatto alla posizione i
                ContattoVO contattoSelezionato = listaContatti.get(i);

                Intent intent = new Intent(MainActivity.this, DettaglioContattoActivity.class);
                intent.putExtra(KEY_NOME, contattoSelezionato.getNome());
                intent.putExtra(KEY_COGNOME, contattoSelezionato.getCognome());
                intent.putExtra(KEY_EMAIL, contattoSelezionato.getEmail());
                intent.putExtra(KEY_TELEFONO, contattoSelezionato.getTelefono());
                intent.putExtra(KEY_ID, contattoSelezionato.getId());
                startActivityForResult(intent, DELETE_CONTACT_REQUEST);
            }
        });

        //inizia il download
        task = new DownloadContattiTask();
        task.execute(getString(R.string.url_contatti));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_contact:
                //start dell'activity per aggiungere un contatto
                return true;

        }
        return true;
    }


    private class DownloadContattiTask extends AsyncTask<String, Void, Void> {
        private ContattiParser contattiParser = new ContattiParser();

        protected Void doInBackground(String... urls) {

            try {
                InputStream stream = requestGet(urls[0]);
                //abbiamo l'input stream, bufferiziamolo
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                //leggiamo il buffer e portiamo tutto su una stringa
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                listaContatti = contattiParser.getListaContatti(responseStrBuilder.toString());


            } catch (IOException e) {
                //facciamo qualcosa in caso di errore
                Log.e(getLocalClassName(), "Errore", e);
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            //abbiamo letto i dati ora possiamo popolare la lista
            if (listaContatti != null) {
                contattiAdapter = new ContattiAdapter(MainActivity.this, listaContatti);
                lvContatti.setAdapter(contattiAdapter);
            } else {
                //mostriamo un messaggio di errore
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Attenzione")
                        .setMessage("Problema di connessione al server riprovare più tardi")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //chiudi l'activity
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        }
    }

    // facciamo una chiamata Http di tipo GET sulla url in parametro
    private InputStream requestGet(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // risposta
        if (requestCode == DELETE_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(KEY_ID, -1);
                //andiamo a cancellare dalla lista l'elemento con questo id
                boolean trovato = false;
                int count = 0;
                while (!trovato && listaContatti.size() > count) {
                    if (listaContatti.get(count).getId() == id) {
                        trovato = true;
                        listaContatti.remove(count);
                    }
                    count++;
                }
                //ora che lo abbiamo cancellato dalla lista aggiorniamo il nostro adapter
                contattiAdapter.notifyDataSetChanged();
            }
        }
    }

   /* private int requestPut(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 *//* milliseconds *//*);
        conn.setConnectTimeout(15000 *//* milliseconds *//*);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        //passiamo il body
        String str =  "{\"nome\":\"Luca\",\"cognome\":\"Verdi\",\"telefono\":\"366436\",\"email\":\"microssi@gmail.com\"}";
        byte[] outputInBytes = str.getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(outputInBytes);
        os.close();
        // Starts the query
        conn.connect();
        return conn.getResponseCode();
    }*/

}
