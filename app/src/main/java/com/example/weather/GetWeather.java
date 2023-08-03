package com.example.weather;

import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;


public class GetWeather extends AsyncTask <URL, String, String> {

    public interface AsyncResponse{
        void processFinished(String output);
        void processError(String error);
    }

    public AsyncResponse response;

    public GetWeather (AsyncResponse response){
        this.response = response;
    }

    protected String getResponse(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if(scanner.hasNext()){
                return scanner.next();
            } else return null;
        }
        finally {
            connection.disconnect();
        }
    }

    private static final String TAG = "GetWeather";
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute: called");
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        response.processError(values[0]);
    }

    @Override
    protected String doInBackground(URL[] url) {
        String result = null;
        try {
            result = getResponse(url[0]);
        }
        catch (IOException e){
            e.printStackTrace();
            publishProgress(e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: called, ");
        response.processFinished(result);
    }
}
