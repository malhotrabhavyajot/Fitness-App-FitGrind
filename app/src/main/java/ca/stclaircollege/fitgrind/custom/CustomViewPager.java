package ca.stclaircollege.fitgrind.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by jnguy on 4/17/2017.
 * This class only overrides the onInterceptTouchEvent
 * According to a few sources on GitHub for PhotoView, it uses the same type of method to get the pinching/zooming affects on a photo.
 * However ViewPager uses ViewGroups which also use the same type of event which can end up crashing an android app.
 * So the current workaround right now is to override the method and to return it from a parent class.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
