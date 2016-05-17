package br.com.mafra.webservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leandro on 17/05/2016.
 */
public class Servico extends Service {

    private Context contesto;
    private int update = 0;
    private int constante = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contesto = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Checa();
        return(START_STICKY);
    }

    public void Checa(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://192.168.0.105:8080/WebServiceAppMaven/json/app/check");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String linha;
                    while((linha = br.readLine()) != null){
                        update = Integer.parseInt(linha);
                    }
                    conn.disconnect();

                    if(constante != update){
                        constante = update;
                        Publica();
                    }

                } catch (MalformedURLException e) {
                    conn.disconnect();
                    e.printStackTrace();
                } catch (IOException e) {
                    conn.disconnect();
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void Publica(){
        NotificationManager nota = (NotificationManager) getSystemService(contesto.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(contesto, 0, new Intent(contesto, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contesto);
        builder.setTicker("Novo Cadastro");
        builder.setContentTitle("Novo Cadastro");
        builder.setSmallIcon(R.drawable.icone_notificacao);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icone_notificacao));
        builder.setContentIntent(pi);


        Notification n = builder.build();
        /*************** notificação resumida **************/
        RemoteViews viewremotacontra;

        if(Build.VERSION.SDK_INT >= 21){
            viewremotacontra = new RemoteViews(getPackageName(), R.layout.layout_da_notificacao_contraida_api21);
            viewremotacontra.setTextViewText(R.id.textnotifcontraido21, "Novo Cadastro");
            viewremotacontra.setTextViewText(R.id.textonotificatitulo21, "Novo Cadastro");
            viewremotacontra.setTextColor(R.id.textnotifcontraido21, Color.rgb(0, 0, 0));
            viewremotacontra.setTextColor(R.id.textonotificatitulo21, Color.rgb(0, 0, 0));
        }else{
            viewremotacontra = new RemoteViews(getPackageName(), R.layout.layout_da_notificacao_contraida);
            viewremotacontra.setTextViewText(R.id.textnotifcontraido, "Novo Cadastro");
            viewremotacontra.setTextViewText(R.id.textonotificatitulo, "Novo Cadastro");
        }

        n.contentView = viewremotacontra;
        /********** notificação expandida *************/
        RemoteViews viewremotaexpand;

        if(Build.VERSION.SDK_INT >= 21){
            viewremotaexpand = new RemoteViews(getPackageName(), R.layout.layout_da_notificacao_expandido_api21);
            viewremotaexpand.setTextViewText(R.id.ntextnotifcontraido21, "Um novo cadastro foi feito");
            viewremotaexpand.setTextViewText(R.id.ntextonotificatitulo21, "Novo Cadastro");
            viewremotaexpand.setTextColor(R.id.ntextnotifcontraido21, Color.rgb(0, 0, 0));
            viewremotaexpand.setTextColor(R.id.ntextonotificatitulo21, Color.rgb(0, 0, 0));
        }else{
            viewremotaexpand = new RemoteViews(getPackageName(), R.layout.layout_da_notificacao_expandido);
            viewremotaexpand.setTextViewText(R.id.ntextnotifcontraido, "Um novo cadastro foi feito");
            viewremotaexpand.setTextViewText(R.id.ntextonotificatitulo, "Novo Cadastro");
        }

        n.bigContentView = viewremotaexpand;


        n.vibrate = new long[]{150, 300, 150, 300, 150, 300};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nota.notify((int) (Math.random() * 9999), n);

        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(contesto, som);
            toque.play();
        } catch (Exception e) {}
    }
}
