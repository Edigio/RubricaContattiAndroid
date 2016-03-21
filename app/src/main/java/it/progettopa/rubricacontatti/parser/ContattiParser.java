package it.progettopa.rubricacontatti.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.progettopa.rubricacontatti.model.ContattoVO;

/**
 * Created on 11/03/16.
 */
public class ContattiParser {

    private final String TAG_NOME = "nome";
    private final String TAG_COGNOME = "cognome";
    private final String TAG_TELEFONO = "telefono";
    private final String TAG_EMAIL = "email";
    private final String TAG_ID = "id";


    public List<ContattoVO> getListaContatti(String result) {

        ArrayList<ContattoVO> listaContatti = new ArrayList<ContattoVO>();
        Log.d("Json contatti", "-> " + result);

        if (result != null) {
            try {

                // Getting JSON Array
                JSONArray contacts = new JSONArray(result);

                //leggiamoci tutti i contatti
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String nome = c.getString(TAG_NOME);
                    String cognome = c.getString(TAG_COGNOME);
                    String telefono = c.getString(TAG_TELEFONO);
                    String email = c.getString(TAG_EMAIL);
                    long id = c.getLong(TAG_ID);

                    // tmp per il singolo contatto
                    ContattoVO contatto = new ContattoVO();

                    contatto.setNome(nome);
                    contatto.setCognome(cognome);
                    contatto.setEmail(email);
                    //id è un long
                    contatto.setId(id);
                    contatto.setTelefono(telefono);

                    // aggiungiamo il contatto alla lista
                    listaContatti.add(contatto);
                }
            } catch (JSONException e) {
                Log.e(getClass().getName(), "Errore", e);
            }
        }

        return listaContatti;
    }
}
