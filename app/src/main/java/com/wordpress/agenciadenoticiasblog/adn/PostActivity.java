package com.wordpress.agenciadenoticiasblog.adn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class PostActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        webView = (WebView) findViewById(R.id.webView);

        String content = getIntent().getStringExtra("content");
        webView.loadData(content, "text/html; charset=UTF-8",null);
    }
}
