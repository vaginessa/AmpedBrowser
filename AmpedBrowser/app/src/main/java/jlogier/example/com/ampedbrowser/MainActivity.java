package jlogier.example.com.ampedbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import android.widget.Toolbar;

public class MainActivity extends ActionBarActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    protected SharedPreferences prefs;
    private WebView webView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ActionBar mActionBar;
    private float mActionBarHeight;
    private boolean temp;
    private ShareActionProvider mShareActionProvider = null;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        temp = prefs.getBoolean("private_preference", false);
        if (temp) {
            setTheme(R.style.AppTheme_Private);
        }
        super.onCreate(savedInstanceState);

        // Sets the action bar as an overlay to prevent the view from "jumping" when it is shown or hidden
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mActionBar = getSupportActionBar();

        // Used to get actionbar size
        final TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);

        mActionBar.setCustomView(R.layout.actionbar_layout);
        mActionBar.setHideOnContentScrollEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(true);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) + 50);

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
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_SCROLL || action == MotionEvent.ACTION_POINTER_2_DOWN) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }

                v.onTouchEvent(event);
                return true;
            }
        });

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setUserAgentString(prefs.getString("user_agent", ""));
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
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        Toast.makeText(this, intent.getComponent().toString(), Toast.LENGTH_LONG).show();
        return(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Handle action bar item clicks here.
         */
        int id = item.getItemId();

        if (id == R.id.action_home) {
            webView.loadUrl("http://" + prefs.getString("homepage_preference", "www.google.com"));
            return true;
        }
        else if (id == R.id.action_desktop) {
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36");
            webView.reload();
            webView.getSettings().setUserAgentString("");
            return true;
        }
        else if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentUrl);
            Log.d("DEBUG", "CURRENT URL: " + currentUrl);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
            return true;
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
        webView.loadUrl(currentUrl);

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
            url = prefs.getString("search_preference", "https://www.google.com/?gws_rd=ssl#q=") + text;

        return url;
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);

        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    /**
     * Web View Client to run web pages within the app
     *
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("intent://")){
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    // The following flags launch the app outside the current app
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            else if (Uri.parse(url).getScheme().equals("market")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    Activity host = (Activity) view.getContext();
                    host.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    // Google Play app is not installed, you may want to open the app store link
                    Uri uri = Uri.parse(url);
                    view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                    return false;
                }
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Show progress bar in action bar while page loads
            mSwipeRefreshLayout.setRefreshing(true);
            EditText searchtext = (EditText) findViewById(R.id.searchbar);
            searchtext.setText(url);
            currentUrl = url;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Hide progress bar in action bar when page is finished loading
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}