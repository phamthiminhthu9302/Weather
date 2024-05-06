package in.sunilpaulmathew.weatherwidget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.model.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;


public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private final List<ForecastItems> mData;


    private static ClickListener mClickListener;

    public HourlyForecastAdapter(List<ForecastItems> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public HourlyForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_forecast_hourly, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastAdapter.ViewHolder holder, int position) {
        holder.mStatusIcon.setImageDrawable(this.mData.get(position).getWeatherIcon(this.mData.get(position).getDayOrNight(), holder.mStatusIcon.getContext()));
        holder.mDate.setText(this.mData.get(position).getTime());
        holder.mTemperature.setText(this.mData.get(position).getHourlyTemp());
        holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(false, holder.mTemperature.getContext()));
        holder.mTemperatureUnit.setText(Weather.getTemperatureUnit(holder.mTemperatureUnit.getContext()));
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mStatusIcon;
        private final MaterialTextView mDate, mTemperature, mTemperatureUnit;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mStatusIcon = view.findViewById(R.id.weather_button);
            this.mDate = view.findViewById(R.id.date);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mTemperatureUnit = view.findViewById(R.id.temperature_unit);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        HourlyForecastAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}