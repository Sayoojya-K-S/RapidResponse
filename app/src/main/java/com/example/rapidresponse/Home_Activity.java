package com.example.rapidresponse;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Adapter.NewsAdapter;
import DataModel.Article;
import DataModel.NewsResponse;
import api.NewsApiService;
import api.NewsRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    NewsAdapter adapter;
    String API_KEY = "2811496b0ab9407680544d97e570dde3";
    String BASE_URL = "https://newsapi.org/v2/";
    private List<Article> newsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.feature_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchNews();
    }

    private void fetchNews() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService =  retrofit.create(NewsApiService.class);
        Call<NewsResponse> call = apiService.getDisasterNews(
                "flood earthquake landslide",
                API_KEY, "publishedAt", "en"
        );
        Log.d("API Request", "Request URL: " + call.request().url().toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.d("API Request", "Response received. Code: " + response.code());

                if (!response.isSuccessful()) {
                    Log.e("API Error", "Response Code: " + response.code() + " - " + response.message());
                    try {
                        Log.e("API Error", "Error Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Home_Activity.this, "Failed to get news!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    adapter = new NewsAdapter(Home_Activity.this, articles);
                    recyclerView.setAdapter(adapter);

                    if (articles.isEmpty()) {
                        Toast.makeText(Home_Activity.this, "No news found!", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Article article : articles) {
                            Log.d("News", "Title: " + article.getTitle());
                            Log.d("News", "Description: " + article.getDescription());
                            Log.d("News", "URL: " + article.getUrl());
                        }
                        Toast.makeText(Home_Activity.this, "News fetched!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NewsError", "Failed: " + t.getMessage());
                Toast.makeText(Home_Activity.this, "Error fetching news", Toast.LENGTH_SHORT).show();
            }
        });
    }
}