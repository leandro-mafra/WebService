package br.com.mafra.webservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leandro on 24/04/2016.
 */
public class Conexao {

    private HttpURLConnection conn;

    public Conexao(){}
    private String ip = "http://192.168.0.105:8080/";

    public String Conec(String metodo, int id, byte[] outputbytearray, String jsonOut, String referencia, boolean news) throws MalformedURLException, IOException, JSONException {
        URL url;
        StringBuilder json = new StringBuilder();

        if(metodo.equals("get")){
            String get = ip+"WebServiceAppMaven/json/app/get";
            url =  new URL(get);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String linha;
            while((linha = br.readLine()) != null){
                json.append(linha);
            }
            conn.connect();
        }else if(metodo.equals("post")){
            String post = ip+"WebServiceAppMaven/json/app/post";
            url =  new URL(post);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(jsonOut.length());
            conn.connect();

            OutputStreamWriter outputwrite = new OutputStreamWriter(conn.getOutputStream());
            outputwrite.write(jsonOut);
            outputwrite.flush();
            outputwrite.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String linha;
            while((linha = br.readLine()) != null){
                json.append(linha);
            }
            conn.disconnect();

            if(news){
                post = ip+"WebServiceAppMaven/pandroidima/"+outputbytearray.length+"/"+referencia;
                url =  new URL(post);
                conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "image/jpg");
                conn.setFixedLengthStreamingMode(outputbytearray.length);
                conn.connect();


                OutputStream imagem = conn.getOutputStream();
                imagem.write(outputbytearray);
                imagem.flush();
                imagem.close();

                byte[] by = new byte[1];
                InputStream inp = conn.getInputStream();
                inp.read(by);
                inp.close();
                conn.disconnect();
            }
        }else if(metodo.equals("del")){
            String del = ip+"WebServiceAppMaven/json/app/del";
            url =  new URL(del);
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();

            jo.put("id", id);
            ja.put(0, jo);
            JSONObject joo = new JSONObject();
            joo.put("lista", ja);

            conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setFixedLengthStreamingMode(joo.toString().length());
            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            System.out.println(joo.toString());
            out.write(joo.toString());
            out.flush();
            out.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String linha;
            while((linha = br.readLine()) != null){
                json.append(linha);
            }

            conn.disconnect();
        }else if(metodo.equals("salva")){
            String post = ip+"WebServiceAppMaven/json/app/salva";
            url =  new URL(post);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(jsonOut.length());
            conn.connect();

            OutputStreamWriter outputwrite = new OutputStreamWriter(conn.getOutputStream());
            outputwrite.write(jsonOut);
            outputwrite.flush();
            outputwrite.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String linha;
            while((linha = br.readLine()) != null){
                json.append(linha);
            }
            conn.disconnect();

            if(news){
                post = ip+"WebServiceAppMaven/pandroidima/"+outputbytearray.length+"/"+referencia;
                url =  new URL(post);
                conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "image/jpg");
                conn.setFixedLengthStreamingMode(outputbytearray.length);
                conn.connect();


                OutputStream imagem = conn.getOutputStream();
                imagem.write(outputbytearray);
                imagem.flush();
                imagem.close();

                byte[] by = new byte[1];
                InputStream inp = conn.getInputStream();
                inp.read(by);
                inp.close();
                conn.disconnect();
            }
        }

        return json.toString();
    }

    public List<AuxAdapter> LerJson(String json){
        List<AuxAdapter> li = new ArrayList<AuxAdapter>();
        try {
            JSONObject jo = new JSONObject(json);
            JSONArray ja = jo.getJSONArray("lista");

            for(int i = 0 ; i < ja.length() ; i++ ){
                AuxAdapter adp = new AuxAdapter();
                jo = null;
                jo = ja.getJSONObject(i);

                adp.setId(jo.getInt("id"));
                adp.setNome(jo.getString("nome"));
                adp.setDescricao(jo.getString("descricao"));
                adp.setValor(jo.getDouble("valor"));
                adp.setImagem(Carregaimagem(jo.getInt("id")));
                adp.setNomeDoArquivo(jo.getString("nomeDoArquivo"));
                adp.setTipo(jo.getString("tipo"));

                li.add(adp);

            }

        } catch (JSONException e) {
            li.clear();
            e.printStackTrace();
        }

        return li;
    }

    public Bitmap Carregaimagem(int id){
        Bitmap ima = null;
        HttpURLConnection co = null;
        try{
            URL url = new URL(ip+"WebServiceAppMaven/androidima/"+id);
            co = (HttpURLConnection) url.openConnection();
            co.setUseCaches(false);
            co.setDoInput(true);
            //co.setDoOutput(false);
            co.setRequestMethod("GET");
            co.connect();
            InputStream in = co.getInputStream();
            ima = BitmapFactory.decodeStream(in);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            co.disconnect();
        }
        return ima;
    }

}
