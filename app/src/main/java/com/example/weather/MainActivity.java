package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, GetWeather.AsyncResponses {

    private static final String TAG = "MainActivity";

    private TextView townName;
    private TextView description;
    private TextView temp;
    private TextView feelsTemp;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private ImageView image;

    private String city;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_CITY = "city";
    private SharedPreferences settings;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Копорье&appid=f38e5ba35d6cb5b542711ce044c35e01&units=metric&lang=ru");
        swipeRefreshLayout.setRefreshing(true);
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

    private void initComponents(){
        townName = findViewById(R.id.townNameTextView);
        description = findViewById(R.id.weatherValueTextView);
        temp = findViewById(R.id.tempValueTextView);
        feelsTemp = findViewById(R.id.feelsTempValueTextView);
        wind = findViewById(R.id.windSpeedValueTextView);
        sunrise = findViewById(R.id.sunriseValueTextView);
        sunset = findViewById(R.id.sunsetValueTextView);
        image = findViewById(R.id.imageView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.rgb(138, 43, 226));

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        city = settings.getString(APP_PREFERENCES_CITY, "Москва");
    }

    @Override
    public void onRefresh() {
        new GetWeather(this).execute(urlBuilder(city));
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

        swipeRefreshLayout.setRefreshing(false);
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
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", locale);
            String dateString = format.format(new Date(Long.parseLong(sys.getString("sunrise")) * 1000));
            sunrise.setText(dateString);

            dateString = format.format(new Date(Long.parseLong(sys.getString("sunset")) * 1000));
            sunset.setText(dateString);

            try {
                image.setImageResource(getIcon(weather.getString("icon")));
            }
            catch (NullPointerException e){
                e.printStackTrace();
                image.setImageResource(R.drawable.r01d);
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    private int getIcon(String name) throws NullPointerException{
        Map<String, Integer> icons = new HashMap<>();
        icons.put("01d", R.drawable.r01d);
        icons.put("01n", R.drawable.r01n);
        icons.put("02d", R.drawable.r02d);
        icons.put("02n", R.drawable.r02n);
        icons.put("03d", R.drawable.r03d);
        icons.put("03n", R.drawable.r03n);
        icons.put("04d", R.drawable.r04d);
        icons.put("04n", R.drawable.r04n);
        icons.put("09d", R.drawable.r09d);
        icons.put("09n", R.drawable.r09n);
        icons.put("10d", R.drawable.r10d);
        icons.put("10n", R.drawable.r10n);
        icons.put("11d", R.drawable.r11d);
        icons.put("11n", R.drawable.r11n);
        icons.put("13d", R.drawable.r13d);
        icons.put("13n", R.drawable.r13n);
        icons.put("50d", R.drawable.r50d);
        icons.put("50n", R.drawable.r50n);

        return icons.get(name);
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
                Toast.makeText(this, R.string.default_error, Toast.LENGTH_SHORT).show();
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