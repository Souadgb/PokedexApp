package com.example.pokedexapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pokedexapp.ui.dashboard.FavoritesFragment;
import com.example.pokedexapp.ui.home.ListFragment;
import com.example.pokedexapp.ui.notifications.DetailFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final Fragment listFragment = new ListFragment();
    private final Fragment favoritesFragment = new FavoritesFragment();
    private final Fragment detailFragment = new DetailFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, listFragment)
                .commit();

        navView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) selected = listFragment;
            else if (id == R.id.navigation_dashboard) selected = favoritesFragment;
            else if (id == R.id.navigation_notifications) selected = detailFragment;

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, selected)
                        .commit();
            }
            return true;
        });
    }
}
