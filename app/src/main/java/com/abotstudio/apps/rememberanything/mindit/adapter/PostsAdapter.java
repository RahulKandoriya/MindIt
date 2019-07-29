package com.abotstudio.apps.rememberanything.mindit.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.abotstudio.apps.rememberanything.mindit.PeriodicTask;
import com.abotstudio.apps.rememberanything.mindit.R;
import com.abotstudio.apps.rememberanything.mindit.db.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(Post post);
    }

    public interface OnUpdateButtonClickListener {
        void onUpdateButtonClicked(Post post);
    }

    public interface OnItemClickListener {
        void onItemClickListener(Post post);
    }

    public interface OnSwitchClickListener {

        void onSwitchClickListener(Post post);
    }

    private List<Post> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private OnSwitchClickListener onSwitchClickListener;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;
    private OnUpdateButtonClickListener onUpdateButtonClickListener;
    public  WorkManager workManager = WorkManager.getInstance();
    public String instructText = " ";



    public PostsAdapter(Context context, OnDeleteButtonClickListener listener, OnItemClickListener item_listener, OnSwitchClickListener switch_listener, OnUpdateButtonClickListener update_listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = listener;
        this.onItemClickListener = item_listener;
        this.onSwitchClickListener = switch_listener;
        this.onUpdateButtonClickListener = update_listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_post_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Post> newData) {
        if (data != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvContent, instruct_content;
        private ImageView btnDelete, btnUpdate;
        private Switch notification_switch;

        public int timeValue;

        public SeekBar seekBar;




        PostViewHolder(View itemView) {
            super(itemView);
            instruct_content = itemView.findViewById(R.id.instruct_content);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            notification_switch = itemView.findViewById(R.id.notification_switch);
            seekBar = itemView.findViewById(R.id.seekBar);

        }

        void bind(final Post post) {
            if (post != null) {

                timeValue = post.getTimeCount();
                tvTitle.setText(post.getTitle());
                tvContent.setText(post.getContent());
                instructText = (context.getResources().getString(R.string.instruct_text) + " " + String.valueOf(timeValue) + " " + context.getResources().getString(R.string.hour_text));
                instruct_content.setText(instructText);

                seekBar.setMax(24);
                seekBar.setProgress(timeValue);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        int min = 1;

                        if (progress<min) {
                            timeValue = min;
                            seekBar.setProgress(timeValue);


                        } else {

                            timeValue = progress;
                            seekBar.setProgress(timeValue);

                        }

                        instructText = (context.getResources().getString(R.string.instruct_text) + " " + String.valueOf(timeValue) + " " + context.getResources().getString(R.string.hour_text));

                        instruct_content.setText(instructText);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                notification_switch.setChecked(post.isNotification_check());

                seekBar.setEnabled(!post.isNotification_check());

                if (post.isNotification_check()){

                    btnDelete.setImageResource(R.drawable.ic_close_diabled_24dp);
                    btnUpdate.setImageResource(R.drawable.ic_edit_diabled_24dp);


                } else {
                    btnDelete.setImageResource(R.drawable.ic_close_black_24dp);
                    btnUpdate.setImageResource(R.drawable.ic_edit_black_24dp);
                }


                notification_switch.setOnClickListener(v -> {
                    if(onSwitchClickListener!= null) {

                        post.setNotification_check(!post.isNotification_check());

                        post.setTimeCount(timeValue);

                        Data.Builder builder = new Data.Builder();

                        builder.putInt("post_id", post.getId());
                        builder.putString("post_title", post.getTitle());
                        builder.putString("post_content", post.getContent());

                        Data data1 = builder.build();


                        post.setPeriodicWorkRequest(new PeriodicWorkRequest.Builder(PeriodicTask.class, timeValue, TimeUnit.HOURS, 1, TimeUnit.MINUTES).addTag(String.valueOf(post.getId())).setInputData(data1).build());

                        if (post.isNotification_check()){

                            workManager.enqueue(post.getPeriodicWorkRequest());
                            Snackbar.make(itemView,R.string.started_text,Snackbar.LENGTH_LONG).show();

                        } else {

                            workManager.cancelAllWorkByTag(String.valueOf(post.getId()));
                            Snackbar.make(itemView,R.string.stopped_text,Snackbar.LENGTH_LONG).show();

                        }
                        onSwitchClickListener.onSwitchClickListener(post);
                    }
                });


                btnDelete.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null && !post.isNotification_check())
                        onDeleteButtonClickListener.onDeleteButtonClicked(post);

                });

                btnUpdate.setOnClickListener(v -> {
                    if (onUpdateButtonClickListener != null && !post.isNotification_check())
                        onUpdateButtonClickListener.onUpdateButtonClicked(post);
                });
                tvContent.setOnClickListener(v -> {
                    if (onItemClickListener!= null)
                        onItemClickListener.onItemClickListener(post);
                });


            }
        }

    }

    class PostDiffCallback extends DiffUtil.Callback {

        private final List<Post> oldPosts, newPosts;

        public PostDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getId() == newPosts.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }



}
