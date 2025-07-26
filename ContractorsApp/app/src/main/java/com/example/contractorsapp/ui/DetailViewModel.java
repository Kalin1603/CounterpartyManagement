package com.example.contractorsapp.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.contractorsapp.data.ContragentRepository;
import com.example.contractorsapp.data.model.Contragent;

public class DetailViewModel extends AndroidViewModel {
    private ContragentRepository repository;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new ContragentRepository(application);
    }

    // Този метод изпраща заявка към сървъра и после обновява локалната база
    public void update(Contragent contragent) {
        repository.updateContragent(contragent);
    }

    // Този метод обновява само локалната база (за isFavorite)
    public void updateLocal(Contragent contragent) {
        repository.updateContragentLocal(contragent);
    }
}