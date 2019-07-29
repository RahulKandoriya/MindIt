package com.abotstudio.apps.rememberanything.mindit.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.abotstudio.apps.rememberanything.mindit.db.Post;
import com.abotstudio.apps.rememberanything.mindit.db.PostDao;
import com.abotstudio.apps.rememberanything.mindit.db.PostsDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostViewModel extends AndroidViewModel {

    private PostDao postDao;
    private ExecutorService executorService;

    public PostViewModel(@NonNull Application application) {
        super(application);
        postDao = PostsDatabase.getInstance(application).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Post>> getAllPosts() {
        return postDao.findAll();
    }

    public void savePost(Post post) {
        executorService.execute(() -> postDao.save(post));
    }

    public void deletePost(Post post) {
        executorService.execute(() -> postDao.delete(post));
    }

    public void updatePost(Post post) {
        executorService.execute(() -> postDao.update(post));
    }
}
