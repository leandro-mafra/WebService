package br.com.mafra.webservice;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpNewActivity extends AppCompatActivity {
    private static final int CHECA_IMAGEM = 1;

    private ImageView imagem;
    private Button carrega;
    private Button salva;
    private EditText nome;
    private EditText descri;
    private EditText valor;
    private int id;
    private String nomeDoArquico = "";
    private String tipoDoArquivo = "";
    private boolean news = false;
    private Context contesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_new);
        contesto = this;

        Bundle bundle = getIntent().getExtras();

        imagem = (ImageView) findViewById(R.id.UNimagem);

        nome = (EditText) findViewById(R.id.UNnome);

        descri = (EditText) findViewById(R.id.UNdescri);

        valor = (EditText) findViewById(R.id.UNvalor);

        carrega = (Button) findViewById(R.id.UNCarrega);
        carrega.setOnClickListener(Carre);

        salva = (Button) findViewById(R.id.UNsalva);

        if(bundle.getString("tipoB").equals("atua")){
            id = bundle.getInt("idB");

            imagem.setImageBitmap((Bitmap) bundle.get("imagemB"));

            nome.setText(bundle.getString("nomeB"));

            descri.setText(bundle.getString("descriB"));

            valor.setText("" + bundle.getDouble("valorB"));

            nomeDoArquico = bundle.getString("nomeDoArquivo");

            tipoDoArquivo = bundle.getString("ArqTipoB");

            salva.setOnClickListener(Salve);

        }else{


            salva.setOnClickListener(atua);
        }
    }

    View.OnClickListener Carre = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, CHECA_IMAGEM);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECA_IMAGEM) {
            if (resultCode == RESULT_OK) {
                news = true;
                Uri imagemSelecionada = data.getData();
                String[] projeto = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(imagemSelecionada, projeto, null, null, null);
                cursor.moveToFirst();

                int indexColuna = cursor.getColumnIndex(projeto[0]);
                String PathImg = cursor.getString(indexColuna);

                indexColuna = cursor.getColumnIndex(projeto[1]);
                nomeDoArquico = cursor.getString(indexColuna);
                cursor.close();

                tipoDoArquivo = nomeDoArquico.substring(nomeDoArquico.length()-3);
                if(tipoDoArquivo.equals("jpg")){
                    tipoDoArquivo = "image/jpeg";
                }else{
                    tipoDoArquivo = "image/png";
                }

                Bitmap image = BitmapFactory.decodeFile(PathImg);
                imagem.setImageBitmap(image);
                imagem.refreshDrawableState();

            }
        }
    }

    View.OnClickListener Salve = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String motodo = (String) v.getTag();
                JSONObject jo = new JSONObject();
                JSONArray ja = new JSONArray();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap im = ((BitmapDrawable)imagem.getDrawable()).getBitmap();

                if(tipoDoArquivo.equals("image/jpeg")){
                    im.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }else {
                    im.compress(Bitmap.CompressFormat.PNG, 100, out);
                }

                final byte[] OutPutByteArray;
                final String referencia;
                if(news){
                    OutPutByteArray = out.toByteArray();
                    int re = (int) (Math.random()*100);
                    referencia = ""+OutPutByteArray.hashCode()+re;
                    jo.put("referencia", referencia);
                }else{
                    OutPutByteArray = new byte[]{-1};
                    referencia = "";
                }


                jo.put("id", id);
                jo.put("nome", nome.getText().toString());
                jo.put("descricao", descri.getText().toString());
                jo.put("nomeDoArquivo", nomeDoArquico);
                jo.put("tipo", tipoDoArquivo);
                double val = Double.parseDouble(valor.getText().toString());
                jo.put("valor", val);
                JSONArray jaa = new JSONArray();
                jo.put("lista", jaa);


                ja.put(jo);

                JSONObject joo = new JSONObject();
                joo.put("lista", ja);

                final String jsonOut = joo.toString();

                final String[] JJson = {jsonOut, referencia};

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean corri = true;
                        int cont = 0;
                        while (corri || cont < 2){
                            corri = false;
                            cont++;
                            System.out.println("jijijijijijijijijijijijijijijij");
                            Conexao cx = new Conexao();
                            try {
                                cx.Conec("post", id, OutPutByteArray, jsonOut, referencia, news);
                                Intent it = new Intent(contesto, MainActivity.class);
                                contesto.startActivity(it);
                            } catch (IOException e) {
                                corri = true;
                                e.printStackTrace();
                            } catch (JSONException e) {
                                corri = true;
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener atua = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String motodo = (String) v.getTag();
                JSONObject jo = new JSONObject();
                JSONArray ja = new JSONArray();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap im = ((BitmapDrawable)imagem.getDrawable()).getBitmap();

                if(tipoDoArquivo.equals("image/jpeg")){
                    im.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }else {
                    im.compress(Bitmap.CompressFormat.PNG, 100, out);
                }

                final byte[] OutPutByteArray;
                final String referencia;
                if(news){
                    OutPutByteArray = out.toByteArray();
                    int re = (int) (Math.random()*100);
                    referencia = ""+OutPutByteArray.hashCode()+re;
                    jo.put("referencia", referencia);
                }else{
                    OutPutByteArray = new byte[]{-1};
                    referencia = "";
                }


                jo.put("id", id);
                jo.put("nome", nome.getText().toString());
                jo.put("descricao", descri.getText().toString());
                jo.put("nomeDoArquivo", nomeDoArquico);
                jo.put("tipo", tipoDoArquivo);
                double val = Double.parseDouble(valor.getText().toString());
                jo.put("valor", val);
                JSONArray jaa = new JSONArray();
                jo.put("lista", jaa);


                ja.put(jo);

                JSONObject joo = new JSONObject();
                joo.put("lista", ja);

                final String jsonOut = joo.toString();

                final String[] JJson = {jsonOut, referencia};

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean corri = true;
                        int cont = 0;
                        while (corri || cont < 2){
                            corri = false;
                            cont++;
                            System.out.println("jijijijijijijijijijijijijijijij");
                            Conexao cx = new Conexao();
                            try {
                                cx.Conec("salva", id, OutPutByteArray, jsonOut, referencia, news);
                                Intent it = new Intent(contesto, MainActivity.class);
                                contesto.startActivity(it);
                            } catch (IOException e) {
                                corri = true;
                                e.printStackTrace();
                            } catch (JSONException e) {
                                corri = true;
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
