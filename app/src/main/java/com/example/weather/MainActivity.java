package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GetWeather.AsyncResponses {

    private static final String TAG = "MainActivity";

    TextView townName;
    TextView description;
    TextView temp;
    TextView feelsTemp;
    TextView wind;
    TextView sunrise;
    TextView sunset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        townName = findViewById(R.id.townNameTextView);
        description = findViewById(R.id.weatherValueTextView);
        temp = findViewById(R.id.tempValueTextView);
        feelsTemp = findViewById(R.id.feelsTempValueTextView);
        wind = findViewById(R.id.windSpeedValueTextView);
        sunrise = findViewById(R.id.sunriseValueTextView);
        sunset = findViewById(R.id.sunsetValueTextView);


        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Москва&appid=f38e5ba35d6cb5b542711ce044c35e01&units=metric&lang=ru");
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
            JSONObject jWind = jsonResult.getJSONObject("wind");
            JSONObject sys = jsonResult.getJSONObject("sys");
            String name = jsonResult.getString("name");

            townName.setText(name);
            description.setText(weather.getString("description"));
            temp.setText(main.getString("temp"));
            feelsTemp.setText(main.getString("feels_like"));
            wind.setText(jWind.getString("speed") + " м/c");


            Locale locale = new Locale("ru", "RU");
            SimpleDateFormat format = new SimpleDateFormat("HH.mm.ss", locale);
            String dateString = format.format(new Date(Long.parseLong(sys.getString("sunrise")) * 1000));
            sunrise.setText(dateString);

            dateString = format.format(new Date(Long.parseLong(sys.getString("sunset")) * 1000));
            sunset.setText(dateString);


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