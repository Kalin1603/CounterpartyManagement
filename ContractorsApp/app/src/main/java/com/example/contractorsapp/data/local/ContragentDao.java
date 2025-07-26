package com.example.contractorsapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.contractorsapp.data.model.Contragent;
import java.util.List;

@Dao
public interface ContragentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Contragent> contragents);

    @Update
    void update(Contragent contragent);

    @Query("SELECT * FROM contragent_table")
    LiveData<List<Contragent>> getAllContragents();

    @Query("SELECT * FROM contragent_table WHERE isFavorite = 1")
    LiveData<List<Contragent>> getFavoriteContragents();

    @Query("SELECT * FROM contragent_table WHERE id = :id")
    LiveData<Contragent> getContragentById(int id);

    @Query("SELECT * FROM contragent_table WHERE id = :id")
    Contragent getContragentByIdSync(int id);
}