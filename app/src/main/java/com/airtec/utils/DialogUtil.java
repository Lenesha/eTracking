package com.airtec.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RibboN on 7/26/15.
 */
public class DialogUtil {

    public static void showAlertDialog(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static DatePickerDialog showDatePickerDialogMinToday(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(context, onDateSetListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        fromDatePickerDialog.show();
        return fromDatePickerDialog;
    }

    public static DatePickerDialog showDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(context, onDateSetListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        //fromDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        fromDatePickerDialog.show();
        return fromDatePickerDialog;
    }

    public static DatePickerDialog showDatePickerDialogDOB(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Locale locale = context.getResources().getConfiguration().locale;
        Locale.setDefault(locale);

        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(context, onDateSetListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        fromDatePickerDialog.show();
        return fromDatePickerDialog;
    }

//    public static DatePickerDialog showDatePickerDialog(BaseActvity context, DatePickerDialog.OnDateSetListener onDateSetListener,DialogInterface.OnCancelListener onDateCancelListener) {
//        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//
//        DatePickerDialog timeDatePickerDialog = DatePickerDialog.newInstance(onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        timeDatePickerDialog.setOnCancelListener(onDateCancelListener);
//        timeDatePickerDialog.show(context.getFragmentManager(), "Date Picker");
//
//        return timeDatePickerDialog;
//
//    }
//
//    public static DatePickerDialog showDatePickerDialogDOB(BaseActvity context, DatePickerDialog.OnDateSetListener onDateSetListener,DialogInterface.OnCancelListener onDateCancelListener) {
//        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//
//        DatePickerDialog timeDatePickerDialog = DatePickerDialog.newInstance(onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        timeDatePickerDialog.setOnCancelListener(onDateCancelListener);
//        timeDatePickerDialog.setMaxDate(calendar);
//        timeDatePickerDialog.show(context.getFragmentManager(), "Date Picker");
//
//        return timeDatePickerDialog;
//
//    }


    public static String getMonth(String date, String initialFormat , String outputformat) throws ParseException {
        Date d = new SimpleDateFormat(initialFormat, Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat(outputformat, Locale.ENGLISH).format(cal.getTime());
        return monthName;
    }


    public static boolean checkDatesBefore(String startDate, String endDate) {
        boolean b = false;
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-dd-MM");
        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = true;// If start date is before end date
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = true;// If two dates are equal
            } else {
                b = false; // If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }




    public static  boolean checkDatesAfter(String mfrom, String to) {
        boolean b = false;
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-dd-MM");
        try {
            if (dfDate.parse(mfrom).compareTo((dfDate.parse(to))) < 0)

            {
                b = true;// If start date is before end date
            } else if (dfDate.parse(mfrom).equals(dfDate.parse(to))) {
                b = true;// If two dates are equal
            } else {
                b = false; // If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

}