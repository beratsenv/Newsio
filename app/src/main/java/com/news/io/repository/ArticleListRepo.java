package com.news.io.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.news.io.R;
import com.news.io.data.api.ApiResponse;
import com.news.io.data.api.NewsioService;
import com.news.io.data.db.ArticlesCacheDao;
import com.news.io.model.ArticleList;
import com.news.io.model.ArticlesCache;
import com.news.io.model.Resource;
import com.news.io.util.AppExecutor;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.news.io.NewsioApplication.getAppContext;
import static com.news.io.NewsioApplication.getPreferences;

public class ArticleListRepo {
    private static ArticleListRepo repository;

    private final NewsioService apiService;
    private final ArticlesCacheDao articlesCacheDao;
    private final AppExecutor executor;

    private ArticleListRepo(NewsioService apiService, ArticlesCacheDao articlesCacheDao,
                            AppExecutor executor) {
        this.apiService = apiService;
        this.articlesCacheDao = articlesCacheDao;
        this.executor = executor;
    }

    public synchronized static ArticleListRepo getInstance(NewsioService apiService,
                                                           ArticlesCacheDao articlesCacheDao,
                                                           AppExecutor executor) {
        if (repository == null) {
            repository = new ArticleListRepo(apiService, articlesCacheDao, executor);
        }
        return repository;
    }

    public LiveData<Resource<ArticleList>> loadArticleList(@NonNull String category,
                                                           boolean forceRefresh) {
        return new NetworkBound<ArticleList, ApiResponse>(executor) {
            @Override
            protected void saveCallResults(@NonNull ApiResponse item) {
                ArticlesCache cache = new ArticlesCache();
                cache.setCategory(category);
                cache.setArticles(new ArticleList(item.getArticles()));
                cache.setLastFetch(new Date(System.currentTimeMillis()));
                articlesCacheDao.addToCache(cache);
            }

            @Override
            protected boolean shouldFetch(@Nullable ArticleList data) {
                return forceRefresh || data == null || data.getArticles().isEmpty();
            }


            //Get data article from db

            @NonNull
            @Override
            protected LiveData<ArticleList> loadFromDb() {
                return articlesCacheDao.getFromCache(category);
            }

            @NonNull
            @Override
            protected Call<ApiResponse> createCall() {
                String country = getPreferences().getString(getAppContext().getString(R.string.settings_country_key), getAppContext().getString(R.string.settings_country_usa_value));
                String pageSize = getPreferences().getString(getAppContext().getString(R.string.setting_page_size_key), getAppContext().getString(R.string.settings_max_page_default_value));

                return apiService.getTopArticlesByCategory(
                        category,
                        country,
                        pageSize,
                        NewsioService.API_KEY
                );
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ArticleList>> searchQuery(String query) {

        MediatorLiveData<Resource<ArticleList>> result = new MediatorLiveData<>();
        apiService.searchQuery(query, NewsioService.API_KEY)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            ApiResponse body = response.body();
                            if (body != null) {
                                ArticleList list = new ArticleList(body.getArticles());
                                result.setValue(Resource.success(list));
                            }
                        }

                    }

                    @Override
                    @EverythingIsNonNull
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        result.setValue(Resource.error(t.toString(), null));
                    }
                });
        return result;
    }


}