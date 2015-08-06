package jlogier.example.com.ampedbrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Josh on 7/22/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    protected SharedPreferences prefs;
    private boolean temp;
    private boolean screentoggle;
    private String theme = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        temp = prefs.getBoolean("private_preference", false);
        screentoggle = prefs.getBoolean("fullscreen_preference", false);
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

        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);

        addPreferencesFromResource(R.xml.general_preferences);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getBoolean("private_preference", false) != temp || !prefs.getString("theme_preference", "default").equals(theme)) {
                    recreate();
                    finish();
                }
                else if (prefs.getBoolean("fullscreen_preference", false) && !screentoggle) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    recreate();
                    finish();
                }
                else if (!prefs.getBoolean("fullscreen_preference", false) && screentoggle) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    recreate();
                    finish();
                }
                else
                    finish();
            }
        });
    }
}
