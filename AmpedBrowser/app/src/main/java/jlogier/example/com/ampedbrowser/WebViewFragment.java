package jlogier.example.com.ampedbrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class WebViewFragment extends Fragment {
    private static WebView webView;

    /**
     * The {@link android.support.v4.widget.SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		/* Creating view corresponding to the fragment */
        View v1 = inflater.inflate(R.layout.webview, container, false);
        //final View v2 = inflater.inflate(R.layout.actionbar_layout, container, false);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) v1.findViewById(R.id.swipe_container);

        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

		/* Initializing and loading url in WebView */
        webView = (WebView)v1.findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());

        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl("http://www.google.com");
        webView.clearHistory();

        if (Build.VERSION.SDK_INT >= 21)
            webView.enableSlowWholeDocumentDraw();

        final EditText searchtext = (EditText) getActivity().findViewById(R.id.searchbar);

        final ImageButton btn = (ImageButton) getActivity().findViewById(R.id.go_forward);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchtext.getText().toString();
                text = "http://" + text;
                if (URLUtil.isValidUrl(text)) {
                    webView.loadUrl(text);
                }
                else if (!text.equals("")) {
                    webView.loadUrl("https://www.google.com/?gws_rd=ssl#q=" + text);
                }
                else {
                    webView.loadUrl("http://www.google.com");
                }
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
            }
        });

        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String text = searchtext.getText().toString();
                    text = "http://" + text;
                    if (URLUtil.isValidUrl(text)) {
                        webView.loadUrl(text);
                    }
                    else if (!text.equals("")) {
                        webView.loadUrl("https://www.google.com/?gws_rd=ssl#q=" + text);
                    }
                    else {
                        webView.loadUrl("http://www.google.com");
                    }
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return v1;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //This tells the parent activity that the Fragment wants to add items to the Actionbar
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initiateRefresh() {
        webView.reload();
    }

    public static boolean canGoBack(){
        return webView.canGoBack();
    }

    public static void goBack(){
        webView.goBack();
    }

    /**
     * Web View Client to run web pages within the app
     *
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Show progress bar in action bar while page loads
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Hide progress bar in action bar when page is finished loading
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}