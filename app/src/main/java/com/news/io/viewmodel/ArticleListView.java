package com.news.io.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.news.io.model.ArticleList;
import com.news.io.model.Resource;
import com.news.io.repository.ArticleListRepo;

public class ArticleListView extends ViewModel {

    private String category;
    private ArticleListRepo repository;
    private LiveData<Resource<ArticleList>> articles;

    ArticleListView(ArticleListRepo repository) {
        this.repository = repository;
    }

    public void init(String category) {
        if (articles != null) {
            return;
        }
        this.category = category;
        articles = repository.loadArticleList(category, false);
    }

    public LiveData<Resource<ArticleList>> getArticles() {
        return articles;
    }

    public LiveData<Resource<ArticleList>> refreshArticles() {
        articles = repository.loadArticleList(category, true);
        return articles;
    }

    public LiveData<Resource<ArticleList>> searchQuery(String query) {
        return repository.searchQuery(query);
    }

}
