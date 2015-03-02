package com.paydowncalc.app;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class ExtraPayment {

    public Date start;
    public BigDecimal amount;
    public String type;

    ExtraPayment()
    {
        start= new Date();
        amount= new BigDecimal((int) 0);
        type = "";

    }

    public ExtraPayment(String value, String startmonth, int startyear,
                        String frequency) {

        amount = new BigDecimal(value);
        start = stringToDate(startmonth, Integer.toString(startyear));
        type = frequency;
    }

    public void nextDate()
    {
        String [] extraTypes = PayDownCalcMain.getExtraTypes();
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(start); // sets calendar time/date
       if(type.equalsIgnoreCase(extraTypes[0])) // monthly
        {
            cal.add(Calendar.MONTH, 1);
        }
        if(type.equalsIgnoreCase(extraTypes[1])) //annual
        {
            cal.add(Calendar.YEAR, 1);
        }
        if(type.equalsIgnoreCase(extraTypes[2])) // one-time
        {
            cal.add(Calendar.YEAR, 1000); //disable
        }

        start = cal.getTime();

    }

    public Date getTime()
    {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(start); // sets calendar time/date
        cal.add(Calendar.DAY_OF_MONTH, -5);  // making it before first day
        return cal.getTime();
    }

    public static Date stringToDate(String month, String year)
    {
        return ExtraPayment.stringToDate(month + " " + year);
    }


    public static Date stringToDate(String string) {

        Log.v("extra", "Parsing string "+ string);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        Date date;
        try {
            date = sdf.parse(string);
            Log.v("extra", "Extracted "+ sdf.format(date));

        }
        catch(Exception ex) {
            Log.v("extra", "Parse error!");
            date = new Date();
        }

        return date;
    }


}
