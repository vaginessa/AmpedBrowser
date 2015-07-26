package jlogier.example.com.ampedbrowser;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieManager;
import java.net.URL;

public class MainActivity extends ActionBarActivity {

    protected SharedPreferences prefs;
    private WebView webView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ActionBar mActionBar;
    private float mActionBarHeight;
    private boolean temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        temp = prefs.getBoolean("private_preference", false);
        if (temp)
            setTheme(R.style.AppTheme_Private);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mActionBar = getSupportActionBar();

        findViewById(R.id.webView).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                float mfloat = findViewById(R.id.webView).getScrollY();
                if (mfloat > mActionBarHeight && mActionBar.isShowing()) {
                    mActionBar.hide();
                } else if (mfloat == 0 && !mActionBar.isShowing()) {
                    mActionBar.show();
                }
            }
        });

        mActionBar.setCustomView(R.layout.actionbar_layout);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(true);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

		/* Initializing and loading url in WebView */
        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(!prefs.getBoolean("savedata_preference", false));
        webView.getSettings().setBlockNetworkImage(prefs.getBoolean("savedata_preference", false));
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://" + prefs.getString("homepage_preference", "www.google.com"));
        webView.clearHistory();

        if (Build.VERSION.SDK_INT >= 21)
            webView.enableSlowWholeDocumentDraw();

        final EditText searchtext = (EditText) findViewById(R.id.searchbar);

        ImageButton btn = (ImageButton) findViewById(R.id.go_forward);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchtext = (EditText) findViewById(R.id.searchbar);
                String text = searchtext.getText().toString();
                String newText;
                if (!text.equals("")) {
                    newText = checkInput(text);
                    webView.loadUrl(newText);
                }
                searchtext.clearFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
            }
        });

        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    EditText searchtext = (EditText) findViewById(R.id.searchbar);
                    String text = searchtext.getText().toString();
                    String newText;
                    if (!text.equals("")) {
                        newText = checkInput(text);
                        webView.loadUrl(newText);
                    }
                    searchtext.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String address = uri.toString();
            webView.loadUrl(address);
        }

        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Handle action bar item clicks here.
         */
        int id = item.getItemId();

        if (id == R.id.action_home) {
            webView.loadUrl("http://" + prefs.getString("homepage_preference", "www.google.com"));
        }
        else if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        if (prefs.getBoolean("private_preference", false) != temp)
            recreate();

        webView.getSettings().setLoadsImagesAutomatically(!prefs.getBoolean("savedata_preference", false));
        webView.getSettings().setBlockNetworkImage(prefs.getBoolean("savedata_preference", false));

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        EditText searchtext = (EditText) findViewById(R.id.searchbar);
        searchtext.clearFocus();

        if(webView.canGoBack()){
            webView.goBack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onStop() {
        if (prefs.getBoolean("private_preference", false)) {
            WebViewDatabase.getInstance(this).clearFormData();
            android.webkit.CookieManager.getInstance().removeAllCookie();
            WebStorage.getInstance().deleteAllData();
            webView.clearCache(true);
        }

        super.onStop();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void showDialog(String title, CharSequence[] items) {
        Log.d("DEBUG", "function showdialog");
        final CharSequence[] fitems = items;

        AlertDialog.Builder lmenu = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        final AlertDialog ad = lmenu.create();
        lmenu.setTitle(title);
        lmenu.setMessage(fitems[0]);
        lmenu.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ad.dismiss();
            }
        });
        lmenu.show();
    }

    //Gets version number to display in about so I don't need to remember to update this
    private String getVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    private String checkInput(String text) {
        String url;

        if (text.startsWith("http://") && text.contains(".") && !text.contains(" "))
            url = text;
        else if (text.contains(".") && !text.contains(" "))
            url = "http://" + text;
        else
            url = "https://www.google.com/?gws_rd=ssl#q=" + text;

        return url;
    }

    /**
     * Web View Client to run web pages within the app
     *
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Show progress bar in action bar while page loads
            mSwipeRefreshLayout.setRefreshing(true);
            EditText searchtext = (EditText) findViewById(R.id.searchbar);
            searchtext.setText(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Hide progress bar in action bar when page is finished loading
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}