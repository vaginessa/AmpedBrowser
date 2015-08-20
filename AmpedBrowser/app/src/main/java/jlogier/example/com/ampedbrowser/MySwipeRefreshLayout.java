package jlogier.example.com.ampedbrowser;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Josh on 8/11/2015.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        /*if (event.getAction() == MotionEvent.ACTION_SCROLL && MotionEvent.obtain(event).getY() > 0) {
            requestDisallowInterceptTouchEvent(true);
            return false;
        }*/
        /*WebView webView = (MyWebView)findViewById(R.id.webView);
        if (webView.getScrollY() > 0) {
            requestDisallowInterceptTouchEvent(true);
            return false;
        }*/

        return super.onInterceptTouchEvent(event);
    }
}
