package com.example.rapidresponse;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import Adapter.NewsAdapter;
import DataModel.Article;
import DataModel.NewsResponse;
import api.NewsApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    NewsAdapter adapter;
    String API_KEY = "2811496b0ab9407680544d97e570dde3";
    String BASE_URL = "https://newsapi.org/v2/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.feature_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchNews();// Inflate the layout for this fragment
        return view;
    }

    private void fetchNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);
        Call<NewsResponse> call = apiService.getDisasterNews(
                "flood earthquake landslide",
                API_KEY, "publishedAt", "en"
        );
        Log.d("API Request", "Request URL: " + call.request().url());
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e("API Error", "Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to get news!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    adapter = new NewsAdapter(getContext(), articles);
                    recyclerView.setAdapter(adapter);

                    if (articles.isEmpty()) {
                        Toast.makeText(getContext(), "No news found!", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Article article : articles) {
                            Log.d("News", "Title: " + article.getTitle());
                        }
                        Toast.makeText(getContext(), "News fetched!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NewsError", "Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Error fetching news", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
