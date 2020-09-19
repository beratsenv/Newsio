package com.news.io.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.news.io.data.db.AppDatabase;
import com.news.io.data.db.BookmarksDao;
import com.news.io.repository.BookmarksRepo;
import com.news.io.util.AppExecutor;

public class BookmarksViewModel implements ViewModelProvider.Factory {
    private BookmarksRepo mRepository;

    public BookmarksViewModel(Application application) {
        BookmarksDao bookmarksDao = AppDatabase.getDatabase(application).bookmarksDao();
        AppExecutor executor = new AppExecutor();

        mRepository = BookmarksRepo.getInstance(bookmarksDao, executor);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BookmarksView(mRepository);
    }
}
