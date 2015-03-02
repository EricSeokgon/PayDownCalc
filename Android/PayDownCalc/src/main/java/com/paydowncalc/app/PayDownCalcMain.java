package com.paydowncalc.app;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class PayDownCalcMain extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    private static final int SERIES_NR = 3;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    public Mortgage mtg;

    public View mtgFragmentView;
    public View extraPayFragmentView;
    public View chartFragmentView;
    public DummySectionFragment chartFragment;
    public ViewGroup chartFragmentContainer;

    public IconAdapter listItems;

    public static final String PREFS_NAME = "PDC_Prefs";


    public List<ItemWidget> listWidgets;


    public TextWatcher getTextWatcher()
    {
        return new TextWatcher() {
            public void afterTextChanged(Editable s) {
                showCalcButton(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

    }

    public OnItemSelectedListener getSpinnerWatcher()
    {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showCalcButton(true);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        };

    }

    public void showCalcButton(boolean show)
    {


        if(mtgFragmentView == null || extraPayFragmentView == null)
        {
            return;
        }

        int backdate = 0;
        TableLayout resultsTable  = (TableLayout) mtgFragmentView.findViewById(R.id.resultsTable);
        Button calcButton = (Button) mtgFragmentView.findViewById(R.id.calcbutton);
        TextView backdatewarning =(TextView) mtgFragmentView.findViewById(R.id.backdatewarning);
        TextView backdatewarning2 =(TextView) extraPayFragmentView.findViewById(R.id.backdatewarning);
        if(show)
        {
            calcButton.setVisibility(View.VISIBLE);
            resultsTable.setVisibility(View.GONE);

        } else {
            calcButton.setVisibility(View.GONE);
            resultsTable.setVisibility(View.VISIBLE);
            Spinner spin= (Spinner) mtgFragmentView.findViewById(R.id.monthspinner);
            String startmonth = (String)spin.getSelectedItem();
            spin= (Spinner) mtgFragmentView.findViewById(R.id.yearspinner);
            String startyear = (String)spin.getSelectedItem();
            String dateformatted = startmonth + " " + startyear ;
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
            Date startDate = new Date();
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(startDate);
            try {
                startDate = sdf.parse(dateformatted);
                Log.v("startDate", "Extracted "+ sdf.format(startDate));
            }
            catch(Exception ex) {
                Log.v("startDate", "Parse error!");
            }

            currentDate.add(Calendar.MONTH, -2);
            Log.v("currentDate", "compare " + sdf.format(currentDate.getTime()));
            if(currentDate.getTime().after(startDate))
            {
                Log.v("currentDate", sdf.format(currentDate.getTime()) + " is after " + sdf.format((startDate)));
                backdatewarning.setVisibility(View.VISIBLE);
                backdatewarning2.setVisibility(View.VISIBLE);
            } else {
                Log.v("currentDate", sdf.format(currentDate.getTime()) + " is before " + sdf.format((startDate) ));
                backdatewarning.setVisibility(View.GONE);
                backdatewarning2.setVisibility(View.GONE);
            }


        }

        resultsTable  = (TableLayout) extraPayFragmentView.findViewById(R.id.resultsTable);
        if(show)
        {
            resultsTable.setVisibility(View.GONE);

        } else {
            resultsTable.setVisibility(View.VISIBLE);

        }

        if(chartFragmentView != null )
        {

            LayoutInflater inflater = getLayoutInflater();
            View newChartView = inflater.inflate(R.layout.invalidchart, (ViewGroup)chartFragmentView.getParent(), false);

            Button theCalcButton = (Button) newChartView.findViewById(R.id.calcbutton);


            theCalcButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    calcMortgage();
                }
            });


            ViewGroup parent = (ViewGroup) chartFragmentView.getParent();

            int index = parent.indexOfChild(chartFragmentView);
            parent.removeView(chartFragmentView);
            parent.addView(newChartView, index);
            chartFragmentView = newChartView;

            //LayoutInflater inflater = getLayoutInflater();
           //chartFragmentView = inflater.inflate(R.layout.invalidchart, (ViewGroup)chartFragmentView.getParent(), false);
            //chartFragmentView = ChartFactory.getBarChartView(chartFragmentView.getContext(), getBarDemoDataset(), renderer, BarChart.Type.DEFAULT);

        }



    }

    public void calcMortgage()
    {


        mtg = new Mortgage();

        //traverse the list getting all values
        for(int i = 0; i<listItems.listWidgets.size(); i++ )
        {

            if(listItems.listWidgets.get(i).isExtra)
            {
                ExtraPayment ePay = new ExtraPayment(
                        listItems.listWidgets.get(i).value, listItems.listWidgets.get(i).startmonth,
                        listItems.listWidgets.get(i).startyear, listItems.listWidgets.get(i).frequency
                );
                mtg.extraPayments.add(ePay);

            }
        }


        calcMtg();


    }

    public void calcMtg()
    {


        //TODO need to pull and error check the mtg values from the EditText!

        EditText editT = (EditText) mtgFragmentView.findViewById(R.id.origLoanEditText);
            mtg.originalloanamount = new BigDecimal(editT.getText().toString());
        editT = (EditText) mtgFragmentView.findViewById(R.id.currentOwedEditText);
        mtg.amountstillowed = new BigDecimal(editT.getText().toString());
        editT = (EditText) mtgFragmentView.findViewById(R.id.interestEditText);
        mtg.interestrate = Float.parseFloat(editT.getText().toString());
        editT = (EditText) mtgFragmentView.findViewById(R.id.loanLengthEditText);
        mtg.loanlengthyears = Integer.parseInt(editT.getText().toString());
        Spinner spin= (Spinner) mtgFragmentView.findViewById(R.id.monthspinner);
        mtg.startmonth = (String)spin.getSelectedItem();
        spin= (Spinner) mtgFragmentView.findViewById(R.id.yearspinner);
        mtg.startyear = (String)spin.getSelectedItem();

        Log.v("pdc", "here");
        mtg.calculateMtg();
        Log.v("pdc", "there");
        mtg.amortize();
        Log.v("pdc", "tthere");



        if(mtgFragmentView != null)
        {
            TextView monthlyTVmtg = (TextView) mtgFragmentView.findViewById(R.id.monthlyamount);
            TextView totalPaidTVmtg = (TextView) mtgFragmentView.findViewById(R.id.totalpaidamount);
            TextView finalPayTVmtg = (TextView) mtgFragmentView.findViewById(R.id.payoffdate);
            TextView totalIntTVmtg = (TextView) mtgFragmentView.findViewById(R.id.totalinterestamount);
            TextView IntSavedTVmtg = (TextView) mtgFragmentView.findViewById(R.id.interestsavedamount);
            TextView timeSavedTVmtg = (TextView) mtgFragmentView.findViewById(R.id.timesavedamount);




            if(monthlyTVmtg != null)
            {
                Log.v("pdc", "a3tthere");

                monthlyTVmtg.setText(mtg.getMonthlyPayment());
                totalPaidTVmtg.setText(mtg.getTotalPaid());
                finalPayTVmtg.setText(mtg.getFinalPayoffDate());
                totalIntTVmtg.setText(mtg.getTotalInterestPaid());
                IntSavedTVmtg.setText(mtg.getTotalInterestSaved());
                timeSavedTVmtg.setText(mtg.getTimeSaved());
            }

        } else {
            Log.w("pdc", "mtgFragmentView is null");
        }

        if(extraPayFragmentView != null)
        {
            TextView monthlyTVextra = (TextView) extraPayFragmentView.findViewById(R.id.monthlyamount);
            TextView totalPaidTVextra = (TextView) extraPayFragmentView.findViewById(R.id.totalpaidamount);
            TextView finalPayTVextra = (TextView) extraPayFragmentView.findViewById(R.id.payoffdate);
            TextView totalIntTVextra = (TextView) extraPayFragmentView.findViewById(R.id.totalinterestamount);
            TextView IntSavedTVextra = (TextView) extraPayFragmentView.findViewById(R.id.interestsavedamount);
            TextView timeSavedTVextra = (TextView) extraPayFragmentView.findViewById(R.id.timesavedamount);
            if(monthlyTVextra != null)
            {
                Log.v("pdc", "3tthere");
                monthlyTVextra.setText(mtg.getMonthlyPayment());
                totalPaidTVextra.setText(mtg.getTotalPaid());
                finalPayTVextra.setText(mtg.getFinalPayoffDate());
                totalIntTVextra.setText(mtg.getTotalInterestPaid());
                IntSavedTVextra.setText(mtg.getTotalInterestSaved());
                timeSavedTVextra.setText(mtg.getTimeSaved());
            }
        } else {
            Log.w("pdc", "extraPayFragmentView is null");
        }



        showCalcButton(false);

        //Toast.makeText(getBaseContext(), "Chart Updated.", Toast.LENGTH_SHORT).show();

        Log.v("pdc", "reload chart fragment?");



        if(chartFragmentView != null)
        {
            Log.w("pdc", "reloading chart fragment");

            XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
            setChartSettings(renderer);
            View newChartView = ChartFactory.getBarChartView(chartFragmentView.getContext(), getBarDemoDataset(), renderer, BarChart.Type.DEFAULT);

            ViewGroup parent = (ViewGroup) chartFragmentView.getParent();

            int index = parent.indexOfChild(chartFragmentView);
            parent.removeView(chartFragmentView);
            parent.addView(newChartView, index);
            chartFragmentView = newChartView;


           // int tabIndex = getCurrentPageIndex(mViewPager);
           // Log.v("tabIndex", tabIndex + "");
           // Log.v("currentItem", mViewPager.getCurrentItem() + "");
           // mViewPager.setCurrentItem(mViewPager.getCurrentItem());
            //mViewPager.setCurrentItem(tabIndex);
            //final ActionBar actionBar = getActionBar();
            //actionBar.selectTab();
            //chartFragment.

        } else {
            Log.w("pdc", "chart fragment is null");

        }


    }

    public static String[] getMonths()
    {

        return new String[] { "January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};
    }


    public static String[] getYears()
    {
        String [] years = new String[50];
        for(int i=0;i<years.length; i++)
        {
            years[i] = (i + 1970) + "";
            years[i] = years[i].trim();
        }

        return years;



    }

    public static String[] getExtraTypes()
    {

        return new String[] { "Monthly", "Annual", "One-time"};
    }

    public static String[] getLoanLengths()
    {

        String [] years = new String[100];
        for(int i=1;i<years.length; i++)
        {
            years[i] = (i) + " Years";
            years[i] = years[i].trim();
        }

        return years;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paydowncalc_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mtg = new Mortgage();

        listWidgets = new ArrayList<ItemWidget>();
        listItems = new IconAdapter(this, listWidgets);

    }


    public XYMultipleSeriesRenderer getBarDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(20);
        renderer.setMargins(new int[]{20, 30, 15, 0});
        renderer.clearXTextLabels();
        renderer.setXLabelsColor(Color.BLACK);
        //renderer.setYLabelsColor(Color.MAGENTA, 0);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setYLabelsAlign(Paint.Align.LEFT, 0);
        renderer.setShowGridY(true);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setYTitle("Amount");
        renderer.setZoomEnabled(false, false);
        renderer.setXLabels(0);
        renderer.addXTextLabel(1,  "Extra Payments");
        renderer.addXTextLabel(2,  "No Extra Payments");
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);


        SimpleSeriesRenderer red = new SimpleSeriesRenderer();
        SimpleSeriesRenderer orange = new SimpleSeriesRenderer();
        SimpleSeriesRenderer blue = new SimpleSeriesRenderer();
        blue.setColor(Color.BLUE);
        red.setColor(Color.RED);
        orange.setColor(Color.parseColor("#F87217"));
        renderer.addSeriesRenderer(blue);
        renderer.addSeriesRenderer(red);
        renderer.addSeriesRenderer(orange);
        return renderer;
    }

    public void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setChartTitle("Principal / Interest  / Savings");
        //renderer.setXTitle("x values");
        //renderer.setYTitle("y values");

        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(2.5);
        renderer.setYAxisMin(0);

        if(mtg == null)
        {
            renderer.setYAxisMax(0);

        } else {
            double maxvalue = mtg.originalloanamount.doubleValue();

            if(mtg.interestsaved != null)
            {
                maxvalue = mtg.runningtotalinterest.add(mtg.interestsaved).doubleValue();
            }

            if(maxvalue < mtg.originalloanamount.doubleValue())
            {
                maxvalue = mtg.originalloanamount.doubleValue();
            }

            renderer.setYAxisMax(maxvalue);


        }

        renderer.setBarWidth(50);
        renderer.setBarSpacing(10);
    }

    public XYMultipleSeriesDataset getBarDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        CategorySeries seriesPrincipal = new CategorySeries("Principal");
        CategorySeries seriesInterest = new CategorySeries("Interest");
        CategorySeries seriesSavings = new CategorySeries("Savings");


        if(mtg == null) {
            Log.w("getBarDemoDataset", "mtg is null");
        }
        if(mtg.runningtotalinterest == null) {
            Log.w("getBarDemoDataset", "runningtotalinterest is null");
        }
        if(mtg.interestsaved == null) {
            Log.w("getBarDemoDataset", "interestsaved is null");
        }




        if(mtg == null || mtg.runningtotalprincipal == null
                || mtg.runningtotalinterest == null || mtg.interestsaved == null)
        {
            seriesPrincipal.add(0);
            seriesPrincipal.add(0);

            seriesInterest.add(0);
            seriesInterest.add(0);

            seriesSavings.add(0);
            seriesSavings.add(0);

        } else {

            if(listItems.listWidgets.size() == 0)
            {
                seriesPrincipal.add(0);
                seriesInterest.add(0);
                seriesSavings.add(0);

            } else {
                seriesPrincipal.add(mtg.runningtotalprincipal.doubleValue());
                seriesInterest.add(mtg.runningtotalinterest.doubleValue());
                seriesSavings.add(mtg.interestsaved.doubleValue());

            }

            seriesPrincipal.add(mtg.runningtotalprincipal.doubleValue());

            seriesInterest.add(mtg.runningtotalinterest.add(mtg.interestsaved).doubleValue());

            seriesSavings.add(0);


        }

        dataset.addSeries(seriesPrincipal.toXYSeries());
        dataset.addSeries(seriesInterest.toXYSeries());
        dataset.addSeries(seriesSavings.toXYSeries());

        return dataset;

            /*


            for (int i = 0; i < SERIES_NR; i++) {
                CategorySeries series = new CategorySeries("Demo series " + (i + 1));
                for (int k = 0; k < nr; k++) {
                    series.add(100 + r.nextInt() % 100);
                }
                dataset.addSeries(series.toXYSeries());
            }
            return dataset;
            */
    }

    public void message_dialog_yes_no (Activity activity, String msg, DialogInterface.OnClickListener yesListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", yesListener)
                .setNegativeButton("No",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }})
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        DialogInterface.OnClickListener yesListener;
        switch (item.getItemId()) {
            case R.id.action_save:


                if(mtgFragmentView != null)
                {


                    Spinner spin;
                    EditText editT;

                    SharedPreferences.Editor editor = settings.edit();

                    editT = (EditText) mtgFragmentView.findViewById(R.id.origLoanEditText);
                    editor.putString("origLoanEditText", editT.getText().toString());
                    editT = (EditText) mtgFragmentView.findViewById(R.id.currentOwedEditText);
                    editor.putString("currentOwedEditText", editT.getText().toString());
                    editT = (EditText) mtgFragmentView.findViewById(R.id.interestEditText);
                    editor.putString("interestEditText", editT.getText().toString());
                    editT = (EditText) mtgFragmentView.findViewById(R.id.loanLengthEditText);
                    editor.putString("loanLengthEditText", editT.getText().toString());
                    spin= (Spinner) mtgFragmentView.findViewById(R.id.monthspinner);
                    editor.putString("monthspinner", (String)spin.getSelectedItem());
                    spin= (Spinner) mtgFragmentView.findViewById(R.id.yearspinner);
                    editor.putString("yearspinner", (String)spin.getSelectedItem());


                    //traverse the list getting all values
                    int listSize = listItems.listWidgets.size();
                    editor.putInt("listSize", listSize);
                    for(int i = 0; i<listSize; i++ )
                    {
                        editor.putString("listvalue"+i, listItems.listWidgets.get(i).value);
                        editor.putString("liststartmonth"+i, listItems.listWidgets.get(i).startmonth);
                        editor.putInt("liststartyear"+i, listItems.listWidgets.get(i).startyear);
                        editor.putString("listfrequency"+i, listItems.listWidgets.get(i).frequency);
                    }

                    editor.commit();

                    Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT)
                            .show();
                }





                break;
            case R.id.action_load:

                yesListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        Spinner tempSpinner;
                        ArrayAdapter<String> tempAdapter;
                        EditText editT;
                        String[] months = getMonths();
                        String [] years = getYears();
                        String [] extraTypes = getExtraTypes();
                        String [] loanYears = getLoanLengths();
                        Calendar tempCal = Calendar.getInstance();
                        String listvalue, liststartmonth,  listfrequency;
                        int liststartyear;

                        if(extraPayFragmentView != null)
                        {
                            //Spinner tempSpinner = (Spinner)rootView.findViewById(R.id.monthspinner);
                            ListView extraPayList = (ListView)extraPayFragmentView.findViewById(R.id.extraPayList);

                            //traverse the list getting all values
                            int listSize = settings.getInt("listSize", 0);
                            for(int i = 0; i<listSize; i++ )
                            {
                                listvalue = settings.getString("listvalue"+i, "");
                                liststartmonth = settings.getString("liststartmonth"+i, "");
                                liststartyear = settings.getInt("liststartyear"+i, 0);
                                listfrequency = settings.getString("listfrequency"+i, "");
                                addExtraPayment(listfrequency, liststartmonth, liststartyear, listvalue, extraPayList);
                            }



                        }

                        if(mtgFragmentView != null)
                        {
                            tempSpinner = (Spinner)mtgFragmentView.findViewById(R.id.monthspinner);
                            tempAdapter = (ArrayAdapter<String>)tempSpinner.getAdapter();
                            tempSpinner.setSelection(tempAdapter.getPosition(settings.getString("monthspinner", months[tempCal.get(Calendar.MONTH)])));
                            tempSpinner = (Spinner)mtgFragmentView.findViewById(R.id.yearspinner);
                            tempAdapter = (ArrayAdapter<String>)tempSpinner.getAdapter();
                            tempSpinner.setSelection(tempAdapter.getPosition(settings.getString("yearspinner", Integer.toString(tempCal.get(Calendar.YEAR)))));

                            editT =  (EditText)mtgFragmentView.findViewById(R.id.origLoanEditText);
                            editT.setText(settings.getString("origLoanEditText", "100000.00"));
                            editT =  (EditText)mtgFragmentView.findViewById(R.id.currentOwedEditText);
                            editT.setText(settings.getString("currentOwedEditText", "100000.00"));
                            editT =  (EditText)mtgFragmentView.findViewById(R.id.interestEditText);
                            editT.setText(settings.getString("interestEditText", "4.15"));
                            editT =  (EditText)mtgFragmentView.findViewById(R.id.loanLengthEditText);
                            editT.setText(settings.getString("loanLengthEditText", "30"));


                        }

                        Toast.makeText(getBaseContext(), "Loaded.", Toast.LENGTH_SHORT)
                                .show();
                        calcMortgage();
                    }
                };

                message_dialog_yes_no(this, "Abandon changes and load saved?" , yesListener);



                break;
            case R.id.action_about:


                yesListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://DanNagle.com/Amazon"));
                        startActivity(browserIntent);

                    }
                };

                message_dialog_yes_no(this, "PayDownCalc.com is (c) Dan Nagle. \n\nYou can shop Amazon via DanNagle.com/Amazon to support this app for free. Launch Browser?" , yesListener);






                Toast.makeText(this, "Copyright Dan Nagle", Toast.LENGTH_SHORT)
                        .show();
                break;


            default:
                break;
        }

        return true;
    }

    public void addExtraPayment(String frequency, String startmonth, int startyear, String amount, final ListView extraPayList)
    {

        ExtraPayment newExtra = new ExtraPayment(amount, startmonth, startyear, frequency);
        ItemWidget itemWidget = new ItemWidget(frequency, startmonth, amount, startyear, true);
        listWidgets.add(itemWidget);
        listItems = new IconAdapter(getBaseContext(), listWidgets);

        extraPayList.setAdapter(listItems);
        listItems.notifyDataSetChanged();

        extraPayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                TextView theItem = (TextView) view.findViewById(R.id.label);
                final ItemWidget itemWidget = (ItemWidget) theItem.getTag();
                final int finalPosition = position;
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("removing", "size is  " + listWidgets.size());
                                listWidgets.remove(itemWidget);
                                listItems = new IconAdapter(getBaseContext(), listWidgets);
                                extraPayList.setAdapter(listItems);
                                Log.v("removing", "size is  " + listWidgets.size());
                                listItems.notifyDataSetChanged();
                                view.setAlpha(1);
                                calcMortgage();
                            }
                        });
            }

        });

    }

    public void getExtraPayListClickListener ()
    {
//        sdsd
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_down_calc_main, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            Log.v("getItem", "Am I fetching a fragment?");
            if(position == 2)
            {
                //chartFragment = (DummySectionFragment) fragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }



    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";



        private class StableArrayAdapter extends ArrayAdapter<String> {

            HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

            public StableArrayAdapter(Context context, int textViewResourceId,
                                      List<String> objects) {
                super(context, textViewResourceId, objects);
                for (int i = 0; i < objects.size(); ++i) {
                    mIdMap.put(objects.get(i), i);
                }
            }

            @Override
            public long getItemId(int position) {
                String item = getItem(position);
                return mIdMap.get(item);
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        }


        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            String[] months = getMonths();
            String [] years = getYears();
            String [] extraTypes = getExtraTypes();
            String [] loanYears = getLoanLengths();


            Log.v("onCreateView", "Passed Arg is " + Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            if((getArguments().getInt(ARG_SECTION_NUMBER)) == 3)
            {
                Log.v("ARG_SECTION_NUMBER", "3:" );
            }

            Calendar tempCal = Calendar.getInstance();

            Log.v("pdc", "monthtest:" +tempCal.get(Calendar.MONTH));
//            Log.v("pdc", "monthtest2:" + months[tempCal.get(Calendar.MONTH) -1]);
            Log.v("pdc", "monthtest3:" + months[tempCal.get(Calendar.MONTH)]);


            final Spinner monthSpinner, yearSpinner, typeSpinner;
            ArrayAdapter<String> monthSpinnerAdapter, yearSpinnerAdapter, typeSpinnerAdapter;
            final EditText amountEdit;



            View rootView = inflater.inflate(R.layout.fragment_pay_down_calc_main_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));


            monthSpinnerAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_dropdown_item, months);
            yearSpinnerAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_dropdown_item, years);
            typeSpinnerAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_dropdown_item, extraTypes);


            Log.v("onCreateView", "Passed Arg is " + Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            PayDownCalcMain PDCM = (PayDownCalcMain)getActivity();
            SharedPreferences settings = PDCM.getSharedPreferences(PREFS_NAME, 0);

            switch((getArguments().getInt(ARG_SECTION_NUMBER)))
            {
                case 0:
                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.mortgageform, container, false);


                    monthSpinner = (Spinner)rootView.findViewById(R.id.monthspinner);
                    yearSpinner = (Spinner)rootView.findViewById(R.id.yearspinner);
                    //typeSpinner = (Spinner)rootView.findViewById(R.id.extratype);
                    monthSpinner.setAdapter(monthSpinnerAdapter);
                    yearSpinner.setAdapter(yearSpinnerAdapter);
                    monthSpinner.setSelection(monthSpinnerAdapter.getPosition(months[tempCal.get(Calendar.MONTH)]));
                    yearSpinner.setSelection(yearSpinnerAdapter.getPosition(Integer.toString(tempCal.get(Calendar.YEAR))));

                    EditText editT =  (EditText)rootView.findViewById(R.id.origLoanEditText);
                    editT.setText(settings.getString("origLoanEditText", "100000.00"));
                    editT.addTextChangedListener(PDCM.getTextWatcher());
                    editT =  (EditText)rootView.findViewById(R.id.currentOwedEditText);
                    editT.setText(settings.getString("currentOwedEditText", "100000.00"));
                    editT.addTextChangedListener(PDCM.getTextWatcher());
                    editT =  (EditText)rootView.findViewById(R.id.interestEditText);
                    editT.setText(settings.getString("interestEditText", "4.15"));
                    editT.addTextChangedListener(PDCM.getTextWatcher());
                    editT =  (EditText)rootView.findViewById(R.id.loanLengthEditText);
                    editT.setText(settings.getString("loanLengthEditText", "30"));
                    editT.addTextChangedListener(PDCM.getTextWatcher());

                    monthSpinner.setOnItemSelectedListener(PDCM.getSpinnerWatcher());
                    yearSpinner.setOnItemSelectedListener(PDCM.getSpinnerWatcher());

                    monthSpinner.setSelection(monthSpinnerAdapter.getPosition(settings.getString("monthspinner", months[tempCal.get(Calendar.MONTH)])));
                    yearSpinner.setSelection(yearSpinnerAdapter.getPosition(settings.getString("yearspinner", Integer.toString(tempCal.get(Calendar.YEAR)))));


                    Button calcButton = (Button) rootView.findViewById(R.id.calcbutton);


                    calcButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            PayDownCalcMain PDCM = (PayDownCalcMain)getActivity();
                            PDCM.calcMortgage();
                        }
                    });

                    PDCM.mtgFragmentView = rootView;

                    if(PDCM.mtg == null)
                    {
                        PDCM.showCalcButton(true);
                    }
                    //PDCM.calcMortgage();

                    PDCM.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.extrapaymentform, container, false);


                    monthSpinner = (Spinner)rootView.findViewById(R.id.monthspinner);
                    yearSpinner = (Spinner)rootView.findViewById(R.id.yearspinner);
                    typeSpinner = (Spinner)rootView.findViewById(R.id.extratype);
                    amountEdit = (EditText)rootView.findViewById(R.id.extraPayAmountEditText);


                    /* Begin ListView Setup */

                    final ListView extraPayList = (ListView)rootView.findViewById(R.id.extraPayList);

                    extraPayList.setAdapter(PDCM.listItems);

                    /*End ListView setup */

                    monthSpinner.setAdapter(monthSpinnerAdapter);
                    yearSpinner.setAdapter(yearSpinnerAdapter);
                    typeSpinner.setAdapter(typeSpinnerAdapter);

                    monthSpinner.setSelection(monthSpinnerAdapter.getPosition( months[tempCal.get(Calendar.MONTH)]));
                    yearSpinner.setSelection(yearSpinnerAdapter.getPosition(Integer.toString(tempCal.get(Calendar.YEAR))));

                    String listvalue, liststartmonth,  listfrequency;
                    int liststartyear;
                    //traverse the list getting all values
                    int listSize = settings.getInt("listSize", 0);
                    for(int i = 0; i<listSize; i++ )
                    {
                        listvalue = settings.getString("listvalue"+i, "");
                        liststartmonth = settings.getString("liststartmonth"+i, "");
                        liststartyear = settings.getInt("liststartyear"+i, 0);
                        listfrequency = settings.getString("listfrequency"+i, "");
                        PDCM.addExtraPayment(listfrequency, liststartmonth, liststartyear, listvalue, extraPayList);
                    }



                    Button addButton = (Button)rootView.findViewById(R.id.addExtraPayButton);
                    addButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("onClick", "Add extra payment clicked");

                            String frequency = typeSpinner.getSelectedItem().toString();
                            String startmonth = monthSpinner.getSelectedItem().toString();
                            int startyear = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                            String amount = amountEdit.getText().toString();
                            Log.w("extrapay!", amount + " " + frequency + ", " + startmonth + ", " + startyear);
                            final PayDownCalcMain PDCM = (PayDownCalcMain) getActivity();

                            PDCM.addExtraPayment(frequency,startmonth,startyear,amount, extraPayList);


                            PDCM.calcMortgage();
                            //PDCM.showCalcButton(true);


                        }
                    });

                    PDCM.extraPayFragmentView = rootView;

                    //Spinner tempSpinner = (Spinner)rootView.findViewById(R.id.monthspinner);

                    PDCM.showCalcButton(true);

                    //typeSpinner.setSelection(yearSpinnerAdapter.getPosition("Monthly"));

                    break;
                case 3:



                    XYMultipleSeriesRenderer renderer = PDCM.getBarDemoRenderer();
                    PDCM.setChartSettings(renderer);
                    rootView = ChartFactory.getBarChartView(container.getContext(), PDCM.getBarDemoDataset(), renderer, BarChart.Type.DEFAULT);

                    PDCM.chartFragmentContainer = container;
                    PDCM.chartFragmentView = rootView;

                    //Intent intent = ChartFactory.getBarChartIntent(container.getContext(), getBarDemoDataset(), renderer, BarChart.Type.DEFAULT);
                    //startActivity(intent);

                default :
                    break;

            }

            return rootView;
        }
    }

}
