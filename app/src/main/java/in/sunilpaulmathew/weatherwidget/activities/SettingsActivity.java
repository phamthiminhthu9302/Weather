package in.sunilpaulmathew.weatherwidget.activities;

import android.Manifest;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.SettingsAdapter;

import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.controller.SettingController;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Utils.getString("language", "VietNamese", this).equals("English")){
            Utils.setLocale("en", this);
        }else{
            Utils.setLocale("vi", this);
        }
        SettingController mSettingController = new SettingController(this);

            RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            SettingsAdapter mAdapter = new SettingsAdapter(mSettingController.getData());
            mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((position, v) -> {
                mSettingController.handleItemClick(position);


        });

    }

    public final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        Utils.saveBoolean("useGPS", fineLocationGranted != null && fineLocationGranted
                                || coarseLocationGranted != null && coarseLocationGranted, this);
                        recreate();
                    }
            );

    @RequiresApi(api = Build.VERSION_CODES.O)
    public final ActivityResultLauncher<String> notificationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(), isGranted -> {
                        Utils.saveBoolean("weatherAlerts", isGranted, this);
                        recreate();
                    }
            );

}