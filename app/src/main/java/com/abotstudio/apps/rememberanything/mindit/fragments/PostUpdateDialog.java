package com.abotstudio.apps.rememberanything.mindit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.abotstudio.apps.rememberanything.mindit.PeriodicTask;
import com.abotstudio.apps.rememberanything.mindit.R;
import com.abotstudio.apps.rememberanything.mindit.db.Post;
import com.abotstudio.apps.rememberanything.mindit.viewModel.PostViewModel;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;


public class PostUpdateDialog extends AppCompatDialogFragment {


    private PostViewModel postViewModel;

    private int post_id;

    private Post post = new Post("title", "content", 1, false, new PeriodicWorkRequest.Builder(PeriodicTask.class, 1, TimeUnit.HOURS).build());
    private String title_text;
    private String sub_text;

    private EditText editTitleText;
    private EditText editSubText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);



        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        editTitleText = view.findViewById(R.id.edit_title);
        editSubText = view.findViewById(R.id.edit_subtext);

        Bundle bundle = getArguments();



        if (bundle != null){
            title_text = bundle.getString("title_update");
            sub_text = bundle.getString("content_update");
            post_id = bundle.getInt("post_id");

            editTitleText.setText(title_text);
            editSubText.setText(sub_text);
        }

        builder.setView(view)
                .setTitle(R.string.update_text)
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        title_text = editTitleText.getText().toString();
                        sub_text = editSubText.getText().toString();
                        post.setId(post_id);
                        post.setTitle(title_text);
                        post.setContent(sub_text);
                        postViewModel.updatePost(post);

                    }
                });



        return builder.create();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {



            //DialogListener listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListener");
        }
    }


}
