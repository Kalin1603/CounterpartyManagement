package com.example.contractorsapp.data.remote;

import com.example.contractorsapp.data.model.Contragent;
import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Заявка за взимане на всички контрагенти
    @POST("GetContragents")
    Call<List<Contragent>> getContragents(@Body JsonObject emptyBody);

    // Заявка за обновяване на контрагент.
    // Сървисът очаква обект, който съдържа друг обект "contragent"
    class UpdateWrapper {
        Contragent contragent;
        public UpdateWrapper(Contragent contragent) {
            this.contragent = contragent;
        }
    }
    @POST("UpdateContragents")
    Call<Void> updateContragent(@Body UpdateWrapper contragentWrapper);
}