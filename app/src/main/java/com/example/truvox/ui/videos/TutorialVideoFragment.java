package com.example.truvox.ui.videos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.truvox.R;

public class TutorialVideoFragment extends Fragment {

    private static class VideoConfig {
        int webViewId;
        String iframeHtml;

        VideoConfig(int webViewId, String iframeHtml) {
            this.webViewId = webViewId;
            this.iframeHtml = iframeHtml;
        }
    }

    private void setupWebView(View parentView, int webViewId, String html) {
        WebView webView = parentView.findViewById(webViewId);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(html, "text/html", "utf-8");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorials, container, false);

        VideoConfig[] videos = new VideoConfig[]{
                new VideoConfig(R.id.constantWebview,
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/63OuVpZWm4g?si=EG57lFd7uT018HrW\" frameborder=\"0\" allowfullscreen></iframe>"),
                new VideoConfig(R.id.humanCurveWebView,
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/cP1_hMm4y2s?si=emBQ1xFJ82eR9CqA\" frameborder=\"0\" allowfullscreen></iframe>"),
                new VideoConfig(R.id.stairWebview,
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Majy2E2zw94?si=OLKTHN6RFoh5cZp8\" frameborder=\"0\" allowfullscreen></iframe>"),
                new VideoConfig(R.id.heteronymWebview,
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/nVrrTkP49Mw?si=fDd15fqUqMW61BTr\" frameborder=\"0\" allowfullscreen></iframe>"),
                new VideoConfig(R.id.chantingWebview,
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/-oeIlBYThoY?si=ccEqE7v0qlvUHcXq\" frameborder=\"0\" allowfullscreen></iframe>")
        };

        for (VideoConfig video : videos) {
            setupWebView(view, video.webViewId, video.iframeHtml);
        }

        return view;
    }
}
