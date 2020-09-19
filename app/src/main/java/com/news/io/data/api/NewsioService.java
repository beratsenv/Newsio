package com.news.io.data.api;


import com.news.io.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsioService {
    String API_KEY = "517abb0c5b78411da33f835b6cf0a538";
    String BASE_URL = "https://newsapi.org/";

    @GET("v2/top-headlines")
    Call<ApiResponse> getTopArticlesByCategory(
            @Query("category") String category,
            @Query("country") String country,
            @Query("pageSize") String pageSize,
            @Query("apiKey") String apiKey);

    @GET("v2/everything")
    Call<ApiResponse> searchQuery(
            @Query("q") String query,
            @Query("apiKey") String apiKey);
}