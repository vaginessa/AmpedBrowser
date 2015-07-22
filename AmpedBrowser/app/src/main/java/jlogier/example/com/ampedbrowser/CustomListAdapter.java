package jlogier.example.com.ampedbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.mylist, itemname);

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text1);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icIcon);

        if (isTablet(context)) {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        else {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (imgid[position] != null) {
            params1.setMargins(25, 0, 0, 0);
            txtTitle.setLayoutParams(params1);

            imageView.setImageResource(imgid[position]);
        }
        else {
            params1.setMargins(0, 0, 0, 0);
            txtTitle.setLayoutParams(params1);

            params1.setMargins(dpToPix(10), 0, 0, 0);
            imageView.setLayoutParams(params1);
        }
        txtTitle.setText(itemname[position]);
        return rowView;
    };

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //This method allows layout params to scale identically across any device by converting dp to pixels for that specific device
    private int dpToPix(float dp) {
        // The gesture threshold expressed in dp
        final float GESTURE_THRESHOLD_DP = dp;

        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int mGestureThreshold = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
        return mGestureThreshold;
    }
}