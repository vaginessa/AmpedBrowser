package jlogier.example.com.ampedbrowser;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Josh on 8/8/2015.
 */
public class MyDialogPreference extends DialogPreference {

    public MyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogMessage("Amped Browser - Josh Logier\nVersion: " + getVersion() +
                "\n\nFast. Simple. Beautiful. A web browser designed with mobile users in mind.\n\n"
                + "v1.4.3\n- Fixed a bug that caused the device to hang and crash" + "\n- Removed fullscreen option. " +
                "App now defaults to full screen mode\n\n" +
                "v1.4.2\n- Removed the ability for the action bar to slide away. This was causing issues" +
                "and incompatibilities with many websites due to the way I configured the layout" +
                "\n- Bug fixes and performance improvements" +
                "\n- Fixed a bookmark related crash\n\nv1.4\n- Bookmarks!\n- Option to define a custom user agent\n" +
                "- Automatically optimizes data usage when on 2G or slower\n- Many bug fixes and improvements");
    }

    //Gets version number to display in about so I don't need to remember to update this
    private String getVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }
}
