package com.example.contractorsapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.contractorsapp.R;
import com.example.contractorsapp.data.model.Contragent;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTRAGENT = "com.example.contractorsapp.EXTRA_CONTRAGENT";

    private DetailViewModel detailViewModel;
    private Contragent currentContragent;

    private TextInputEditText editTextName;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextBulstat;
    private TextInputEditText editTextEmail;
    private MaterialCheckBox checkBoxFavorite;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Детайли за контрагент");
        }

        // Инициализиране на ViewModel
        detailViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DetailViewModel.class);

        // Намиране на изгледите
        editTextName = findViewById(R.id.edit_text_name);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextBulstat = findViewById(R.id.edit_text_bulstat);
        editTextEmail = findViewById(R.id.edit_text_email);
        checkBoxFavorite = findViewById(R.id.checkbox_favorite);
        buttonSave = findViewById(R.id.button_save);

        // Взимане на данните от Intent
        currentContragent = (Contragent) getIntent().getSerializableExtra(EXTRA_CONTRAGENT);

        if (currentContragent != null) {
            populateUI(currentContragent);
        }

        buttonSave.setOnClickListener(v -> saveContragent());

        // Когато се промени статуса на "Любим", го запазваме само локално
        checkBoxFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(currentContragent != null){
                currentContragent.setFavorite(isChecked);
                detailViewModel.updateLocal(currentContragent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUI(Contragent contragent) {
        setTitle(contragent.getName());
        editTextName.setText(contragent.getName());
        editTextPhone.setText(contragent.getPhone());
        editTextAddress.setText(contragent.getAddress());
        editTextBulstat.setText(contragent.getBulstat());
        editTextEmail.setText(contragent.getEmail());
        checkBoxFavorite.setChecked(contragent.isFavorite());
    }

    private void saveContragent() {
        // Събираме данните от полетата
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String address = editTextAddress.getText().toString();
        String bulstat = editTextBulstat.getText().toString();
        String email = editTextEmail.getText().toString();
        boolean isFavorite = checkBoxFavorite.isChecked();

        // Валидация (проста)
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Името е задължително", Toast.LENGTH_SHORT).show();
            return;
        }

        // Обновяваме обекта
        currentContragent.setName(name);
        currentContragent.setPhone(phone);
        currentContragent.setAddress(address);
        currentContragent.setBulstat(bulstat);
        currentContragent.setEmail(email);
        currentContragent.setFavorite(isFavorite);

        // Изпращаме към ViewModel за запис
        detailViewModel.update(currentContragent);

        Toast.makeText(this, "Промените са запазени", Toast.LENGTH_SHORT).show();
        finish(); // Затваряме екрана и се връщаме към списъка
    }
}