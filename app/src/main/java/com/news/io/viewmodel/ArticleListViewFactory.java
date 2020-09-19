package com.news.io.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.news.io.data.api.NewsioService;
import com.news.io.data.db.AppDatabase;
import com.news.io.data.db.ArticlesCacheDao;
import com.news.io.repository.ArticleListRepo;
import com.news.io.util.AppExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleListViewFactory implements ViewModelProvider.Factory {

    private ArticleListRepo mRepository;

    public ArticleListViewFactory(Application application) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewsioService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NewsioService apiService = retrofit.create(NewsioService.class);
        ArticlesCacheDao articlesCacheDao = AppDatabase.getDatabase(application).articlesCacheDao();
        AppExecutor executor = new AppExecutor();


        mRepository = ArticleListRepo.getInstance(apiService, articlesCacheDao, executor);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArticleListView(mRepository);
    }
}
