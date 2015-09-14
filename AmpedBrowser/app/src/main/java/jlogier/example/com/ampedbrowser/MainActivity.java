package jlogier.example.com.ampedbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    protected SharedPreferences prefs;
    private ActionBar mActionBar;
    private MyWebView webView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean temp;
    private String theme = "default";
    private ShareActionProvider mShareActionProvider = null;
    private String currentUrl;
    private Context context;
    private ArrayAdapter<String> listViewAdapter;
    private boolean desktopFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        temp = prefs.getBoolean("private_preference", false);
        theme = prefs.getString("theme_preference", "default");
        if (temp)
            setTheme(R.style.AppTheme_Private);
        else if (theme.equals("black"))
            setTheme(R.style.AppTheme_Black);
        else if (theme.equals("red"))
            setTheme(R.style.AppTheme_Red);
        else if (theme.equals("orange"))
            setTheme(R.style.AppTheme_Orange);
        else if (theme.equals("yellow"))
            setTheme(R.style.AppTheme_Yellow);
        else if (theme.equals("green"))
            setTheme(R.style.AppTheme_Green);
        else if (theme.equals("blue"))
            setTheme(R.style.AppTheme);
        else if (theme.equals("purple"))
            setTheme(R.style.AppTheme_Purple);
        else if (theme.equals("pink"))
            setTheme(R.style.AppTheme_Pink);
        else if (theme.equals("teal"))
            setTheme(R.style.AppTheme_Teal);
        else if (theme.equals("brown"))
            setTheme(R.style.AppTheme_Brown);
        else
            setTheme(R.style.AppTheme);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences.Editor editor = prefs.edit();
        TelephonyManager connection = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_B ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_1xRTT ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_IDEN ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS ||
                connection.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA) {
            editor.putBoolean("network_flag", true);
            editor.putBoolean("prev_savedata", prefs.getBoolean("savedata_preference", false));
            editor.putBoolean("prev_adblock", prefs.getBoolean("adblock_preference", false));
            editor.putBoolean("savedata_preference", true);
            editor.putBoolean("adblock_preference", true);
            editor.apply();
        }
        else if (prefs.getBoolean("network_flag", false)) {
            editor.putBoolean("network_flag", false);
            editor.putBoolean("savedata_preference", prefs.getBoolean("prev_savedata", false));
            editor.putBoolean("adblock_preference", prefs.getBoolean("prev_adblock", false));
            editor.apply();
        }

        super.onCreate(savedInstanceState);

        // Sets the action bar as an overlay to prevent the view from "jumping" when it is shown or hidden
        //supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        // Used to get actionbar size
        final TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        final float actionBarHeight = TypedValue.complexToDimensionPixelSize(typed_value.data,getResources().getDisplayMetrics());

        mActionBar.setCustomView(R.layout.actionbar_layout);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) - 50);

        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        mSwipeRefreshLayout.setDistanceToTriggerSync(400);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

		/* Initializing and loading url in WebView */
        webView = (MyWebView)findViewById(R.id.webView);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        registerForContextMenu(webView);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setUserAgentString(prefs.getString("ua_preference", ""));
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
        webView.getSettings().setJavaScriptEnabled(!prefs.getBoolean("adblock_preference", false));
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("http://" + prefs.getString("homepage_preference", "www.google.com"));

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

        TextView header = new TextView(context);
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.BlackColor));
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(0, 15, 0, 15);
        header.setText("Bookmarks");

        TextView footer = new TextView(context);
        footer.setTextSize(12);
        footer.setTextColor(getResources().getColor(R.color.BlackColor));
        footer.setGravity(Gravity.CENTER_HORIZONTAL);
        footer.setText("** Press and hold a bookmark to delete **");


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.right_drawer);
        mDrawerList.addHeaderView(header);
        mDrawerList.addFooterView(footer);

        ArrayList<String> bookmarks = new ArrayList<String>(Arrays.asList(loadArray("bookmark_titles")));
        listViewAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.drawer_list_item, bookmarks);
        mDrawerList.setAdapter(listViewAdapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text[] = {"Do you really want to delete this bookmark?"};
                showDialog("Remove", text, position);
                return true;
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            String[] bookmarkUrls = loadArray("bookmark_urls");
            mDrawerLayout.closeDrawers();

            if (Build.VERSION.SDK_INT >= 21 && prefs.getBoolean("bookmark_preference", true)) {
                Intent newWindow = new Intent(Intent.ACTION_VIEW, Uri.parse(bookmarkUrls[position-1]), context, MainActivity.class);

                newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
                        Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);

                startActivity(newWindow);
            }
            else
                webView.loadUrl(bookmarkUrls[position-1]);
        }
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
        else if (id == R.id.action_window) {
            Intent newWindow = new Intent(this, MainActivity.class);
            newWindow.setType("text/plain");

            if (Build.VERSION.SDK_INT >= 21) {
                newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
                        Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
            }
            else {
                newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            }
            startActivity(newWindow);
        }
        else if (id == R.id.action_bookmarks) {
            // Add to shared prefs, then update drawer list
            addBookmark();
        }
        else if (id == R.id.action_desktop) {
            webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/44.0.2403.133 Safari/537.36");
            webView.reload();
            desktopFlag = true;
            return true;
        }
        else if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentUrl);

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
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (prefs.getBoolean("private_preference", false) != temp || !prefs.getString("theme_preference", "default").equals(theme))
            recreate();

        webView.getSettings().setLoadsImagesAutomatically(!prefs.getBoolean("savedata_preference", false));
        webView.getSettings().setBlockNetworkImage(prefs.getBoolean("savedata_preference", false));

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        EditText searchtext = (EditText) findViewById(R.id.searchbar);
        searchtext.clearFocus();

        if(webView.canGoBack()) {
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

    private String checkInput(String text) {
        String url;

        if (text.startsWith("http://") || text.startsWith("https://") && text.contains(".") &&
                !text.contains(" "))
            url = text;
        else if (text.contains(".") && !text.contains(" "))
            url = "http://" + text;
        else {
            url = prefs.getString("search_preference", "https://www.google.com/?gws_rd=ssl#q=") +
                    text.replace(" ", "+");
        }

        return url;
    }

    public void addBookmark() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_bookmark_dialog, null);
        final AlertDialog.Builder alert;
        if (prefs.getString("theme_preference", "blue").equals("black") ||
                prefs.getBoolean("private_preference", false)) {
             alert = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        }
        else {
            alert = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        }
        alert.setView(textEntryView);
        final EditText titleText = (EditText) textEntryView.findViewById(R.id.bookmarkTitle);
        final EditText urlText = (EditText) textEntryView.findViewById(R.id.bookmarkUrl);
        titleText.setText(webView.getTitle());
        urlText.setText(webView.getUrl());

        alert.setTitle("Add Bookmark")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Add title and url to shared prefs
                                addToArray("bookmark_titles", titleText.getText().toString());
                                addToArray("bookmark_urls", urlText.getText().toString());
                                ArrayList<String> bookmarks = new ArrayList<String>(Arrays.asList(loadArray("bookmark_titles")));
                                listViewAdapter.clear();
                                listViewAdapter.addAll(bookmarks);
                                listViewAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        });
        alert.show();
    }

    public String[] loadArray(String arrayName) {
        int size = prefs.getInt(arrayName + "_size", 0);
        Log.d("DEBUG", "SIZE: " + size);
        String array[] = new String[size];
        for(int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    public boolean addToArray(String arrayName, String newString) {
        SharedPreferences.Editor editor = prefs.edit();
        int size = prefs.getInt(arrayName + "_size", 0);
        int newSize = size + 1;
        editor.putInt(arrayName + "_size", newSize);
        editor.putString(arrayName + "_" + size, newString);

        return editor.commit();
    }

    public void showDialog(String title, CharSequence[] items, final int position) {
        final CharSequence[] fitems = items;
        AlertDialog.Builder lmenu = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        final AlertDialog ad = lmenu.create();

        lmenu.setTitle(title);
        lmenu.setMessage(fitems[0]);
        lmenu.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Deletes the entry in shared prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("bookmark_titles_" + (position-1));
                editor.remove("bookmark_urls_" + (position-1));

                int numBookmarks = prefs.getInt("bookmark_titles_size", 0);
                if (position != numBookmarks) {
                    String curTitle = prefs.getString("bookmark_titles_" + numBookmarks, "");
                    String curUrl = prefs.getString("bookmark_urls_" + numBookmarks, "");
                    for (int i = numBookmarks; i > position - 1; i--) {
                        String prevTitle = prefs.getString("bookmark_titles_" + (i - 1), "");
                        String prevUrl = prefs.getString("bookmark_urls_" + (i - 1), "");

                        editor.putString("bookmark_titles_" + (i-1), curTitle);
                        editor.putString("bookmark_urls_" + (i-1), curUrl);

                        curTitle = prevTitle;
                        curUrl = prevUrl;
                    }
                }
                editor.putInt("bookmark_titles_size", numBookmarks - 1);
                editor.putInt("bookmark_urls_size", numBookmarks - 1);
                editor.apply();

                ArrayList<String> bookmarks = new ArrayList<String>(Arrays.asList(loadArray("bookmark_titles")));
                listViewAdapter.clear();
                listViewAdapter.addAll(bookmarks);
                listViewAdapter.notifyDataSetChanged();
            }
        });
        lmenu.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });
        lmenu.show();
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            else if (url.startsWith("market://")) {
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
            else if (url.startsWith("mailto://")) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(MainActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                startActivity(i);
                //view.reload();
                return true;
            }
            else {
                view.loadUrl(url);
            }
            return true;
        }

        public Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, cc);
            intent.setType("message/rfc822");
            return intent;
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

            webView.loadUrl("javascript:window.alert = function(){}");

            if (desktopFlag) {
                desktopFlag = false;
                webView.getSettings().setUserAgentString(prefs.getString("ua_preference", ""));
            }
        }
    }
}