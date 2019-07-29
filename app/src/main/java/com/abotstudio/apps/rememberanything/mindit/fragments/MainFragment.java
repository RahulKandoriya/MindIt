package com.abotstudio.apps.rememberanything.mindit.fragments;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abotstudio.apps.rememberanything.mindit.DismissReceiver;
import com.abotstudio.apps.rememberanything.mindit.MainActivity;
import com.abotstudio.apps.rememberanything.mindit.PostDetailActivity;
import com.abotstudio.apps.rememberanything.mindit.R;
import com.abotstudio.apps.rememberanything.mindit.adapter.PostsAdapter;
import com.abotstudio.apps.rememberanything.mindit.db.Post;

import com.abotstudio.apps.rememberanything.mindit.viewModel.PostViewModel;


public class MainFragment extends Fragment {

    public FloatingActionButton floatingActionButton;
    private PostsAdapter postsAdapter;
    private PostViewModel postViewModel;
    public ImageView  share_imageView, rate_imageView;
    public Intent shareIntentMain, rateIntentMain;
    private final String CHANNEL_ID = "mindit_code";
    public int NOTIFICATION_ID = 333;
    public int r;
    public Context mContext;
    public String title;
    public String content;


    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mContext = getContext();

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("MIND IT!");

        share_imageView = view.findViewById(R.id.imageViewShare);
        rate_imageView = view.findViewById(R.id.imageViewRate);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });

        share_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIntentMain = new Intent();
                shareIntentMain.setAction(Intent.ACTION_SEND);
                shareIntentMain.putExtra(Intent.EXTRA_TEXT, "Remember Anything you want! set offline notifications for reading it hourly.\nInstall Now https://goo.gl/ccsfyC");
                shareIntentMain.setType("text/plain");
                startActivity(shareIntentMain);
            }
        });

        rate_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateIntentMain = new Intent();
                Uri uriMain = Uri.parse("https://play.google.com/store/apps/details?id=com.abotstudio.apps.rememberanything.mindit");
                rateIntentMain  = new Intent(Intent.ACTION_VIEW, uriMain);
                startActivity(rateIntentMain);
            }
        });



        postsAdapter = new PostsAdapter(getContext(), this::onDeleteButtonClicked, this::onItemClickListener, this::onSwitchClickListener, this::onUpdateButtonClicked);

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        postViewModel.getAllPosts().observe(this, posts -> postsAdapter.setData(posts));

        RecyclerView recyclerView = view.findViewById(R.id.rvPostsLis);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postsAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));




        return view;
    }




    public void onDeleteButtonClicked(Post post) {
        postViewModel.deletePost(post);
        Log.i("tag", "delete");

    }

    public void onUpdateButtonClicked(Post post) {



        PostUpdateDialog postUpdateDialog = new PostUpdateDialog();
        Bundle args = new Bundle();
        args.putString("title_update", post.getTitle());
        args.putString("content_update", post.getContent());
        args.putInt("post_id", post.getId());
        postUpdateDialog.setArguments(args);
        postUpdateDialog.show(getChildFragmentManager(), "Update Things");

    }

    public void onItemClickListener(Post post) {

        Intent postDetail_intent = new Intent(getContext(), PostDetailActivity.class);
        postDetail_intent.putExtra("content_post", post.getContent());
        postDetail_intent.putExtra("title_post", post.getTitle());
        startActivity(postDetail_intent);

    }

    public void onSwitchClickListener(Post post) {

        postViewModel.updatePost(post);
        if (post.isNotification_check()){
            displayNotification(post);
        }


    }



    public void openAddDialog(){

        PostDialog postDialog = new PostDialog();
        postDialog.show(getChildFragmentManager(), "Things");


    }

    private void buildNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            CharSequence name="Quotes Notifications";
            String description="Contains all Quotes notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager=(NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            try {
                notificationManager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public void displayNotification(Post post){

        title = post.getTitle();
        content = post.getContent();
        NOTIFICATION_ID = post.getId();

        PendingIntent pendingActionIntent, pendingDismissIntent;
        Intent actionIntent, backIntent, dismissIntent;

        buildNotificationChannel();

        actionIntent = new Intent(mContext, PostDetailActivity.class);
        backIntent = new Intent(mContext, MainActivity.class);
        actionIntent.putExtra("title_post", title);
        actionIntent.putExtra("content_post", content);


        pendingActionIntent = PendingIntent.getActivities(mContext, NOTIFICATION_ID, new Intent[] {backIntent, actionIntent}, PendingIntent.FLAG_UPDATE_CURRENT);



        dismissIntent = new Intent(mContext, DismissReceiver.class);
        dismissIntent.putExtra("notification_id", NOTIFICATION_ID);

        pendingDismissIntent = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        nBuilder.setSmallIcon(R.drawable.ic_repeat_black_24dp);
        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setContentTitle(title);



        if (content.length() > 500 ) {

            r = (int) (Math.random() * (content.length()-300));
            nBuilder.setContentText(content.substring(0,200));
            nBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("\"" + content.substring(r,r+300)  + "\""));
        } else {
            nBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(content));
            nBuilder.setContentText(content);
        }
        nBuilder.setContentIntent(pendingActionIntent);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nBuilder.setOngoing(true);
        nBuilder.addAction(R.drawable.ic_close_black_24dp, "DISMISS", pendingDismissIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(NOTIFICATION_ID,nBuilder.build());

    }






}
