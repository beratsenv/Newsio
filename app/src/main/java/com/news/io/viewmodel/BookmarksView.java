package com.news.io.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.news.io.model.Article;
import com.news.io.repository.BookmarksRepo;

import java.util.List;

public class BookmarksView extends ViewModel {

    private BookmarksRepo repository;
    private LiveData<List<Article>> bookmarks;

    BookmarksView(@NonNull BookmarksRepo repository) {
        this.repository = repository;
    }

    public void init() {
        if (bookmarks != null)
            return;
        bookmarks = repository.getAllBookmarks();
    }

    public LiveData<List<Article>> getBookmarks() {
        return bookmarks;
    }

    public void insert(Article article) {
        repository.addBookmark(article);
    }

    public void delete(Article article) {
        repository.deleteBookmark(article);
    }
}
