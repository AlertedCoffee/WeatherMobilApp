package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GetWeather.AsyncResponse {

    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=%D0%9A%D0%BE%D0%BF%D0%BE%D1%80%D1%8C%D0%B5&appid=f38e5ba35d6cb5b542711ce044c35e01&units=metric&lang=ru");
            new GetWeather(this).execute(url);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void processFinished(String output){
        Log.d(TAG, "processFinished: " + output);
    }

    @Override
    public void processError(String error){
        Log.d(TAG, "processError: "+ error);
        switch (error){
            case "Unable to resolve host \"api.openweathermap.org\": No address associated with hostname":
                Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}