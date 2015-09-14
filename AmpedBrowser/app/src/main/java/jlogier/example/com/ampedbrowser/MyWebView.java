package jlogier.example.com.ampedbrowser;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Toast;

/**
 * Created by Josh on 8/5/2015.
 */
public class MyWebView extends WebView {

    private static final int ID_SAVEIMAGE = 1;
    private static final int ID_VIEWIMAGE = 2;
    private static final int ID_OPENLINK = 3;
    private static final int ID_COPYLINK = 4;
    private static final int ID_SHARELINK = 5;
    private Context ctx;

    public MyWebView(Context context) {
        super(context);
        ctx = context;
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        final WebView.HitTestResult result = getHitTestResult();
        final String address = result.getExtra();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == ID_SAVEIMAGE) {
                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(address));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, address);
                        DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                    }
                    catch (Exception e) {
                        Toast.makeText(ctx, "Invalid URL. Click the image and try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (item.getItemId() == ID_VIEWIMAGE) {
                    loadUrl(address);
                }
                else if (item.getItemId() == ID_OPENLINK) {
                    Intent newWindow = new Intent(Intent.ACTION_VIEW);
                    newWindow.setType("text/plain");
                    newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    newWindow.setData(Uri.parse(address));
                    ctx.startActivity(newWindow);
                }
                else if (item.getItemId() == ID_COPYLINK) {
                    ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", address);
                    clipboard.setPrimaryClip(clip);
                }
                else if (item.getItemId() == ID_SHARELINK) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, address);
                    ctx.startActivity(Intent.createChooser(shareIntent, "Pick an app"));

                }
                return true;
            }
        };

        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // Menu options for an image. Set the header title to the image url
            //menu.setHeaderTitle(address);
            menu.setHeaderTitle("Image");
            menu.add(0, ID_SAVEIMAGE, 0, "Save image").setOnMenuItemClickListener(handler);
            menu.add(0, ID_VIEWIMAGE, 0, "View image").setOnMenuItemClickListener(handler);
        } else if (result.getType() == WebView.HitTestResult.ANCHOR_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            // Menu options for a hyperlink. Set the header title to the link url
            //menu.setHeaderTitle(address);
            menu.setHeaderTitle("Link");
            menu.add(0, ID_OPENLINK, 0, "Open in a new window").setOnMenuItemClickListener(handler);
            menu.add(0, ID_COPYLINK, 0, "Copy link URL").setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARELINK, 0, "Share link URL").setOnMenuItemClickListener(handler);
        }
    }
}
