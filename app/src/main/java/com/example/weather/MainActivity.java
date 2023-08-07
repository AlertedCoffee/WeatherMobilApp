package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GetWeather.AsyncResponses {

    private static final String TAG = "MainActivity";

    private TextView townName;
    private TextView description;
    private TextView temp;
    private TextView feelsTemp;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;

    private String city;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_CITY = "city";
    private SharedPreferences settings;

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

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        city = settings.getString(APP_PREFERENCES_CITY, "Москва");

        //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Копорье&appid=f38e5ba35d6cb5b542711ce044c35e01&units=metric&lang=ru");
        new GetWeather(this).execute(urlBuilder(city));


        final EditText townSearch = (EditText) findViewById(R.id.town_search);
        townSearch.setOnKeyListener((v, kayCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && kayCode == KeyEvent.KEYCODE_ENTER)
            {
                city = townSearch.getText().toString().trim();
                new GetWeather(this).execute(urlBuilder(city));
                return true;
            }
            return false;
        });
    }


    private URL urlBuilder (String city){
        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY = "q";
        String PARAM_APPID = "appid";
        String appID = "f38e5ba35d6cb5b542711ce044c35e01";
        String PARAM_UNITS = "units";
        String units = "metric";
        String PARAM_LANG = "lang";
        String lang = getString(R.string.lang);

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_CITY, city)
                .appendQueryParameter(PARAM_APPID, appID)
                .appendQueryParameter(PARAM_UNITS, units)
                .appendQueryParameter(PARAM_LANG, lang)
                .build();

        URL url = null;

        try {
            url = new URL (builtUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    @Override
    public void processFinished(String output){
        Log.d(TAG, "processFinished: " + output);
        if (output == null) return;
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
            case "404":
                Toast.makeText(this, R.string.error404, Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (settings.contains(APP_PREFERENCES_CITY)) {
            city = settings.getString(APP_PREFERENCES_CITY, "Москва");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_CITY, city).apply();
    }
}