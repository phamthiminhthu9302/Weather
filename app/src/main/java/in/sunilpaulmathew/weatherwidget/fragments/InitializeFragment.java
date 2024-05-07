package in.sunilpaulmathew.weatherwidget.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.networks.LocationListener;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.controller.InitializeController;

public class InitializeFragment extends Fragment {

    private AppCompatAutoCompleteTextView mLatitude, mLocation, mLongitude;

    private InitializeController initializeController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_intialize, container, false);

        mLatitude = mRootView.findViewById(R.id.latitude);
        mLocation = mRootView.findViewById(R.id.location);
        mLongitude = mRootView.findViewById(R.id.longitude);
        MaterialCardView mApplyCard = mRootView.findViewById(R.id.apply_card);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        initializeController = new InitializeController(this, mLatitude, mLocation, mLongitude, mRecyclerView, mApplyCard, mRootView);
        initializeController.setupListeners();

        checkLocationPermission();

        return mRootView;
    }
    private void checkLocationPermission() {
        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.location_access_request), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.request_access, v -> requestLocationPermission());
        if (Utils.isLocationAccessDenied(requireActivity())) {
            snackbar.show();
        }
    }

    private void requestLocationPermission() {
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    public final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted || coarseLocationGranted != null && coarseLocationGranted) {
                            new LocationListener(requireActivity()) {
                                @Override
                                public void onLocationInitialized(String latitude, String longitude, String address) {
                                    initializeController.apply(address, latitude, longitude);
                                }
                            }.initialize();
                        } else {
                            mLocation.requestFocus();
                        }
                    }
            );
}