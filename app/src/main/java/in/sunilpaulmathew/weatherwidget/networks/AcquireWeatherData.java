package in.sunilpaulmathew.weatherwidget.networks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.model.ForecastItems;
import in.sunilpaulmathew.weatherwidget.model.WeatherAlerts;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.model.WeatherItems;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class AcquireWeatherData {


    private final Context mContext;
    private final String mLatitude, mLongitude;
    public AcquireWeatherData(String latitude, String longitude, Context context) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mContext = context;
    }

    @SuppressLint("StringFormatMatches")
    public void acquire() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.open-meteo.com/v1/forecast?latitude=" + mLatitude + "&longitude=" + mLongitude +
                        "&current_weather=true&daily=weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min," +
                        "uv_index_max,sunrise,sunset&hourly=temperature_2m,weathercode,precipitation_probability,visibility,relativehumidity_2m," +
                        "pressure_msl,apparent_temperature,is_day&timezone=auto" + Utils.getString("temperatureUnit", "", mContext) +
                        Utils.getString("forecastDays", "", mContext))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Xử lý khi gặp lỗi
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        String jsonData = responseBody.string();
                        saveDataToFile(jsonData);
                        if (Utils.getBoolean("reAcquire", false, mContext)) {
                            Utils.saveBoolean("reAcquire", false, mContext);
                        }else{
                            jsonData = Utils.read(Weather.getDataFile(mContext));
                        }
                        JSONObject jsonObject = new JSONObject(jsonData);
                        processData(jsonObject); // Gọi phương thức để xử lý dữ liệu JSON
                    }

                } catch (JSONException e) {
                    e.printStackTrace();


                }

            }
        });
    }
    // Phương thức để lưu dữ liệu vào tệp getDataFile()
    private void saveDataToFile(String jsonData) {
        try {
            FileWriter writer = new FileWriter(Weather.getDataFile(mContext));
            writer.write(jsonData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("StringFormatMatches")
    private void processData(JSONObject jsonObject) {
        try {
            List<WeatherItems> mWeatherItems = new ArrayList<>();

            JSONObject mCurrentWeather = jsonObject.getJSONObject("current_weather");

            JSONObject mHourly = jsonObject.getJSONObject("hourly");
            JSONArray mHourlyTime = mHourly.getJSONArray("time");
            JSONArray mHourlyWeatherCode = mHourly.getJSONArray("weathercode");
            JSONArray mHourlyTemp = mHourly.getJSONArray("temperature_2m");
            JSONArray mHourlyPre = mHourly.getJSONArray("precipitation_probability");
            JSONArray mAirPressure = mHourly.getJSONArray("pressure_msl");
            JSONArray mVisibility = mHourly.getJSONArray("visibility");
            JSONArray mHumidity = mHourly.getJSONArray("relativehumidity_2m");
            JSONArray mTempApparent = mHourly.getJSONArray("apparent_temperature");
            JSONArray mDayOrNight = mHourly.getJSONArray("is_day");

            // Xử lý dữ liệu cho mục hourlyForecastItems
            List<ForecastItems> mHourlyForecastItems = new ArrayList<>();
            int hour;
            String timeHour = mCurrentWeather.getString("time").split("T")[1].split(":")[0];
            if (timeHour.equals("00")) {
                hour = 0;
            } else if (timeHour.startsWith("0")) {
                hour = Integer.parseInt(timeHour.replace("0",""));
            } else {
                hour = Integer.parseInt(timeHour);
            }
            for (int i = hour; i < hour + 24; i++) {
                mHourlyForecastItems.add(
                        new ForecastItems(
                                mHourlyTime.getString(i),
                                Utils.valueOfInt(mHourlyTemp.getString(i)),
                                null,
                                "(" + mContext.getString(R.string.temperature_feels_like, Utils.valueOfInt(mTempApparent.getString(i))) +
                                        Weather.getTemperatureUnit(mContext) + ")",
                                mHourlyWeatherCode.getInt(i),
                                null,
                                mDayOrNight.getInt(i),
                                null,
                                null,
                                mContext.getString(R.string.humidity, mHumidity.getInt(i)),
                                mHourlyPre.getString(i),
                                mContext.getString(R.string.air_pressure, mAirPressure.getString(i)),
                                mContext.getString(R.string.visibility, mVisibility.getString(i))
                        )
                );
            }

            // Xử lý dữ liệu cho mục dailyForecastItems
            JSONObject mDaily = jsonObject.getJSONObject("daily");
            JSONArray mDailyTime = mDaily.getJSONArray("time");
            JSONArray mDailyWeatherCode = mDaily.getJSONArray("weathercode");
            JSONArray mDailyTempMax = mDaily.getJSONArray("temperature_2m_max");
            JSONArray mDailyTempMin = mDaily.getJSONArray("temperature_2m_min");
            JSONArray mUVIndex = mDaily.getJSONArray("uv_index_max");
            JSONArray mSunrise = mDaily.getJSONArray("sunrise");
            JSONArray mSunset = mDaily.getJSONArray("sunset");

            // Send weather alerts if enabled
            if (Utils.getBoolean("weatherAlerts", false, mContext)) {
                if (mUVIndex.getInt(0) >= 3  && Weather.getFormattedHour(mSunrise.getString(0)) <= hour && Weather.getFormattedHour(
                        mSunrise.getString(0)) + 6 >= hour && Utils.getLong("lastUVAlert", Long.MIN_VALUE, mContext) +
                                  1 * 60 * 60 * 1000 <= System.currentTimeMillis()) {

                    new WeatherAlerts(true, mUVIndex.getInt(0), Integer.MIN_VALUE, mContext).alert();
                }

             if (Utils.getLong("lastWeatherAlert", Long.MIN_VALUE, mContext) + 1 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                    int alertCode = mHourlyWeatherCode.getInt(hour + 2);
                    Integer[] weatherCodes = new Integer[]{
                            45, 48, 55, 57, 65, 67, 75, 82, 86, 95, 96, 99

                    };
                    for (Integer weatherCode : weatherCodes) {
                        if (alertCode == weatherCode) {
                            new WeatherAlerts(false, alertCode, mDayOrNight.getInt(hour + 2), mContext).alert();
                        }
                    }
                 }
            }

            List<ForecastItems> mDailyForecastItems = new ArrayList<>();
            for (int i = 0; i < mDailyTime.length(); i++) {
                mDailyForecastItems.add(
                        new ForecastItems(
                                mDailyTime.getString(i),
                                Utils.valueOfInt(mDailyTempMax.getString(i)),
                                Utils.valueOfInt(mDailyTempMin.getString(i)),
                                null,
                                mDailyWeatherCode.getInt(i),
                                mUVIndex.getString(i),
                                mDayOrNight.getInt(i),
                                mSunrise.getString(i),
                                mSunset.getString(i),
                                null,
                                null,
                                null,
                                null
                        )
                );
            }

            // Xử lý dữ liệu cho mục mCurrentWeatherItem
            mWeatherItems.add(
                    new WeatherItems(
                            Weather.getWeatherIcon(mCurrentWeather.getInt("is_day"), mCurrentWeather.getInt("weathercode"), mContext),
                            mHourlyForecastItems,
                            mDailyForecastItems,
                            Weather.getLocation(mContext),
                            mHourlyPre.getString(hour),
                            Utils.valueOfInt(mCurrentWeather.getString("temperature")),
                            "(" + mContext.getString(R.string.temperature_feels_like, Utils.valueOfInt(mTempApparent.getString(hour))) +
                                    Weather.getTemperatureUnit(mContext) + ")",
                            "(" + jsonObject.getString("timezone") + ")",
                            Weather.getWeatherMode(mCurrentWeather.getInt("weathercode"), mContext),
                            mSunrise.getString(0),
                            mSunset.getString(0),
                            mContext.getString(R.string.wind_speed, mCurrentWeather.getInt("windspeed")),
                            mContext.getString(R.string.air_pressure, mAirPressure.getString(hour)),
                            mContext.getString(R.string.visibility, mVisibility.getString(hour)),
                            mContext.getString(R.string.humidity, mHumidity.getInt(hour)),
                            mCurrentWeather.getInt("is_day"),
                            true
                    )
            );

            // Gọi phương thức successLister để truyền danh sách mWeatherItems cho phương thức thành công.
            new Handler(Looper.getMainLooper()).post(() -> successLister(mWeatherItems));
        } catch (JSONException e) {
            // Xử lý khi gặp lỗi JSONException
            e.printStackTrace();


        }
    }

    public void successLister(List<WeatherItems> weatherItems) {

    }

}
