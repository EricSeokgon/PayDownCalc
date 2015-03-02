package com.paydowncalc.app;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconAdapter extends ArrayAdapter<String> {


    public List<ItemWidget> listWidgets;
    private Context ctx;
    public boolean isExtra = false;



    public IconAdapter(Context context, int resource, int textViewResourceId,
                       String[] objects) {
        super(context, resource, textViewResourceId, objects);
        ctx = context;
        listWidgets  = new ArrayList<ItemWidget>();
        listWidgets.add(new ItemWidget("option", "value", R.drawable.settings_dark));
        // TODO Auto-generated constructor stub
    }

    public void removeAt(int index)
    {

        Log.v("IconAdapter", "old Size " + Integer.toString(listWidgets.size()));
        Log.v("IconAdapter", "Removing " + Integer.toString(index));
        listWidgets.remove(index);
        Log.v("IconAdapter", "new Size " + Integer.toString(listWidgets.size()));

    }


    public void addItem(ItemWidget theitem)
    {
        listWidgets.add(theitem);

    }

    public IconAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        ctx = context;
        listWidgets  = new ArrayList<ItemWidget>();
        listWidgets.add(new ItemWidget("option", "value", R.drawable.settings_dark));
        // TODO Auto-generated constructor stub
    }



    public IconAdapter(Context context, List<ItemWidget> thelistWidgets) {
        super(context, R.layout.iconrow, R.id.label, getObjects(thelistWidgets));
        ctx = context;
        listWidgets  = thelistWidgets;
        // TODO Auto-generated constructor stub
    }



    public IconAdapter(Context context, List<ItemWidget> thelistWidgets, boolean isextra) {
        super(context, R.layout.iconrow, R.id.label, getObjects(thelistWidgets));
        ctx = context;
        listWidgets  = thelistWidgets;
        isExtra = isextra;
        // TODO Auto-generated constructor stub
    }

    private static String[] getObjects(List<ItemWidget> thelistWidgets) {

        String [] returnList = new String[thelistWidgets.size()];

        for(int i = 0 ; i<thelistWidgets.size(); i++)
        {
            returnList[i] = thelistWidgets.get(i).option;
        }

        // TODO Auto-generated method stub
        return returnList;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {

        View row=convertView;



        ImageView icon;
        TextView textBlurb;
        TextView valueBlurb;
        TextView yearBlurb;
        TextView monthBlurb;
        TextView typeBlurb;


        if (row==null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.iconrow, parent, false);
        }


        icon = (ImageView) row.findViewById(R.id.icon);
        textBlurb = (TextView) row.findViewById(R.id.label);
        valueBlurb = (TextView) row.findViewById(R.id.value);
        monthBlurb = (TextView) row.findViewById(R.id.monthtext);
        yearBlurb = (TextView) row.findViewById(R.id.yeartext);
        typeBlurb = (TextView) row.findViewById(R.id.typetext);

        typeBlurb.setText("");
        yearBlurb.setText("");
        monthBlurb.setText("");

        textBlurb.setTextColor(Color.BLACK);
        textBlurb.setTypeface(textBlurb.getTypeface(), Typeface.NORMAL);
        valueBlurb.setTextColor(Color.BLACK);
        monthBlurb.setTextColor(Color.BLACK);
        yearBlurb.setTextColor(Color.BLACK);
        typeBlurb.setTextColor(Color.BLACK);
        textBlurb.setWidth(40);


        if(listWidgets.size()  > position)
        {
            ItemWidget widget = listWidgets.get(position);
            icon.setImageResource(widget.thumb);
            textBlurb.setText(widget.option);
            valueBlurb.setText(widget.getFormatted());
            textBlurb.setTag(widget);

            if(widget.isExtra && widget.option.equalsIgnoreCase("extra"))
            {
//				Log.v("pdc", "foundextra at :" + position + ", startmonth = " + widget.startmonth);
                icon.setImageResource(widget.thumb);
                textBlurb.setText("Extra Payment");
                textBlurb.setTextColor(Color.parseColor("#33773a"));
                textBlurb.setTypeface(null, Typeface.BOLD_ITALIC);
                typeBlurb.setText(widget.frequency);
                valueBlurb.setText(widget.getFormatted());
                textBlurb.setTag(widget);
                monthBlurb.setText(widget.startmonth);
                yearBlurb.setText(Integer.toString(widget.startyear));


            }

            if(widget.action.equals("launch"))
            {
                valueBlurb.setText("");
                textBlurb.setTypeface(null, Typeface.BOLD);
            }

        } else {


            if(textBlurb.getText().toString().contains("t"))
            {
                icon.setImageResource(R.drawable.dollar_black);
                valueBlurb.setText("dollar");

            } else {
                icon.setImageResource(R.drawable.calendar_blank_black);

            }

        }

        return row;
    }



}
