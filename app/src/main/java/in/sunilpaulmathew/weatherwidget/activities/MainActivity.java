package in.sunilpaulmathew.weatherwidget.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.fragments.WeatherFragment;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.Utils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Utils.getString("language", "VietNamese", this).equals("English")){
            Utils.setLocale("en", this);
        }else{
            Utils.setLocale("vi", this);
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Weather.getLatitude(this) == null || Weather.getLongitude(this) == null) {
            Intent initialize = new Intent(this, InitializeActivity.class);
            startActivity(initialize);
            finish();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new WeatherFragment()).commit();
    }

}
