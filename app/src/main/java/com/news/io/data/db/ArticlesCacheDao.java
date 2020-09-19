package com.news.io.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.news.io.model.ArticleList;
import com.news.io.model.ArticlesCache;

import java.util.Date;

@Dao
public interface ArticlesCacheDao {
    @Query("SELECT articles FROM articles_cache WHERE category = :category LIMIT 1")
    LiveData<ArticleList> getFromCache(String category);

    @Query("SELECT COUNT(*) FROM articles_cache WHERE category = :category AND last_fetch > :timeout")
    int hasData(String category, Date timeout);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToCache(ArticlesCache cache);
}