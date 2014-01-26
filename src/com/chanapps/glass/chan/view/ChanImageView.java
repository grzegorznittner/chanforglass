package com.chanapps.glass.chan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import com.chanapps.glass.chan.R;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.ChanThread;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/22/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanImageView {

    private static final String IMAGE_FORMAT = "https://i.4cdn.org/%1$s/src/%2$s%3$s";

    private Context mContext;

    public ChanImageView(Context context) {
        mContext = context;
    }

    public void displayImage(String board, long tim, String ext) {
        final String url = String.format(IMAGE_FORMAT, board, tim, ext);

        //displayOnCard(url);
        //displayOnWebView(url);
        displayOnBrowser(url);
    }

    private void displayOnCard(final String url) {
        final Card card = new Card(mContext);
        CardImageLoader.loadCardImage(card, Card.ImageLayout.FULL, url, new Runnable() {
            @Override
            public void run() {
                File file = ImageLoader.getInstance().getDiscCache().get(url);
                if (file != null && file.exists() && file.length() > 0) {
                    Uri uri = Uri.fromFile(file);
                    card.addImage(uri);
                    if (mContext instanceof Activity)
                        ((Activity)mContext).setContentView(card.toView());
                }
            }
        });
    }

    private void displayOnWebView(final String url) {
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                if (mContext instanceof Activity)
                    ((Activity)mContext).setContentView(webView);
                webView.invokeZoomPicker();
            }
        };
        WebView webView = new WebView(mContext);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(url);
        //String data = String.format(HTML_IMAGE_FORMAT, url);
        //webView.loadData(data, HTML_MIME_TYPE, null);
    }

    //private static final String HTML_IMAGE_FORMAT = "<html><body><img src=\"%1$s\"></img></body></html>";
    //private static final String HTML_MIME_TYPE = "text/html";

    private void displayOnBrowser(final String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (mContext instanceof Activity) {
            ((Activity)mContext).startActivity(intent);
            ((Activity)mContext).finish();
        }
    }

}
