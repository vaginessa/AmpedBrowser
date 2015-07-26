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
import android.widget.LinearLayout;

/**
 * Created by Josh on 7/22/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    protected SharedPreferences prefs;
    private boolean temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        temp = prefs.getBoolean("private_preference", false);
        if (temp)
            setTheme(R.style.AppTheme_Private);
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
                if (prefs.getBoolean("private_preference", false) != temp) {
                    recreate();
                    finish();
                }
                else
                    finish();
            }
        });
    }
}
