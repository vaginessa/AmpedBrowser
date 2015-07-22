package jlogier.example.com.ampedbrowser;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends ActionBarActivity {

    protected SharedPreferences prefs;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private boolean toggle = false;

    private Integer[] iconArray = { null, null, null, null, null, null, null};
    private String[] optionsArray = { "Home", "Remote Control", "Power Consumption", "Distribution", "About" };
    private String[] mDrawerTitles = {"Smart Power Strip", "Remote Control", "Power Consumption", "Distribution"};
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("jlogier.example.com.ampedbrowser", Context.MODE_PRIVATE);


        LayoutInflater inflater = this.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);
        TextView text = (TextView) rowView.findViewById(R.id.text1);

        mDrawerList = (ListView) findViewById(R.id.navList);
        //scales the navigation drawer differently for a tablet and phone similar to Google's apps
        if (isTablet(this)) {
            mDrawerList.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());
        } else {
            mDrawerList.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        //Launches proper fragment when navigation drawer option is selected
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Initial screen when app loads
        browserFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void addDrawerItems() {
        CustomListAdapter adapter = new CustomListAdapter(this, optionsArray, iconArray);

        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                updateFragment();
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        selectedPosition = 0;
        updateFragment();
    }

    private void browserFragment() {
        FragmentManager fm = getSupportFragmentManager();
        WebViewFragment frag = new WebViewFragment();

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_frame, frag);
        ft.addToBackStack(null);
        ft.commit();

        mActivityTitle = mDrawerTitles[0];
        invalidateOptionsMenu();
    }

    private void updateFragment() {
        /* Getting reference to the FragmentManager */
        FragmentManager fm = getSupportFragmentManager();
        //Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        if (selectedPosition == 0) {
            browserFragment();
            //sets the action bar title back to the app name when on "home screen"
            mActivityTitle = mDrawerTitles[0];
        }
        else if (selectedPosition == 4) {
            CharSequence[] ch = { "Amped Browser - Josh Logier\n\nApp Version: " + getVersion() +
                    "\n\nThis is a lightweight and beautiful browser for basic browsing needs " +
                    "with no bloat or complicated features. Mobile web made simple." };
            showDialog("About", ch);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //menu.findItem(R.id.urlfield).getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        if (id == R.id.action_refresh) {
            //Request new data from Raspberry Pi
            Thread t = requestData();
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }*/

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // change the title
        FragmentManager fm = this.getSupportFragmentManager();
        //fm.popBackStackImmediate();
        Fragment cur = fm.findFragmentById(R.id.content_frame);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        }

        if (cur instanceof WebViewFragment) {
            selectedPosition = 0;
            if(WebViewFragment.canGoBack()){
                WebViewFragment.goBack();
                return;
            }
            else {
                fm.popBackStackImmediate();
                super.onBackPressed();
            }
        }/* else if (cur instanceof RemoteControlFragment) {
            selectedPosition = 1;
            getSupportActionBar().setTitle(mDrawerTitles[1]);
        } else if (cur instanceof BarChartFragment) {
            selectedPosition = 2;
            getSupportActionBar().setTitle(mDrawerTitles[2]);
        } else if (cur instanceof PieChartFragment) {
            selectedPosition = 3;
            getSupportActionBar().setTitle(mDrawerTitles[3]);
        }*/
        super.onBackPressed();
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


    private boolean checkConnection() {
        try {
            byte[] b = new byte[]{(byte) 192, (byte) 168, (byte) 43, (byte) 40};
            InetAddress addr = InetAddress.getByAddress(b);
            if (addr.isReachable(325))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}