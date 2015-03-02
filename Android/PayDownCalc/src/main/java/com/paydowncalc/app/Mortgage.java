package com.paydowncalc.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class Mortgage {


    public BigDecimal originalloanamount;
    public Date originalloanstartdate;

    public BigDecimal amountstillowed;
    public Calendar runningdate;
    public String originalloanstartdateformated;
    public String startmonth;
    public String startyear;
    public BigDecimal monthly;
    public BigDecimal runningtotalprincipal;
    public BigDecimal runningtotalinterest;
    public BigDecimal runningtotalpaid;
    public BigDecimal interestsaved;
    public BigDecimal totalinterest;
    public float yearssaved;
    public int loanlengthyears;
    public BigDecimal totalpaid;
    public float interestrate;
    public ArrayList<ExtraPayment> extraPayments;


    public String getDateFormated(Date thedate)
    {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("MMM yyyy");

        return df.format(thedate.getTime());

    }

    public String getFinalPayoffDate()
    {
        return getDateFormated(runningdate.getTime());
    }

    public String getTimeSaved()
    {
        return String.format("%.2g%n", yearssaved);

    }

    public String bigDecToMoney(BigDecimal money)
    {
        String currencyString = NumberFormat.getCurrencyInstance().format(money.doubleValue());
        //Handle the weird exception of formatting whole dollar amounts with no decimal
        currencyString = currencyString.replaceAll("\\.00", "");
        return currencyString;

    }

    public String getTotalInterestSaved()
    {
        return bigDecToMoney(interestsaved);
    }


    public String getTotalInterestPaid()
    {
        return bigDecToMoney(runningtotalinterest);
    }

    public String getMonthlyPayment()
    {
        return bigDecToMoney(monthly);
    }


    public String getTotalPaid()
    {
        return bigDecToMoney(totalpaid);
    }



    public Mortgage()
    {
        originalloanamount = new BigDecimal("100000");
        amountstillowed = originalloanamount;
        interestrate = (float) 4.25;
        loanlengthyears = 30;
        runningdate = Calendar.getInstance();
        runningtotalprincipal = new BigDecimal((int)0);
        runningtotalinterest = new BigDecimal((int)0);
        runningtotalpaid =  new BigDecimal((int)0);
        extraPayments = new ArrayList<ExtraPayment>();


    }

    public Mortgage(BigDecimal originalamount, float interest, int lengthyears )
    {
        originalloanamount = originalamount;
        interestrate = interest;
        amountstillowed = originalloanamount;

        loanlengthyears = lengthyears;
        runningdate = Calendar.getInstance();
        runningtotalprincipal = new BigDecimal((int)0);
        runningtotalinterest = new BigDecimal((int)0);
        runningtotalpaid =  new BigDecimal((int)0);


    }
    public Mortgage readFromFile(Context ctx)
    {
        FileInputStream fis;
        ObjectInputStream is;
        Mortgage mtg = new Mortgage();
        try {
            fis = ctx.openFileInput("mtg");
            is = new ObjectInputStream(fis);
            mtg = (Mortgage) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mtg;


    }

    public void saveToFile(Context ctx)
    {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("mtg", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static public double roundTwoDecimals(double d) {
        return ((double)Math.round(d*100)/100.0d);

    }

    public void amortize()
    {
        //runningdate.setTime(originalloanstartdate);
        runningdate.setTime(new Date());
        //TODO use actual loan start if date is in future!
        runningdate.add(Calendar.MONTH, -1);


        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        nowDate.add(Calendar.MONTH, -2);
        if(nowDate.getTime().before(originalloanstartdate))
        {
            runningdate.setTime(originalloanstartdate);
        }

        int amortizeCount = 0;
        BigDecimal interestratemonth = new BigDecimal(interestrate);
        interestratemonth = interestratemonth.divide(new BigDecimal(12), 10, BigDecimal.ROUND_FLOOR);
        interestratemonth = interestratemonth.divide(new BigDecimal(100), 10, BigDecimal.ROUND_FLOOR);
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("MMM yyyy");

        //local declarations...
        BigDecimal appliedinterest;
        BigDecimal appliedprincipal;
        BigDecimal amount;
        Calendar originalCalendar = runningdate;
        BigDecimal originalAmountOwed = amountstillowed;


        while(amountstillowed.compareTo(BigDecimal.ZERO) > 0  && amortizeCount <= (loanlengthyears*12))
        {
            amortizeCount++;
            amount = monthly;
            appliedinterest = amountstillowed.multiply(interestratemonth);
            appliedprincipal = amount.subtract(appliedinterest);


            if(amountstillowed.subtract(appliedprincipal).compareTo(BigDecimal.ZERO) < 0)
            {
                appliedprincipal = amountstillowed;
            }


            //extra payments
            for(int i = 0; i < extraPayments.size(); i++)
            {
                ExtraPayment testExtra = extraPayments.get(i);

                Log.v("extraPayments", "Checking extra "+ testExtra.type +":" + df.format(testExtra.start) + "/" + testExtra.amount.toString());

                if(runningdate.getTime().after(testExtra.getTime()))
                {
                    Log.v("extraPayments", "Apply extra payment.");

                    appliedprincipal = appliedprincipal.add(testExtra.amount);
                    testExtra.nextDate();
                    extraPayments.set(i, testExtra);
                    /*
                    if(extraPayments.get(i).type.equalsIgnoreCase("One-time"))
                    {
                        extraPayments.get(i).amount = BigDecimal.ZERO;
                    }
                    */
                }

            }



            runningdate.add(Calendar.MONTH, 1);


            amountstillowed = amountstillowed.subtract(appliedprincipal);
            BigDecimal mtgprincipalafter = runningtotalprincipal.add(appliedprincipal);
            runningtotalpaid = runningtotalpaid.add(appliedinterest);
            runningtotalpaid = runningtotalpaid.add(appliedprincipal);
            runningtotalprincipal = mtgprincipalafter;
            runningtotalinterest = runningtotalinterest.add(appliedinterest);


        }

        totalpaid = runningtotalinterest.add(originalloanamount);
        Calendar finalCalendar = runningdate;

        //adjustments
        if(extraPayments.size() <= 0)
        {
            totalinterest  = runningtotalinterest;
            interestsaved = BigDecimal.ZERO;
            yearssaved = 0;

        } else {

            interestsaved= BigDecimal.ZERO;
            yearssaved = 0;

            //do it again, with no extra payments.
            Calendar runningDateNoExtra = Calendar.getInstance();
            //runningDateNoExtra.setTime(originalloanstartdate);
            runningDateNoExtra.setTime(new Date());
            //TODO use actual loan start if date is in future!
            runningDateNoExtra.add(Calendar.MONTH, -1);

            if(nowDate.getTime().before(originalloanstartdate))
            {
                runningDateNoExtra.setTime(originalloanstartdate);
            }


            amountstillowed = originalAmountOwed;
            BigDecimal totalInterestNoExtra = BigDecimal.ZERO;
            runningtotalprincipal = BigDecimal.ZERO;
            amortizeCount = 0;

            Log.v("no savings", "Begin no savings branch on " + getDateFormated(runningdate.getTime()));

            while(amountstillowed.compareTo(BigDecimal.ZERO) > 0  && amortizeCount <= (loanlengthyears*12))
            {
                amortizeCount++;
                amount = monthly;
                appliedinterest = amountstillowed.multiply(interestratemonth);
                appliedprincipal = amount.subtract(appliedinterest);

                if(amountstillowed.subtract(appliedprincipal).compareTo(BigDecimal.ZERO) < 0)
                {
                    Log.v("applypay", "Branched:" + appliedprincipal.doubleValue() + ":" + runningtotalprincipal.doubleValue() +
                            " / " + amountstillowed.doubleValue() + " / " + appliedprincipal.add(runningtotalprincipal).doubleValue());

                    appliedprincipal = amountstillowed;
                }



                runningDateNoExtra.add(Calendar.MONTH, 1);
                amountstillowed = amountstillowed.subtract(appliedprincipal);
                BigDecimal mtgprincipalafter = runningtotalprincipal.add(appliedprincipal);
                runningtotalprincipal = mtgprincipalafter;
                totalInterestNoExtra = totalInterestNoExtra.add(appliedinterest);


            }


            //interest saved...
            interestsaved = totalInterestNoExtra.subtract(runningtotalinterest);


            Log.v("date with extra",  getDateFormated(runningdate.getTime()));
            Log.v("date without extra", getDateFormated(runningDateNoExtra.getTime()));



            //time saved
            long milliEnd = runningdate.getTimeInMillis();
            long milliStart = runningDateNoExtra.getTimeInMillis();

            long dayDiff = ((milliStart - milliEnd) / 1000) / 86400;
            float yearDiff = ((float)dayDiff) / 365;
            yearssaved = (float)Math.round(yearDiff * 100) / 100;

        }

    }

    public void calculateMtg()
    {




//function calculateMtg($originalloanamount, $interestrate, $loanlengthyears = 30)


	/*calculate monthly payment without extra payments...

	P = principal, the initial amount of the loan
	I = the annual interest rate (from 1 to 100 percent)
	L = length, the length (in years) of the loan, or at least the length over which the loan is amortized.

	The following assumes a typical conventional loan where the interest is compounded monthly. First I will define two more variables to make the calculations easier:

	J = monthly interest in decimal form = I / (12 x 100)
	N = number of months over which loan is amortized = L x 12



	                              J
	         M  =  P  x ------------------------

	                      1  - ( 1 + J ) ^ -N

	*/


        int nummonths = loanlengthyears*12;
        float monthlyinterest = (interestrate / (12 * 100));
        double denominator = 1  - Math.pow( 1 + monthlyinterest, 0-nummonths);
        double monthlypaymentraw = originalloanamount.doubleValue() * (monthlyinterest / denominator);

        double totalinterestraw = (monthlypaymentraw * nummonths) - originalloanamount.doubleValue();
        //double totalinterestmonthlyraw = totalinterestraw / nummonths;
        double totalamountraw = totalinterestraw + originalloanamount.doubleValue();



        totalpaid = new BigDecimal((totalamountraw));
        totalpaid = totalpaid.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        totalinterest = new BigDecimal((totalinterestraw));
        totalinterest = totalinterest.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        monthly = new BigDecimal((monthlypaymentraw));
        monthly = monthly.setScale(2, BigDecimal.ROUND_HALF_EVEN);




        originalloanstartdateformated = startmonth + " " + startyear ;
        Log.v("extra393", "Attempting "+ originalloanstartdateformated);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        try {
            originalloanstartdate = sdf.parse(originalloanstartdateformated);
            Log.v("extra", "Extracted "+ sdf.format(originalloanstartdate));

        }
        catch(Exception ex) {
            Log.v("extra", "Parse error!");
            originalloanstartdate = new Date();
        }


        Log.v("MTG","monthly: " + monthly);
        Log.v("MTG","totalinterest: " + totalinterest);
        Log.v("MTG","totalpaid: " + totalpaid);

    }



//There is not a god damn thing wrong with this statement;

//double totalamountraw = totalinterestraw + originalloanamount.doubleValue();

//originalloanamount = originalloanamount;


/*
originalloanamount = Math.round($originalloanamount, 2);
interestrate =Math.round($interestrate, 4);
loanlengthyears = loanlengthyears;
monthly = Math.round(monthlypaymentraw, 2);
totalinterest = Math.round(totalinterestraw, 2);
totalpaid = Math.round(totalamountraw, 2);
runningtotalprincipal = 0;
runningtotalinterest = 0;
*/


}

