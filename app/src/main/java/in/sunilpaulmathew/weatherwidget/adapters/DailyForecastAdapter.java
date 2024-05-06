package in.sunilpaulmathew.weatherwidget.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.model.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;


public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private final List<ForecastItems> mData;

    private static ClickListener mClickListener;

    public DailyForecastAdapter(List<ForecastItems> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public DailyForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_forecast_daily, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastAdapter.ViewHolder holder, int position) {
        holder.mStatusIcon.setImageDrawable(this.mData.get(position).getWeatherIcon(this.mData.get(position).getDayOrNight(), holder.mStatusIcon.getContext()));
        holder.mDay.setText(Weather.getFormattedDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + position));
        holder.mDate.setText(position == 0 ? holder.mDate.getContext().getString(R.string.today) : this.mData.get(position).getDate());
        holder.mDate.setTypeface(null, position == 0 ? Typeface.BOLD_ITALIC : Typeface.BOLD);
        holder.mTemperature.setText(this.mData.get(position).getDailyTemp());
        holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(false, holder.mTemperature.getContext()));
        holder.mTemperatureUnit.setText(Weather.getTemperatureUnit(holder.mTemperatureUnit.getContext()));
        holder.mUVIndex.setText(this.mData.get(position).getUVIndex(holder.mUVIndex.getContext()));
        holder.mWeatherStatus.setText(this.mData.get(position).getWeatherStatus(holder.mStatusIcon.getContext()));
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mStatusIcon;
        private final MaterialTextView mDate, mDay, mUVIndex, mTemperature, mTemperatureUnit, mWeatherStatus;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mStatusIcon = view.findViewById(R.id.weather_button);
            this.mDay = view.findViewById(R.id.day);
            this.mDate = view.findViewById(R.id.date);
            this.mUVIndex = view.findViewById(R.id.uv_index);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mWeatherStatus = view.findViewById(R.id.weather_status);
            this.mTemperatureUnit = view.findViewById(R.id.temperature_unit);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DailyForecastAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}
