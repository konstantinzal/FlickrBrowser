package com.peekily.flickrbrowser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousCloseException;

public class MainActivity extends AppCompatActivity {

    String URL = "https://api.flickr.com/services/feeds/photos_public.gne?tags=lollipo&format=json&nojsoncallback=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadJsonFlickr().execute(URL);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private class DownloadJsonFlickr extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e){
                return "Unable to retreive Webpage";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Downloads", "The Flicr response is: " + result);
            Log.d("Downloads", "The Flicr response has a lenght of : " + result.length());
        }
    }

    private String downloadUrl(String myUrl) throws IOException{
        InputStream is = null;
        int len = 500;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /*milliseconds*/);
            conn.setRequestMethod("GET");

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Download", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a String
            String contentAsString = readIt(is, len);
            return contentAsString;

        }
         finally{
            if (is!=null){
                is.close();
            }
        }
    }


    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        StringBuilder tempBuffer = new StringBuilder();
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        while(true){
            int value = reader.read(buffer);
            if(value <0){
                break;
            }
            tempBuffer.append(buffer);
        }
        return new String(tempBuffer);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
