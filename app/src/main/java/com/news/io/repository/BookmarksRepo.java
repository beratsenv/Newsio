package com.news.io.repository;

import androidx.lifecycle.LiveData;

import com.news.io.data.db.BookmarksDao;
import com.news.io.model.Article;
import com.news.io.util.AppExecutor;

import java.util.List;

public class BookmarksRepo {
    private static BookmarksRepo repository;

    private final BookmarksDao bookmarksDao;
    private final AppExecutor executor;


    private BookmarksRepo(BookmarksDao bookmarksDao, AppExecutor executor) {
        this.bookmarksDao = bookmarksDao;
        this.executor = executor;
    }

    public synchronized static BookmarksRepo getInstance(BookmarksDao bookmarksDao,
                                                         AppExecutor executor) {
        if (repository == null) {
            repository = new BookmarksRepo(bookmarksDao, executor);
        }
        return repository;
    }

    public LiveData<List<Article>> getAllBookmarks() {
        return bookmarksDao.getAllBookmarks();
    }

    public void addBookmark(Article article) {
        executor.getDiskIO().execute(() -> bookmarksDao.addBookmark(article));
    }

    public void deleteBookmark(Article article) {
        executor.getDiskIO().execute(() -> bookmarksDao.deleteBookmark(article));
    }

}
