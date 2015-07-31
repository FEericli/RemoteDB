package demo.ma390104.remotedb;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Eric Li on 2015/7/31.
 */
public class SendDataRunnable implements Runnable {

    private Handler handler;
    private String path;
    private HashMap<String, String> params;

    public SendDataRunnable(Handler handler, String path, HashMap<String, String> params) {
        this.handler = handler;
        this.path = path;
        this.params = params;
    }

    private String sendData(String target, HashMap<String, String> params){
        String responseData = "";
        try {
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //建立與目標之間的連線
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream stream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
            String c = composePostString(params);
            Log.d("int_sdr_54", c);
            writer.write(c);

            writer.flush();
            writer.close();
            stream.close();

            int httpResponseCode = connection.getResponseCode();
            if(httpResponseCode == HttpsURLConnection.HTTP_OK) {
                String oneLine = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((oneLine = reader.readLine()) != null) {
                    responseData += oneLine;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseData;
    }

    /* 組合POST參數 */
    private String composePostString(HashMap<String, String> params){
        String result = "";
        for(Map.Entry<String, String> entry : params.entrySet()){
            try {
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue(), "UTF-8");
                result += key + "=" + value + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.substring(0, result.length()-1);
    }

    @Override
    public void run() {
        String response = sendData(this.path, this.params);
        if(response.length() > 0) {
            //TODO
            handler.obtainMessage(0, response).sendToTarget();
        }else{
            //TODO
        }
    }

}


