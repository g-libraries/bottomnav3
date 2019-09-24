package com.core.bottomnav.bottomnavview;

import android.graphics.Typeface;

@SuppressWarnings("unused")
public interface IBottomCircleNav {
    void setTypeface(Typeface typeface);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);

    void setBadgeValue(int position, String value);
}
