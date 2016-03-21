package it.progettopa.rubricacontatti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.progettopa.rubricacontatti.R;
import it.progettopa.rubricacontatti.model.ContattoVO;

/**
 * Created on 14/03/16.
 */
public class ContattiAdapter extends ArrayAdapter<ContattoVO> {


    public ContattiAdapter(Context context, List<ContattoVO> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // prendiamo l'item in una determinata posizione
        ContattoVO contattoVO = getItem(position);
        // Check sul riutilizzo delle celle
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lv_contatti, parent, false);
        }

        TextView tvNome = (TextView) convertView.findViewById(R.id.tvNomeContatto);
        TextView tvCognome = (TextView) convertView.findViewById(R.id.tvCognomeContatto);
        //popoliamo le textview
        tvNome.setText(contattoVO.getNome());
        tvCognome.setText(contattoVO.getCognome());
        // Ritorniamo la view completa
        return convertView;
    }

}
