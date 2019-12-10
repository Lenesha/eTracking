/*
 * Copyright (c) 2015. Configure.IT, All Rights Reserved.
 */

package com.airtec.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airtec.R;


public class ProgressHUD extends Dialog {


    private static ProgressHUD pd;

    private static TextProgressBar progressBar;
    private static ProgressBar progressBarDefault;
    private static TextView txtProgressValue;
    private static TextView txtProgressMessage;

    private static boolean isSetProgressView;

    private static final String DEFAULT_BG_COLOR = "#ff404040";
    private static final String DEFAULT_TEXT_COLOR = "#ffffffff";

    private ProgressHUD(Context context) {
        super(context);
    }

    private ProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    private static ProgressHUD getProgressHUD(Context context, int theme) {
        if (pd != null && pd.isShowing())
            pd.dismiss();
        pd = new ProgressHUD(context, theme);
        return pd;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        try {
//            ImageView imageView = (ImageView) findViewById(R.id.progressImageView);
//            AnimationDrawable spinner = (AnimationDrawable) imageView
//                    .getBackground();
//            spinner.start();

        } catch (Exception e) {
        }
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.progressMessage1).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.progressMessage1);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static ProgressHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener, String color, String backGroundColor) {

        return show(context, message, null, indeterminate, cancelable, cancelListener, color, backGroundColor);
    }

    public static ProgressHUD show(Context context, CharSequence message1, CharSequence message2, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener, String color, String backGroundColor) {
        isSetProgressView = false;

        ProgressHUD dialog = getProgressHUD(context, R.style.ProgressHUD);
        dialog.setTitle("");

        View vMain = LayoutInflater.from(context).inflate(R.layout.progress_hud, null);

        if(backGroundColor != null) {
            if (Build.VERSION.SDK_INT > 15)
                vMain.setBackground(getShapeDrawable(Color.parseColor(backGroundColor)));
            else
                vMain.setBackgroundDrawable(getShapeDrawable(Color.parseColor(backGroundColor)));
        }

        dialog.setContentView(vMain);
        txtProgressMessage = (TextView) dialog.findViewById(R.id.progressMessage1);
        if (TextUtils.isEmpty(message1)) {
            txtProgressMessage.setVisibility(View.GONE);
        } else {

            if (txtProgressMessage != null) {
                txtProgressMessage.setVisibility(View.VISIBLE);
                txtProgressMessage.setText(message1);
                int textColor = Color.WHITE;
                if(color != null)
                {
                    try {
                        textColor = Color.parseColor(color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                    textColor = Color.parseColor(DEFAULT_TEXT_COLOR);

                txtProgressMessage.setTextColor(textColor == Color.TRANSPARENT ? Color.WHITE : textColor);
            }
        }

        txtProgressValue = (TextView) dialog.findViewById(R.id.progressMessage2);
        if (txtProgressValue != null) {
            if (TextUtils.isEmpty(message2)) {
                txtProgressValue.setVisibility(View.GONE);
            } else {
                txtProgressValue.setVisibility(View.VISIBLE);
                txtProgressValue.setText(message2);
                int textColor = Color.WHITE;
                if(color != null)
                {
                    try {
                        textColor = Color.parseColor(color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                    textColor = Color.parseColor(DEFAULT_TEXT_COLOR);
                txtProgressValue.setTextColor(textColor == Color.TRANSPARENT ? Color.WHITE : textColor);
            }
        }
        progressBar = (TextProgressBar) dialog.findViewById(R.id.progressBar);
        dialog.setCancelable(cancelable);
        if (cancelListener != null)
            dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
        return dialog;
    }


    public static ProgressHUD show(Context context, CharSequence message1, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener) {
        return show(context, message1, null, indeterminate, cancelable, cancelListener, DEFAULT_TEXT_COLOR, DEFAULT_BG_COLOR);
    }

    public static ProgressHUD show(Context context, CharSequence message1, CharSequence message2, boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener) {

        return show(context, message1, message2, indeterminate, cancelable, cancelListener, DEFAULT_TEXT_COLOR, DEFAULT_BG_COLOR);
    }

    public static void dismisss() {
        if (pd != null && pd.isShowing())
            pd.dismiss();

        isSetProgressView = false;
    }

    public static void setProgressView(Context context, String message1, String message2, boolean cancelable,
                                       OnCancelListener cancelListener) {
        isSetProgressView = true;
        ProgressHUD dialog = pd;
        if (dialog == null) {
            dialog = show(context, message1, message2, false, cancelable, cancelListener);
        }
        progressBarDefault = (ProgressBar) dialog.findViewById(R.id.progressBar_defualt);
        progressBarDefault.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(message1)) {
            dialog.findViewById(R.id.progressMessage1).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.progressMessage1);
            txt.setText(message1);
        }

        if (!TextUtils.isEmpty(message2)) {
            txtProgressValue.setVisibility(View.VISIBLE);
            txtProgressValue.setText(message2);
        }

        if (!dialog.isShowing())
            dialog.show();

    }

    public static void setProgressValue(int value) {
        ProgressHUD dialog = pd;
        if (dialog == null)
            return;
        try {
            progressBar.setProgress(value);
            txtProgressValue.setText(value + "%");
            txtProgressValue.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSetProgressView() {
        return isSetProgressView;
    }

    /**
     * @param bgColor -default bg color #ff404040
     * @return
     */
    public static Drawable getShapeDrawable(int bgColor) {
        if (Color.TRANSPARENT == bgColor) {
            bgColor = Color.parseColor(DEFAULT_BG_COLOR);
        }
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        gd.setCornerRadius(8f);
        return gd;
    }

    public static void setMessage(String msg) {
        if (txtProgressMessage != null) {
            txtProgressMessage.setVisibility(View.VISIBLE);
            txtProgressMessage.setText(msg);
            if (progressBarDefault != null)
                progressBarDefault.setVisibility(View.VISIBLE);
            if (txtProgressValue != null)
                txtProgressValue.setVisibility(View.GONE);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
        }
    }
}