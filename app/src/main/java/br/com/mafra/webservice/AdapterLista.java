package br.com.mafra.webservice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Leandro on 22/04/2016.
 */
public class AdapterLista extends BaseAdapter {

    private Context contesto;
    private List<AuxAdapter> listaadap;
    private MainActivity ma;

    public AdapterLista(Context contesto, List<AuxAdapter> listaadap, MainActivity ma){
        this.contesto = contesto;
        this.listaadap = listaadap;
        this.ma = ma;
    }

    @Override
    public int getCount() {
        return listaadap.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return listaadap.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout;

        AuxAdapter aux = listaadap.get(position);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contesto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.layout_adapter, null);
        }else {
            layout = convertView;
        }

        TextView nome = (TextView) layout.findViewById(R.id.adpnome);
        nome.setText(listaadap.get(position).getNome());

        TextView descricao = (TextView) layout.findViewById(R.id.adapdescri);
        descricao.setText(listaadap.get(position).getDescricao());

        TextView valor = (TextView) layout.findViewById(R.id.adapvalor);
        valor.setText(""+listaadap.get(position).getValor());

        ImageView imagem = (ImageView) layout.findViewById(R.id.adapima);
        imagem.setImageBitmap(listaadap.get(position).getImagem());

        Button atualiza = (Button) layout.findViewById(R.id.btnatu);
        atualiza.setTag(position);
        atualiza.setOnClickListener(atuali);

        Button excluir = (Button) layout.findViewById(R.id.btnexclu);
        excluir.setTag(listaadap.get(position).getId());
        excluir.setOnClickListener(exclu);

        return layout;
    }

    View.OnClickListener atuali = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int posi = (int) v.getTag();

            try {

                String ss = listaadap.get(posi).getTipo();

            Intent muda = new Intent(contesto, UpNewActivity.class);
            muda.putExtra("idB", listaadap.get(posi).getId());
            muda.putExtra("imagemB", listaadap.get(posi).getImagem());
            muda.putExtra("nomeB", listaadap.get(posi).getNome());
            muda.putExtra("descriB", listaadap.get(posi).getDescricao());
            muda.putExtra("valorB", listaadap.get(posi).getValor());
            muda.putExtra("nomeDoArquivo", listaadap.get(posi).getNomeDoArquivo());
            muda.putExtra("ArqTipoB", listaadap.get(posi).getTipo());
            muda.putExtra("tipoB", "atua");
            contesto.startActivity(muda);
        }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener exclu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = (int) v.getTag();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Conexao cx = new Conexao();
                        String json = null;
                        json = cx.Conec("del", id, null, null, null, false);

                        listaadap = null;
                        listaadap = cx.LerJson(json);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ma.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AdapterLista adap = new AdapterLista(contesto, listaadap, ma);
                            ma.getViewlist().setAdapter(adap);
                            ma.getViewlist().refreshDrawableState();
                        }
                    });
                }
            }).start();
        }
    };
}
