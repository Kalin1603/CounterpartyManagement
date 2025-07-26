package com.example.contractorsapp.ui;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.contractorsapp.data.ContragentRepository;
import com.example.contractorsapp.data.model.Contragent;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final ContragentRepository repository;
    private final LiveData<List<Contragent>> allContragents;
    private final LiveData<List<Contragent>> favoriteContragents;

    // LiveData, което ще се показва на екрана. То ще е резултат или от търсене, или от мрежа/база данни
    private final MediatorLiveData<List<Contragent>> displayedContragents = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isOffline = new MutableLiveData<>(false);


    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new ContragentRepository(application);
        allContragents = repository.getAllContragents();
        favoriteContragents = repository.getFavoriteContragents();

        // Това е сложната част. Тук решаваме какво да покажем.
        // 1. Следим дали сме офлайн
        displayedContragents.addSource(isOffline, offline -> {
            // Ако режимът се смени, преизчисляваме какво да се покаже
            recalculateDisplayedContragents();
        });

        // 2. Следим за промени в списъка от базата данни
        displayedContragents.addSource(allContragents, contragents -> {
            if (!isOffline.getValue()) {
                recalculateDisplayedContragents();
            }
        });

        // 3. Следим за промени в списъка с любими
        displayedContragents.addSource(favoriteContragents, favorites -> {
            if (isOffline.getValue()) {
                recalculateDisplayedContragents();
            }
        });

        // 4. Следим за промени в търсенето
        displayedContragents.addSource(searchQuery, query -> {
            recalculateDisplayedContragents();
        });
    }

    private void recalculateDisplayedContragents() {
        List<Contragent> sourceList = isOffline.getValue() ? favoriteContragents.getValue() : allContragents.getValue();
        String query = searchQuery.getValue();

        if (sourceList == null) {
            displayedContragents.setValue(new ArrayList<>());
            return;
        }

        if (TextUtils.isEmpty(query)) {
            displayedContragents.setValue(sourceList);
        } else {
            List<Contragent> filteredList = new ArrayList<>();
            for (Contragent contragent : sourceList) {
                if (contragent.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(contragent);
                }
            }
            displayedContragents.setValue(filteredList);
        }
    }


    public LiveData<List<Contragent>> getDisplayedContragents() {
        return displayedContragents;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setOfflineMode(boolean offline) {
        isOffline.setValue(offline);
        // При липса на интернет, опитай да заредиш от нета. Ако не успее, репозиторито ще върне само от базата.
        // Ако има интернет, зареди наново.
        if (!offline) {
            repository.refreshContragents();
        }
    }
}