package com.nnkwachi.firebase_test;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.nnkwachi.firebase_test.fragments.PostFragment;


public class HomeActivity extends AppCompatActivity{

    private DrawerLayout drawer;
    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setUpNavigation();

    }

    private void setUpNavigation(){
       View view = findViewById(R.id.main_content);
        bottomNavigationView = view.findViewById(R.id.bottom_nav);
        NavHostFragment navHostFragment =    (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_2);
        assert navHostFragment != null;
        NavController controller = Navigation.findNavController(HomeActivity.this,R.id.nav_host_fragment_2);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);

        controller.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
             switch (destination.getId()){
                 case R.id.postFragment:
                     hideBottomNav();
                     break;
                 case R.id.homeFragment:
                     showBottomNav();
                     break;
             }
            }
        });
    }

    private void showBottomNav() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }



    public void hideBottomNav() {
        bottomNavigationView.setVisibility(View.GONE);
    }
}
