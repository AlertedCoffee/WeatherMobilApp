package com.example.weather;

import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;


public class GetWeather extends AsyncTask <URL, String, String> {

    public interface AsyncResponses{
        void processFinished(String output);
        void processError(String error);
    }

    public AsyncResponses response;

    public GetWeather (AsyncResponses response){
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
    }

    @Override
    protected void onProgressUpdate(String... values) {
        response.processError(values[0]);
    }

    @Override
    protected String doInBackground(URL[] url) {
        String result = null;
        try {
            result = getResponse(url[0]);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            publishProgress("404");
        }
        catch (IOException e){
            e.printStackTrace();
            publishProgress(e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        response.processFinished(result);
    }
}
