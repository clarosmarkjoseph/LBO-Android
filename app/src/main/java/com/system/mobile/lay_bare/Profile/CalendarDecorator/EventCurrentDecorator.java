package com.system.mobile.lay_bare.Profile.CalendarDecorator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.system.mobile.lay_bare.R;

/**
 * Created by Mark on 07/09/2017.
 */

public class EventCurrentDecorator implements DayViewDecorator {
    private Drawable highlightDrawable;
    private Context context;

    public EventCurrentDecorator( Context context) {
        this.context = context;
        highlightDrawable = this.context.getResources().getDrawable(R.drawable.default_dot);
    }


    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today());
    }


    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.themeBlack)));
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.5f));
    }
}

