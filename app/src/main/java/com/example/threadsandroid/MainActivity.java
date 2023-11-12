package com.example.threadsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView txtView;
    ImageView imgView;
    Handler handler = new Handler(Looper.getMainLooper());
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = findViewById(R.id.txtView);
        imgView = findViewById(R.id.imageView);

        Button btn = (Button) findViewById(R.id.btnCrida);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromUrl("https://randomfox.ca/floof/");
            }
        });
    }

    private void getDataFromUrl(String demoIdUrl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String error = ""; // string field
                String result = null;
                int resCode;
                InputStream in;

                try {
                    URL url = new URL(demoIdUrl);
                    URLConnection urlConn = url.openConnection();
                    HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
                    httpsConn.setAllowUserInteraction(false);
                    httpsConn.setInstanceFollowRedirects(true);
                    httpsConn.setRequestMethod("GET");
                    httpsConn.connect();
                    resCode = httpsConn.getResponseCode();


                    if (resCode == HttpURLConnection.HTTP_OK) {
                        in = httpsConn.getInputStream();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                in, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        in.close();
                        result = sb.toString();
                        Log.i("INFO", result);
                        String urldisplay = "https://randomfox.ca/images/122.jpg";
                        InputStream in2 = new java.net.URL(urldisplay).openStream();
                        bitmap = BitmapFactory.decodeStream(in2);
                        in2.close();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                txtView.setText("Image loaded successfully!");
                                try {
                                    imgView.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    Log.e("Error", "Error loading image: "+e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        error += resCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
