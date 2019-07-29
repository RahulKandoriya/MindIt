package com.abotstudio.apps.rememberanything.mindit;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    public TextView postDetailText, postTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);


        postDetailText = findViewById(R.id.postDetail);

        postTitleText = findViewById(R.id.post_title);


        String content_text = getIntent().getStringExtra("content_post");
        String title_text = getIntent().getStringExtra("title_post");
        Log.i("title", title_text);

        postTitleText.setText(title_text);
        postDetailText.setText(content_text);



    }

}
