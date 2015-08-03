package demo.ma390104.remotedb;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Eric Li on 2015/7/31.
 */
public class Main extends AppCompatActivity {

    HttpHandler handler;
    TextView txvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        //TODO startTrans與startAsyncTask二選一執行
        //startTrans();
        //startAsyncTask();

    }

    private void findViews(){
        txvResponse = (TextView)findViewById(R.id.txvResponse);
    }

    //方法一：使用 Thread 配合 Runnable
    private void startTrans(){
        handler = new HttpHandler(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("name", "tom");
        //TODO 填入URL
        new Thread(
                new SendDataRunnable(handler, "http://www.example.com", params)
        ).start();
    }

    static class HttpHandler extends Handler {

        WeakReference<Activity> weakReference;

        public HttpHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Main main = (Main)weakReference.get();
            if(msg.what == 0) {
                //TODO 依what判斷後續處理
                main.txvResponse.setText((String)msg.obj);
            }
        }
    }

    //方法二：透過AsyncTask取回HTTP資源
    private void startAsyncTask(){
        //TODO 填入URL
        new DataTransAsyncTask().execute("http://www.example.com");
    }

    class DataTransAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                return downloadUrl(urls[0]);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            txvResponse.setText(s);
            Log.d("int_main_96", "onPostExecute");
        }

        private String downloadUrl(String url) throws IOException {
            InputStream is = null;
            String responseData = "";
            try {
                URL target = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)target.openConnection(); //建立與目標之間的連線
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                //connection.setInstanceFollowRedirects(false);

//                OutputStream stream = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
//                String c = composePostString(params);
//
//                writer.write(c);
//
//                writer.flush();
//                writer.close();
//                stream.close();

                int httpResponseCode = connection.getResponseCode();
                Log.d("int_main_108", "http=" + httpResponseCode);
                if(httpResponseCode == HttpsURLConnection.HTTP_OK) {
                    String oneLine = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((oneLine = reader.readLine()) != null) {
                        responseData += oneLine;
                    }
                }
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseData;
        }

    }

}
