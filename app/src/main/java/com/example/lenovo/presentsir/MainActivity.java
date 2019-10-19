package com.example.lenovo.presentsir;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Boolean.*;

public class MainActivity extends AppCompatActivity {
    Animation smalltobig, fortextview, forbtn;
    ImageView imageView;
    String scannedData;
    Button scan_btn;
    TextView textView1, textView2, textView3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.startAnimation(smalltobig);

        fortextview = AnimationUtils.loadAnimation(this, R.anim.fortextview);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 =(TextView) findViewById(R.id.textView3);
        textView1.startAnimation(fortextview);
        textView2.startAnimation(fortextview);
        textView3.startAnimation(fortextview);

        scan_btn = (Button) findViewById(R.id.scan_btn);
        forbtn = AnimationUtils.loadAnimation(this,R.anim.forbtn);
        scan_btn.startAnimation(forbtn);

        final Activity activity = this;

        scan_btn = (Button)findViewById(R.id.scan_btn);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan Student's QR Code here.");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            scannedData = result.getContents();
            if (scannedData != null)
            {
                new SendRequest().execute();
            }
            else {

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

                try {

                    //Enter script URL Here
                    URL url = new URL("https://script.google.com/macros/s/AKfycbw62Ojy5Mt4Txt9sWisdJGfjd4ZY9FiW5kvES6EvGoRuSohSEG-/exec");

                    JSONObject postDataParams = new JSONObject();

                    //int i;
                    //for(i=1;i<=70;i++)


                    //    String usn = Integer.toString(i);

                    //Passing scanned code as parameter

                    postDataParams.put("sdata", scannedData);


                    Log.e("params", postDataParams.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("Your attendance has been recorded successfully. ");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                    }
                } catch (Exception e) {
                    return new String("Failed to sent data. Please check your internet connection");
                }

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        }
    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
