package com.example.truvox.ui.videos;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.truvox.R;


public class WarmupVideosFragment extends Fragment {

    private WebView breathingWebview;
    private WebView resonanceWebview;
    private WebView stretchingWebview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warmup_videos, container, false);

        breathingWebview = view.findViewById(R.id.breathingWebview);
        breathingWebview.getSettings().setJavaScriptEnabled(true);
        breathingWebview.getSettings().setDomStorageEnabled(true);
        breathingWebview.setWebChromeClient(new WebChromeClient());

        stretchingWebview = view.findViewById(R.id.stretchingWebview);
        stretchingWebview.getSettings().setJavaScriptEnabled(true);
        stretchingWebview.getSettings().setDomStorageEnabled(true);
        stretchingWebview.setWebChromeClient(new WebChromeClient());

        resonanceWebview = view.findViewById(R.id.resonanceWebview);
        resonanceWebview.getSettings().setJavaScriptEnabled(true);
        resonanceWebview.getSettings().setDomStorageEnabled(true);
        resonanceWebview.setWebChromeClient(new WebChromeClient());

        //String videoId = "dQw4w9WgXcQ";
        String breathinghtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/GreuJzVwXjU?si=QuR_brzYHkCIxy4R\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        String resonancehtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/3fgTkI_Nh2A?si=89dhFUr2bDmApg8D\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        String stretchinghtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/hMQUXTpn4Vk?si=pV-WqK2dnihfwkta\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";

        breathingWebview.loadData(breathinghtml, "text/html", "utf-8");
        resonanceWebview.loadData(resonancehtml, "text/html", "utf-8");
        stretchingWebview.loadData(stretchinghtml, "text/html", "utf-8");

        return view;
    }
}
