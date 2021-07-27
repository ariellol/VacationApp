package com.ariellip.vacationApp;

import android.Manifest;
import android.content.Context;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;



public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView navigationView;
    Toolbar toolbar;
    Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState==null) {
            if (BuildConfig.DEBUG) {

                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                toolbar.setTitle(getResources().getString(R.string.home));
            }
        }

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnItemSelectedListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        item.setChecked(true);
        switch (item.getItemId()){
            case R.id.personalArea:
                if(currentFragment instanceof personalAreaFragment)
                    break;
                popPreviousFragments();
                transaction.add(R.id.fragment_container, new personalAreaFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                removeItemsUnderline(navigationView);
                underlineMenuItem(item);
                toolbar.setTitle(getResources().getString(R.string.personal_area));
                break;
            case R.id.home:
                if (currentFragment instanceof HomeFragment){
                    break;
                }
                popPreviousFragments();
                transaction.add(R.id.fragment_container, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                removeItemsUnderline(navigationView);
                underlineMenuItem(item);
                toolbar.setTitle(getResources().getString(R.string.home));
                break;
            case R.id.cart:
                if (currentFragment instanceof CartFragment){
                    break;
                }
                popPreviousFragments();
                transaction.add(R.id.fragment_container,new CartFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                removeItemsUnderline(navigationView);
                underlineMenuItem(item);
                toolbar.setTitle(getResources().getString(R.string.cart));
                break;
        }
        return true;
    }


    public void popPreviousFragments(){
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (getSupportFragmentManager().getBackStackEntryCount() <=1){
            if (currentFragment instanceof HomeFragment){
                finish();
            }
            else{
                popPreviousFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                toolbar.setTitle(getResources().getString(R.string.home));
            }
        }
        else
            if (currentFragment instanceof ManagerFragment || currentFragment instanceof VacationPackageFragment){
                popPreviousFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                toolbar.setTitle(getResources().getString(R.string.home));

            }
            else if(currentFragment instanceof UserListWatchFragment || currentFragment instanceof VacationListManager ||
                    currentFragment instanceof ExtrasListManager){
                popPreviousFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ManagerFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                toolbar.setTitle("דף מנהלים");
            }
            else if(currentFragment instanceof AddVacationFragment || currentFragment instanceof EditVacationFragment){
                popPreviousFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new VacationListManager());
                transaction.addToBackStack(null);
                transaction.commit();
                toolbar.setTitle("צפייה ועריכת חבילות");
            }
            else if(currentFragment instanceof ExtrasFragment){
                popPreviousFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new CartFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                toolbar.setTitle("עגלה");
            }

    }

    private void removeItemsUnderline(BottomNavigationView bottomNavigationView) {
        for (int i = 0; i <  bottomNavigationView.getMenu().size(); i++) {
            MenuItem item = bottomNavigationView.getMenu().getItem(i);
            item.setTitle(item.getTitle().toString());
        }
    }

    private void underlineMenuItem(MenuItem item) {
        SpannableString content = new SpannableString(item.getTitle());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        item.setTitle(content);

    }
}