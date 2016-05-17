package br.com.mafra.webservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView viewlist;
    private Button novo;
    private List<AuxAdapter> listaadap = new ArrayList<AuxAdapter>();
    private Context contesto;
    private MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contesto = this;
        ma = this;

        viewlist = (ListView) findViewById(R.id.viewlista);
        novo = (Button)findViewById(R.id.button);
        novo.setOnClickListener(nov);

        Boolean alar = PendingIntent.getService(this, 0, new Intent(this, Servico.class), PendingIntent.FLAG_NO_CREATE) == null;

        if(alar){

            Calendar calen = Calendar.getInstance();
            calen.setTimeInMillis(System.currentTimeMillis());

            Intent it = new Intent(this, Servico.class);
            PendingIntent iit = PendingIntent.getService(this, 0, it, 0);

            AlarmManager alarme = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, calen.getTimeInMillis(), 60000, iit);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PegaGet();
            }
        }).start();
    }

    View.OnClickListener nov = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent muda = new Intent(contesto, UpNewActivity.class);
            muda.putExtra("tipoB", "");
            contesto.startActivity(muda);
        }
    };

    public void PegaGet(){
        try {
            Conexao cx = new Conexao();
            String json = cx.Conec("get",0, null, null, null, false);

            listaadap = null;
            listaadap = cx.LerJson(json);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterLista ad = new AdapterLista(contesto, listaadap, ma);
                    viewlist.setAdapter(ad);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListView getViewlist() {
        return viewlist;
    }

}
