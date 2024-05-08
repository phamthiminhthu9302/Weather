package in.sunilpaulmathew.weatherwidget.controller;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import in.sunilpaulmathew.weatherwidget.R;
import java.util.ArrayList;
import in.sunilpaulmathew.weatherwidget.BuildConfig;
import in.sunilpaulmathew.weatherwidget.activities.LoginActivity;
import in.sunilpaulmathew.weatherwidget.networks.LocationListener;
import in.sunilpaulmathew.weatherwidget.model.SettingsItems;
import in.sunilpaulmathew.weatherwidget.model.SingleChoiceDialog;
import in.sunilpaulmathew.weatherwidget.activities.SettingsActivity;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingController {
    private final SettingsActivity mActivity;
    private FirebaseAuth mAuth;

    public SettingController(SettingsActivity activity) {
       this.mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
    }
    public void handleItemClick(int position) {
        switch (position) {
            case 0:
               logout();
                break;
            case 1:
                openAppDetails();
                break;
            case 2:
                Location();
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Utils.isNotificationAccessDenied(mActivity)) {
                    mActivity.notificationPermissionRequest.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                    );
                } else {
                    Utils.saveBoolean("weatherAlerts", !Utils.getBoolean("weatherAlerts", false, mActivity), mActivity);
                    mActivity.recreate();
                }
                break;
            case 4:
                changeTemperatureUnit();
                break;
            case 5:
                changeForecastDays();
                break;

            case 6:
                changeLanguage();
                break;
        }
    }

    public ArrayList<SettingsItems> getData() {
        ArrayList<SettingsItems> mData = new ArrayList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            mData.add(new SettingsItems(mActivity.getString(R.string.user) ,  currentUser.getEmail(),
                    Utils.getDrawable(R.drawable.ic_account, mActivity)));
        }
        mData.add(new SettingsItems(mActivity.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME , mActivity.getString(R.string.information),
                Utils.getDrawable(R.drawable.ic_info, mActivity)));
        mData.add(new SettingsItems(mActivity.getString(R.string.location_service), mActivity.getString(R.string.location_service_summary),
                Utils.getDrawable(R.drawable.ic_gps, mActivity)));
        mData.add(new SettingsItems(mActivity.getString(R.string.weather_alert),mActivity. getString(R.string.weather_alert_summary),
                Utils.getDrawable(R.drawable.ic_notifications, mActivity)));
        mData.add(new SettingsItems(mActivity.getString(R.string.temperature_unit), getTemperatureUnit(),
                Utils.getDrawable(R.drawable.ic_thermostat, mActivity)));
        mData.add(new SettingsItems(mActivity.getString(R.string.forecast_days), getForecastDays(),
                Utils.getDrawable(R.drawable.ic_days, mActivity)));
        mData.add(new SettingsItems(mActivity.getString(R.string.translations), mActivity.getString(R.string.translations_summary),
                Utils.getDrawable(R.drawable.ic_translate, mActivity)));
        return mData;
    }


public void Location(){
    if (Utils.isLocationAccessDenied(mActivity)) {
        mActivity.locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    } else {
        Utils.saveBoolean("useGPS", !Utils.getBoolean("useGPS", true, mActivity), mActivity);
        Utils.saveBoolean("gpsAllowed", !Utils.getBoolean("gpsAllowed", false, mActivity), mActivity);
        mActivity.recreate();
        if(Utils.getBoolean("useGPS", true, mActivity)){
            new LocationListener(mActivity) {
                @Override
                public void onLocationInitialized(String latitude, String longitude, String address) {
                    Utils.saveBoolean("reAcquire", true,mActivity);
                    Utils.saveString("latitude", latitude, mActivity);
                    Utils.saveString("longitude", longitude, mActivity);
                    Utils.saveString("location", address, mActivity);
                    Utils.saveLong("lastUVAlert", Long.MIN_VALUE, mActivity);
                    Utils.saveLong("lastWeatherAlert", Long.MIN_VALUE, mActivity);
                    Utils.restartApp(mActivity);
                }
            }.initialize();
        }

    }
}
    private String getTemperatureUnit() {
        if (Utils.getString("temperatureUnit", "", mActivity).equals("&temperature_unit=fahrenheit")) {
            return mActivity.getString(R.string.fahrenheit);
        } else {
            return mActivity.getString(R.string.centigrade);
        }
    }

    private String getForecastDays() {
        String days = Utils.getString("forecastDays", "", mActivity);
        if (days.equals("&forecast_days=14")) {
            return mActivity.getString(R.string.days, "14");
        } else if (days.equals("&forecast_days=3")) {
            return mActivity.getString(R.string.days, "3");
        } else {
            return mActivity.getString(R.string.days, "7");
        }
    }

    private void openAppDetails() {
        Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        settings.setData(uri);
        mActivity.startActivity(settings);
        mActivity.finish();
    }

    private void changeTemperatureUnit() {
        new SingleChoiceDialog(R.drawable.ic_thermostat, mActivity.getString(R.string.temperature_unit),
                new String[]{
                        mActivity.getString(R.string.centigrade),
                        mActivity.getString(R.string.fahrenheit)
                }, temperatureUnitPosition(), mActivity,true) {
            @Override
            public void onItemSelected(int itemPosition) {
                if (itemPosition == temperatureUnitPosition()) return;
                if (Utils.isNetworkUnavailable(mActivity)) {
                    Utils.toast(mActivity.getString(R.string.network_connection_failed), mActivity).show();
                    return;
                }
                if (itemPosition == 1) {
                    Utils.saveString("temperatureUnit", "&temperature_unit=fahrenheit", mActivity);
                } else {
                    Utils.saveString("temperatureUnit", "", mActivity);
                }
                Weather.deleteDataFile(mActivity);
                Utils.restartApp(mActivity);
            }
        }.show();
    }
    private void changeForecastDays() {
        new SingleChoiceDialog(R.drawable.ic_days, mActivity.getString(R.string.forecast_days),
                new String[]{
                        mActivity.getString(R.string.days, "3"),
                        mActivity.getString(R.string.days, "7"),
                        mActivity.getString(R.string.days, "14")
                }, forecastDaysPosition(), mActivity,true) {
            @Override
            public void onItemSelected(int itemPosition) {
                if (itemPosition == forecastDaysPosition()) return;
                if (Utils.isNetworkUnavailable(mActivity)) {
                    Utils.toast(mActivity.getString(R.string.network_connection_failed), mActivity).show();
                    return;
                }
                if (itemPosition == 2) {
                    Utils.saveString("forecastDays", "&forecast_days=14", mActivity);
                } else if (itemPosition == 1) {
                    Utils.saveString("forecastDays", "", mActivity);
                } else {
                    Utils.saveString("forecastDays", "&forecast_days=3", mActivity);
                }
                Weather.deleteDataFile(mActivity);
                Utils.restartApp(mActivity);
            }
        }.show();
    }

    private void logout() {
        new SingleChoiceDialog(R.drawable.ic_account, mActivity.getString(R.string.user),
                new String[]{
                        "           "+mActivity.getString(R.string.logout)

                }, -1, mActivity,false) {
            @Override
            public void onItemSelected(int itemPosition) {


                Intent i = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(i);

            }
        }.show();
    }
    private void changeLanguage() {
        new SingleChoiceDialog(R.drawable.ic_translate, mActivity.getString(R.string.translations),
                new String[]{
                        "VietNamese",
                        "English"
                }, getCurrentLanguage() , mActivity,true) {
            @Override
            public void onItemSelected(int itemPosition) {
                if (itemPosition == getCurrentLanguage()) return;
                if (itemPosition == 1) {

                    Utils.setLocale("en", mActivity);
                    Utils.saveString("language", "English", mActivity);
                } else {

                    Utils.setLocale("vi", mActivity);
                    Utils.saveString("language", "VietNamese", mActivity);
                }
                Weather.deleteDataFile(mActivity);
                Utils.restartApp(mActivity);
            }
        }.show();
    }

    public int getCurrentLanguage() {
        if (Utils.getString("language", "VietNamese", mActivity).equals("English")) {
            return 1;
        } else {
            return 0;
        }
    }
    private int temperatureUnitPosition() {
        if (Utils.getString("temperatureUnit", "", mActivity).equals("&temperature_unit=fahrenheit")) {
            return 1;
        } else {
            return 0;
        }
    }
    private int forecastDaysPosition() {
        String days = Utils.getString("forecastDays", "", mActivity);
        if (days.equals("&forecast_days=14")) {
            return 2;
        } else if (days.equals("&forecast_days=3")) {
            return 0;
        } else {
            return 1;
        }
    }


}
