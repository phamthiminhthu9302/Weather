package in.sunilpaulmathew.weatherwidget.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;


public class SettingsItems implements Serializable {

    private final String mTitle, mDescription;
    private final Drawable mIcon;

    public SettingsItems(String title, String description, Drawable icon) {
        this.mTitle = title;
        this.mDescription = description;

        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public Drawable getIcon() {
        return mIcon;
    }

}