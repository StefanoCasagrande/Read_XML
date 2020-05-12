package it.stefanocasagrande.readxml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txt_risultato;
    TextView txt_data_ora;
    Date scarico;
    Api.ElencoValori var_elenco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Variabili.OroLiveApi = new Api(getApplicationContext(),this);
        txt_risultato = findViewById(R.id.txt_risultato);
        txt_data_ora = findViewById(R.id.txt_data_ora);

        Button btn_aggiorna = findViewById(R.id.btn_aggiorna);
        btn_aggiorna.setTag(Variabili.BTN_AGGIORNA);
        btn_aggiorna.setOnClickListener(this);

        Aggiorna_Valori();
    }

    @Override
    public void onClick(View v) {
        int tag = (Integer) v.getTag();

        switch (tag) {
            case Variabili.BTN_AGGIORNA:
                Aggiorna_Valori();
                break;
        }
    }

    public void Aggiorna_Valori()
    {
        if (isNetworkAvailable())
            new RequestTask().execute(Variabili.GET_VALUTAZIONI);
        else
            Toast.makeText(this, getString(R.string.No_Signal), Toast.LENGTH_LONG).show();
    }

    //region NetworkCheck

    public boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager==null)
            return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    //endregion

    public void setLoading(final boolean status)
    {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setProgressBarIndeterminateVisibility(status);
                    setProgressBarVisibility(status);
                }
            });

        }
        catch (Exception ex)
        {
            Toast.makeText(this, getString(R.string.Download_Error), Toast.LENGTH_LONG).show();
        }
    }

    class RequestTask extends AsyncTask<Integer, Void, Api.ApiClass> {

        @Override
        protected Api.ApiClass doInBackground(Integer... params) {
            setLoading(true);
            switch (params[0])
            {
                case Variabili.GET_VALUTAZIONI:
                    return Variabili.OroLiveApi.Return_Valori_oro();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Api.ApiClass response) {
            setLoading(false);
            int classType = -1;
            try {
                classType = response.GetApiClass();
            }
            catch (Exception ex)
            {
                return;
            }
            switch (classType)
            {
                case Api.CLASS_VALORIORO:

                    scarico = new Date();
                    var_elenco = (Api.ElencoValori)response;

                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    String messaggio = String.format("%s <br><strong>%s</strong>", getString(R.string.Last_Update),  df.format(scarico));
                    txt_data_ora.setText(Html.fromHtml(messaggio));

                    List<String> lista_valori = new ArrayList<>();

                    for (Classes.pitch lista : var_elenco.DatoCompleto.pitch) {
                        for (Classes.price var2 : lista.price) {

                            String valore_singolo= String.format("%s %s / %s %s",  getString(R.string.Stock_Exchange), lista.securityId.substring(3,5),  getString(R.string.Product), lista.securityId.substring(0,3));

                            if (var2.actionIndicator.toUpperCase().equals("B"))
                                valore_singolo+= " Buy ";
                            else
                                valore_singolo+= " Sell ";

                            valore_singolo +=String.format("%s %s / %s", var2.limit_kg/1000, lista.considerationCurrency, getString(R.string.gram));

                            lista_valori.add(valore_singolo);
                        }
                    }

                    txt_risultato.setText(TextUtils.join(System.getProperty("line.separator"), lista_valori));


                    break;
            }
        }
    }
}
