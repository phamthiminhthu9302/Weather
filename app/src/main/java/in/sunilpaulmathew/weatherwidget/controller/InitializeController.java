package in.sunilpaulmathew.weatherwidget.controller;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.LocationsAdapter;
import in.sunilpaulmathew.weatherwidget.fragments.InitializeFragment;
import in.sunilpaulmathew.weatherwidget.model.LocationItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class InitializeController {
    private InitializeFragment fragment;
    private AppCompatAutoCompleteTextView mLatitude;
    private AppCompatAutoCompleteTextView mLocation;
    private AppCompatAutoCompleteTextView mLongitude;
    private RecyclerView mRecyclerView;
    private MaterialCardView mApplyCard;
    private View rootView;
    public InitializeController(InitializeFragment fragment, AppCompatAutoCompleteTextView mLatitude,
                                AppCompatAutoCompleteTextView mLocation, AppCompatAutoCompleteTextView mLongitude,
                                RecyclerView mRecyclerView,MaterialCardView mApplyCard,View mRootView) {
        this.fragment = fragment;
        this.mLatitude = mLatitude;
        this.mLocation = mLocation;
        this.mLongitude = mLongitude;
        this.mApplyCard = mApplyCard;
        this.mRecyclerView = mRecyclerView;
        this.rootView = mRootView;
        setupListeners();

    }
    public void setupListeners()  {

            mLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !s.toString().trim().isEmpty()) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://geocoding-api.open-meteo.com/v1/search?name=" + s.toString().trim() + "&count=10&language=en&format=json")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                new Handler(Looper.getMainLooper()).post(() -> Utils.toast(fragment.getString(R.string.location_data_failed), fragment.requireActivity()).show());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    throw new IOException("Unexpected code " + response);
                                }

                                try (ResponseBody responseBody = response.body()) {
                                    if (responseBody != null) {
                                        String jsonData = responseBody.string();
                                        JSONObject mMainObject = new JSONObject(jsonData);
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            try {
                                                JSONArray mResults = mMainObject.getJSONArray("results");
                                                List<LocationItems> mData = new ArrayList<>();
                                                for (int i = 0; i < mResults.length(); i++) {
                                                    mData.add(new LocationItems(
                                                            mResults.getJSONObject(i).getString("name"),
                                                            mResults.getJSONObject(i).getString("country"),
                                                            mResults.getJSONObject(i).getString("latitude"),
                                                            mResults.getJSONObject(i).getString("longitude"))
                                                    );
                                                }
                                                LocationsAdapter mLocationsAdapter = new LocationsAdapter(mData);
                                                mRecyclerView.setVisibility(View.VISIBLE);
                                                mRecyclerView.setAdapter(mLocationsAdapter);
                                                mLocationsAdapter.setOnItemClickListener((position, v) -> apply(mData.get(position).getCity(),
                                                        mData.get(position).getLatitude(), mData.get(position).getLongitude()));

                                            } catch (JSONException ignored) {
                                            }
                                        });
                                    }
                                } catch (JSONException | IOException ignored) {
                                    new Handler(Looper.getMainLooper()).post(() -> Utils.toast(fragment.getString(R.string.location_data_failed), fragment.requireActivity()).show());
                                }
                            }
                        });
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            });

            mApplyCard.setOnClickListener(v -> {
                if (mLatitude.getText().toString().trim().isEmpty() || mLongitude.getText().toString().trim().isEmpty()) {
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.open-meteo.com/v1/forecast?latitude=" + mLatitude.getText().toString().trim() + "&longitude=" + mLongitude.getText().toString().trim() )
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Xử lý khi gặp lỗi
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {

                            new Handler(Looper.getMainLooper()).post(() -> Utils.toast(fragment.getString(R.string.weather_status_failed), fragment.getContext()).show());
                        }else{
                            apply(mLocation.getText().toString().trim(), mLatitude.getText().toString().trim(), mLongitude.getText().toString().trim());
                        }

                    }

            });
                    });
            fragment.requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (Weather.getLatitude(fragment.requireActivity()) != null && Weather.getLongitude(fragment.requireActivity()) != null) {
                        Utils.restartApp(fragment.requireActivity());
                    } else {
                        fragment.requireActivity().finish();
                    }
                }
            });


    }
    public void apply(String city, String latitude, String longitude) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(() -> {
            Utils.saveBoolean("reAcquire", true, fragment.requireActivity());
            Utils.saveString("latitude", latitude, fragment.requireActivity());
            Utils.saveString("longitude", longitude, fragment.requireActivity());
            Utils.saveString("location", city, fragment.requireActivity());
            Utils.saveLong("lastUVAlert", Long.MIN_VALUE, fragment.requireActivity());
            Utils.saveLong("lastWeatherAlert", Long.MIN_VALUE, fragment.requireActivity());
            new Handler(Looper.getMainLooper()).post(() -> Utils.restartApp(fragment.requireActivity()));
            if (!executors.isShutdown()) executors.shutdown();
        });
    }
}
