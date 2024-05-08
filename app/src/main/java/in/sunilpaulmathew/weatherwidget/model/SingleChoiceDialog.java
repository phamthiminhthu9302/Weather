package in.sunilpaulmathew.weatherwidget.model;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SingleChoiceDialog {

    private final int mIcon;
    private final int mPosition;
    private final MaterialAlertDialogBuilder mDialogBuilder;
    private final String mTitle;
    private final String[] mSingleChoiceItems;
    private final boolean mShowRadio;

    public SingleChoiceDialog(int icon, String title, String[] singleChoiceItems,
                              int position, Context context, boolean showRadio) {
        this.mIcon = icon;
        this.mTitle = title;
        this.mSingleChoiceItems = singleChoiceItems;
        this.mPosition = position;
        this.mDialogBuilder = new MaterialAlertDialogBuilder(context);
        this.mShowRadio = showRadio;

        setupDialog();
    }

    private void setupDialog() {
        if (mIcon > Integer.MIN_VALUE) {
            mDialogBuilder.setIcon(mIcon);
        }
        if (mTitle != null) {
            mDialogBuilder.setTitle(mTitle);
        }

        // Kiểm tra tham số mới để quyết định liệu nút radio nên được hiển thị hay không
        if (mShowRadio) {
            mDialogBuilder.setSingleChoiceItems(mSingleChoiceItems, mPosition, (dialog, itemPosition) -> {
                onItemSelected(itemPosition);
                dialog.dismiss();
            });
        } else {
            // Hiển thị dialog mà không có nút radio
            mDialogBuilder.setItems(mSingleChoiceItems, (dialog, which) -> {
                onItemSelected(which);
                dialog.dismiss();
            });
        }
    }

    public void show() {
        mDialogBuilder.show();
    }

    public void onItemSelected(int position) {
        // Để được override bởi lớp con
    }

}
