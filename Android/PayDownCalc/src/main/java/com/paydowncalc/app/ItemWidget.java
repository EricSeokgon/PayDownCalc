package com.paydowncalc.app;

import java.text.NumberFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class ItemWidget {

    public String option;
    public String value;
    public int thumb;
    public String action;
    public int inputType;
    public Object tag;
    public String[] spinnerChoices;
    public IconAdapter listItems;

    //extrapayment stuff
    public String frequency;
    public String startmonth;
    public int startyear;
    public boolean isExtra;
    public boolean moneyFormat;

    ItemWidget(String thefrequency, String thestartmonth, String thevalue, int thestartyear, boolean isextra)
    {
        isExtra = true;
        moneyFormat = true;
        frequency = thefrequency;
        startmonth = thestartmonth;
        startyear = thestartyear;
        if(frequency.toLowerCase().contains("annual"))
        {
            thumb = R.drawable.calendar_multi_blank_black;
        }
        if(frequency.toLowerCase().contains("one"))
        {
            thumb = R.drawable.dollar_black;
        }
        if(frequency.toLowerCase().contains("month"))
        {
            thumb = R.drawable.calendar_blank_black;
        }

        option = "Extra";
        value = thevalue;
        action = "extra";

    }

    ItemWidget (String theoption, String thevalue, int thethumb)
    {
        option = theoption;
        value = thevalue;
        thumb = thethumb;
        action = "update";
        inputType = InputType.TYPE_CLASS_TEXT;
        listItems = null;
        isExtra = false;
        moneyFormat = false;

    }

    ItemWidget (String theoption, String thevalue, int thethumb, int theinputType)
    {
        option = theoption;
        value = thevalue;
        thumb = thethumb;
        action = "update";
        inputType = theinputType;
        listItems = null;
        isExtra = false;
        moneyFormat = false;

    }

    ItemWidget (String theoption, String thevalue, int thethumb, String[] thechoices)
    {
        option = theoption;
        value = thevalue;
        thumb = thethumb;
        action = "spinner";
        spinnerChoices = thechoices;
        listItems = null;
        isExtra = false;
        moneyFormat = false;

    }

    public void notifyDataSetChanged()
    {
        if(listItems != null)
        {
            listItems.notifyDataSetChanged();
            Log.v("itemWidget", "notifyDataSetChanged");
        }
    }

    public void extraPromptBuilder(final Context ctx, IconAdapter iconAdapterItems)
    {
        listItems = iconAdapterItems;

        String[] months = PayDownCalcMain.getMonths();
        String [] years = PayDownCalcMain.getYears();
        String[] extraTypes = PayDownCalcMain.getExtraTypes();

        final Spinner monthSpinner, yearSpinner, typeSpinner;
        ArrayAdapter<String> monthSpinnerAdapter, yearSpinnerAdapter, typeSpinnerAdapter;
        final EditText input;



        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

        alert.setTitle("Extra Payment");


        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate(R.layout.extrapaymentprompt, null);
        monthSpinner = (Spinner)view.findViewById(R.id.monthspinner);
        yearSpinner = (Spinner)view.findViewById(R.id.yearspinner);
        typeSpinner = (Spinner)view.findViewById(R.id.extratype);
        monthSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, months);
        yearSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, years);
        typeSpinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, extraTypes);

        monthSpinner.setAdapter(monthSpinnerAdapter);
        yearSpinner.setAdapter(yearSpinnerAdapter);
        typeSpinner.setAdapter(typeSpinnerAdapter);

        monthSpinner.setSelection(monthSpinnerAdapter.getPosition(this.startmonth));
        yearSpinner.setSelection(yearSpinnerAdapter.getPosition(Integer.toString(this.startyear)));
        typeSpinner.setSelection(typeSpinnerAdapter.getPosition(this.frequency));

        input= (EditText)view.findViewById(R.id.value);
        input.setText(this.value);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setSelection(input.getText().length());


        alert.setView(view);



        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable getvalue = input.getText();
                Toast.makeText(ctx, "Set to " + getvalue, Toast.LENGTH_SHORT).show();
                setValue(getvalue.toString());
                notifyDataSetChanged();


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });



        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable getvalue = input.getText();
                Toast.makeText(ctx, "Set to " + getvalue, Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();


            }
        });

        alert.show();
    }

    public void setmoneyFormat(boolean isMoney)
    {
        this.moneyFormat =isMoney;
    }

    public String getFormatted()
    {
        if(this.moneyFormat)
        {
            //Log.v("itemWidget", "moneyFormat :" + this.moneyFormat + ", value:" + value);

            String currencyString = NumberFormat.getCurrencyInstance().format(Double.parseDouble(this.value));
            //Handle the weird exception of formatting whole dollar amounts with no decimal
            currencyString = currencyString.replaceAll("\\.00", "");
            return currencyString;
			
			/*
			String currencyString = NumberFormat.getCurrencyInstance().format(this.value);
			//Handle the weird exception of formatting whole dollar amounts with no decimal
			currencyString = currencyString.replaceAll("\\.00", "");
			return currencyString;
			*/

        } else {
            return value;
        }

    }

    public void setExtra(boolean extra)
    {
        this.isExtra = extra;
    }

    public void promptBuilder(final Context ctx, IconAdapter iconAdapterItems)
    {
        listItems = iconAdapterItems;

        if(this.action.equals("update"))
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

            alert.setTitle(this.option);

            // Set an EditText view to get user input
            final EditText input = new EditText(ctx);
            input.setText(this.value);
            input.setInputType(this.inputType);
            input.setSelection(input.getText().length());

            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Editable getvalue = input.getText();
                    //	Toast.makeText(ctx, "Set to " + getvalue, Toast.LENGTH_SHORT).show();
                    setValue(getvalue.toString());
                    notifyDataSetChanged();


                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });



            alert.show();
        }


        if(this.action.equals("spinner"))
        {


            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

            alert.setTitle(this.option);

            // Set an EditText view to get user input
            final Spinner input = new Spinner(ctx);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, this.spinnerChoices);
            input.setAdapter(spinnerArrayAdapter);

//      		Toast.makeText(PayDownCalcActivity.this, "Looking for " + this.value, Toast.LENGTH_SHORT).show();
            for(int i =0; i<this.spinnerChoices.length; i++)
            {

                String testValue = this.spinnerChoices[i];

//				Log.v("pdc", "test :" + testValue + " == " + this.value);

                if(testValue.trim().equals(this.value.trim()))
                {
//	          		Toast.makeText(PayDownCalcActivity.this, "Found " + testValue + " at " + i, Toast.LENGTH_SHORT).show();
                    input.setSelection(i);
                }

            }

            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String getvalue = (String) input.getSelectedItem();
                    Toast.makeText(ctx, "Set to " + getvalue, Toast.LENGTH_SHORT).show();
                    setValue(getvalue.toString());
                    notifyDataSetChanged();

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        }





    }



    public void setValue (String newvalue)
    {
        value = newvalue;
    }

    public void setTag (Object newtag)
    {
        tag = newtag;
    }


}
