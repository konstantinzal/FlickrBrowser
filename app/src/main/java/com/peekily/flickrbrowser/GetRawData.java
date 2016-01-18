package com.peekily.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shevatek on 1/18/16.
 */

enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

public class GetRawData {
    private String LOG_TAG = GetRawData.class.getSimpleName();
    private String mRawUrl;
    private String mData;
    private DownloadStatus mDownloadStatus;

    public GetRawData(String mRawUrl) {
        this.mRawUrl = mRawUrl;
        this.mDownloadStatus = DownloadStatus.IDLE;
    }

    public void reset(){
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mRawUrl = null;
        this.mData = null;
    }

    public String getmData() {
        return mData;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public void setmRawUrl(String mRawUrl) {
        this.mRawUrl = mRawUrl;
    }

    public void execute() {
        this.mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(mRawUrl);

    }


    public class DownloadRawData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(params[0] == null)
                return null;


            try {
                return downloadUrl(params[0]);
            } catch (IOException e){
                Log.d(LOG_TAG, "Error; " + e);
                return "Unable to retreive Webpage";
            }
        }

        @Override
        protected void onPostExecute(String webData) {
            mData = webData;
            if(mData == null){
                if(mRawUrl == null){
                    mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
                } else {
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
            } else {
                // SUCCESSS
                mDownloadStatus = DownloadStatus.OK;
//                Log.e(LOG_TAG,"Result of rawData is: " + mData);
            }
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
            Log.d(LOG_TAG, "The response is: " + response);
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
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line = reader.readLine()) != null){
            tempBuffer.append(line + "\n");
        }

        return new String(tempBuffer);
    }
}
