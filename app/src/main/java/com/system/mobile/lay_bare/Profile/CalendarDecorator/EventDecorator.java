package com.system.mobile.lay_bare.Profile.CalendarDecorator;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;

/**
 * Created by Mark on 07/09/2017.
 */



public class EventDecorator implements DayViewDecorator {

    private final int color;
    private final ArrayList<CalendarDay> dates;


    public EventDecorator(int color, ArrayList<CalendarDay> dates) {
        this.color = color;
        this.dates = new ArrayList<>(dates);
//        drawable = new Drawable();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
//        Log.e("S",day.toString());
        return dates.contains(day);
    }


    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(7, color));
    }



}
