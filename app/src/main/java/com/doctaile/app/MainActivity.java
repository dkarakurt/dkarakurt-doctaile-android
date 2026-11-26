package com.doctaile.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

/**
 * doctaile: assets/index.html'i tam ekran WebView'da açar.
 * - Fold açılıp kapanınca yeniden başlamaz (manifest'te configChanges).
 * - Android 15+ kenardan kenara ekranda içerik sistem çubuklarının altında kalmasın diye boşluk bırakılır.
 * - Android 13+ yeni geri tuşu API'si: sayfa içinde geri gidilebiliyorsa (telefonda açık tarif sayfası) onu kapatır.
 */
public class MainActivity extends Activity {
    private static final int INK = 0xFF121820;
    private WebView web;
    private OnBackInvokedCallback backCallback;
    private boolean backRegistered;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        web.setBackgroundColor(INK);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // profiller, seçimler ve favoriler cihazda saklanır
        s.setAllowFileAccess(true);
        // İki parmakla yakınlaştırma kapalı; sayfa her zaman ekran genişliğinde
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;   // IBA / Difford's bağlantıları tarayıcıda açılır
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                updateBackHandling();
            }
        });

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(INK);
        root.addView(web, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets b = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout()
                        | WindowInsets.Type.ime());
                v.setPadding(b.left, b.top, b.right, b.bottom);
                return WindowInsets.CONSUMED;
            });
        }

        if (Build.VERSION.SDK_INT >= 33) {
            backCallback = () -> {
                if (web != null && web.canGoBack()) web.goBack();
                updateBackHandling();
            };
        }

        if (state == null || web.restoreState(state) == null) {
            web.loadUrl("file:///android_asset/index.html");
        }
        setContentView(root);
    }

    /** Geri gidilecek bir sayfa varsa geri tuşunu yakala; yoksa sistem uygulamayı kapatsın. */
    private void updateBackHandling() {
        if (Build.VERSION.SDK_INT < 33 || backCallback == null || web == null) return;
        boolean need = web.canGoBack();
        OnBackInvokedDispatcher d = getOnBackInvokedDispatcher();
        if (need && !backRegistered) {
            d.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
            backRegistered = true;
        } else if (!need && backRegistered) {
            d.unregisterOnBackInvokedCallback(backCallback);
            backRegistered = false;
        }
    }

    @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); if (web != null) web.saveState(out); }
    @Override protected void onResume() { super.onResume(); if (web != null) web.onResume(); }
    @Override protected void onPause() { if (web != null) web.onPause(); super.onPause(); }

    /** Android 12 ve öncesi için eski geri tuşu yolu. */
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
