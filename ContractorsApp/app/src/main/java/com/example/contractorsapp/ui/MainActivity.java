package com.example.contractorsapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contractorsapp.R;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private ContragentAdapter adapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        textViewEmpty = findViewById(R.id.text_view_empty);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_contragents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        adapter = new ContragentAdapter();
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Показваме прогрес бар в началото
        progressBar.setVisibility(View.VISIBLE);

        mainViewModel.getDisplayedContragents().observe(this, contragents -> {
            progressBar.setVisibility(View.GONE);
            adapter.setContragents(contragents);
            if (contragents == null || contragents.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
            }
        });

        // Setup Search
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.setSearchQuery(newText);
                return true;
            }
        });

        // Handle item clicks
        adapter.setOnItemClickListener(contragent -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_CONTRAGENT, contragent);
            startActivity(intent);
        });

        // Проверка за интернет при стартиране
        checkNetworkAndSetMode();
    }

    private void checkNetworkAndSetMode() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        mainViewModel.setOfflineMode(!isConnected);

        if (!isConnected) {
            textViewEmpty.setText("Няма интернет връзка. Показват се само любими контрагенти.");
        } else {
            textViewEmpty.setText("Няма намерени контрагенти.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Проверяваме дали натиснатият бутон е нашият "action_info"
        if (item.getItemId() == R.id.action_info) {
            // Показваме информативно съобщение
            Toast.makeText(this, "Приложение за управление на контрагенти, v1.0", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}