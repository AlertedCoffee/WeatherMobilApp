package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GetWeather.AsyncResponses {

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
        try {
            JSONObject jsonResult = new JSONObject(output);
            JSONArray jsonArray = (JSONArray) jsonResult.get("weather");

            JSONObject weather = jsonArray.getJSONObject(0);
            JSONObject main = jsonResult.getJSONObject("main");
            JSONObject sys = jsonResult.getJSONObject("sys");
            String name = jsonResult.getString("name");

            Locale locale = new Locale("ru", "RU");
            SimpleDateFormat format = new SimpleDateFormat("HH.mm.ss", locale);
            Long timezone = Long.parseLong(jsonResult.getString("timezone")) * 1000;
            String dateString = format.format(new Date(Long.parseLong(sys.getString("sunrise"))));

            TextView textView = findViewById(R.id.textView);
            textView.setText(dateString);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

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