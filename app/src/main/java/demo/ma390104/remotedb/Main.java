package demo.ma390104.remotedb;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Eric Li on 2015/7/31.
 */
public class Main extends AppCompatActivity {

    HttpHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new HttpHandler(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("name", "tom");
        new Thread(
                new SendDataRunnable(handler, "http://192.168.0.106/test/test.php", params)
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
                Toast.makeText(main, (String)msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
