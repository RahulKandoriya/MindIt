package com.abotstudio.apps.rememberanything.mindit.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.abotstudio.apps.rememberanything.mindit.PeriodicTask;
import com.abotstudio.apps.rememberanything.mindit.R;
import com.abotstudio.apps.rememberanything.mindit.db.Post;
import com.abotstudio.apps.rememberanything.mindit.viewModel.PostViewModel;
import java.util.concurrent.TimeUnit;
import androidx.work.PeriodicWorkRequest;

public class PostDialog extends AppCompatDialogFragment {

    private PostViewModel postViewModel;

    private EditText editTitleText;
    private EditText editSubText;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);



        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        builder.setView(view)
                .setTitle(R.string.add_text)
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title_text = editTitleText.getText().toString();
                        String sub_text = editSubText.getText().toString();

                        postViewModel.savePost(new Post(title_text, sub_text, 1, false, new PeriodicWorkRequest.Builder(PeriodicTask.class, 1, TimeUnit.HOURS).build()));

                        editTitleText.setHint(R.string.title_hint);
                        editSubText.setHint(R.string.subtext_hint);

                    }
                });

        editTitleText = view.findViewById(R.id.edit_title);
        editSubText = view.findViewById(R.id.edit_subtext);

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
