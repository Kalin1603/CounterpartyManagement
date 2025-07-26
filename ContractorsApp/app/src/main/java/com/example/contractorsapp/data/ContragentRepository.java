package com.example.contractorsapp.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.contractorsapp.data.local.AppDatabase;
import com.example.contractorsapp.data.local.ContragentDao;
import com.example.contractorsapp.data.model.Contragent;
import com.example.contractorsapp.data.remote.ApiService;
import com.example.contractorsapp.data.remote.RetrofitClient;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContragentRepository {

    private final ContragentDao contragentDao;
    private final ApiService apiService;
    private final ExecutorService executorService;

    public ContragentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.contragentDao = db.contragentDao();
        this.apiService = RetrofitClient.getApiService();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Contragent>> getAllContragents() {
        refreshContragents(); // Опитай да заредиш от нета
        return contragentDao.getAllContragents(); // Винаги връщай данните от базата
    }

    public LiveData<List<Contragent>> getFavoriteContragents() {
        return contragentDao.getFavoriteContragents();
    }

    public LiveData<Contragent> getContragentById(int id) {
        return contragentDao.getContragentById(id);
    }

    // Взимане на данни от API
    public void refreshContragents() {
        apiService.getContragents(new JsonObject()).enqueue(new Callback<List<Contragent>>() {
            @Override
            public void onResponse(Call<List<Contragent>> call, Response<List<Contragent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        contragentDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Contragent>> call, Throwable t) {
                // При грешка не правим нищо, приложението ще покаже кешираните данни
                Log.e("Repository", "Failed to fetch from network", t);
            }
        });
    }

    // Обновяване на данните (локално и на сървъра)
    public void updateContragent(Contragent contragent) {
        // 1. Изпрати към API-то
        apiService.updateContragent(new ApiService.UpdateWrapper(contragent)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 2. Ако е успешно, обнови и в локалната база данни
                    executorService.execute(() -> contragentDao.update(contragent));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Repository", "Failed to update on network", t);
            }
        });
    }

    // Само за локално обновяване (напр. за isFavorite)
    public void updateContragentLocal(Contragent contragent) {
        executorService.execute(() -> contragentDao.update(contragent));
    }
}