package it.stefanocasagrande.readxml;

import android.content.Context;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api {

    private Context c;
    private MainActivity activity;
    public static final int CLASS_VALORIORO = 2002;

    public Api(Context c, MainActivity activity)
    {
        this.c=c;
        this.activity = activity;
    }

    public interface ApiClass
    {
        public int GetApiClass();
    }

    public class ElencoValori implements ApiClass
    {
        public Classes.prices DatoCompleto;

        public ElencoValori(){}

        @Override
        public int GetApiClass() {
            return CLASS_VALORIORO;
        }
    }

    public ElencoValori Return_Valori_oro()
    {
        File var = c.getExternalFilesDir(null);
        ElencoValori var_elenco = new ElencoValori();

        if (var==null)
            return var_elenco;

        File FilesPath = new File(var.getAbsolutePath());
        String dir = FilesPath.toString() + "/";

        try {
            // Scarico XML
            DownloadXML(dir, "Valori.xml");
            // endregion

            // Deserializzo XML
            File source = new File(dir, "Valori.xml");
            Serializer serializer = new Persister();
            var_elenco.DatoCompleto = serializer.read(Classes.prices.class, source);
            // endregion
        }
        catch (Exception e)
        {
            String pippo=e.toString();
        }

        return var_elenco;
    }

    private void DownloadXML(String filepath, String filename) throws IOException {

        // Codice adattato partendo da codice su:
        // http://stackoverflow.com/questions/8986376/how-to-download-xml-file-from-server-and-save-it-in-sd-card

        URL url = new URL("https://oro.bullionvault.it/gold_prices_xml.do");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        File file = new File(filepath, filename);

        FileOutputStream fileOutput = new FileOutputStream(file);
        InputStream inputStream = urlConnection.getInputStream();
        int totalSize = urlConnection.getContentLength();
        int downloadedSize = 0;
        byte[] buffer = new byte[1024];
        int bufferLength = 0; //used to store a temporary size of the buffer
        while ( (bufferLength = inputStream.read(buffer)) > 0 )
        {
            fileOutput.write(buffer, 0, bufferLength);
            downloadedSize += bufferLength;
            int progress=(int)(downloadedSize*100/totalSize);
        }

        fileOutput.close();

    }

}
