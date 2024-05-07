package in.sunilpaulmathew.weatherwidget.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.fragments.InitializeFragment;


public class InitializeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new InitializeFragment()).commit();
    }

}