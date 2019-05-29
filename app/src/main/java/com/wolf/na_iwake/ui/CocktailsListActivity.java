package com.wolf.na_iwake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.wolf.na_iwake.Constants;
import com.wolf.na_iwake.adapters.CocktailListAdapter;
import com.wolf.na_iwake.R;
import com.wolf.na_iwake.models.Cocktail;
import com.wolf.na_iwake.services.CocktailService;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CocktailsListActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    /* private String mRecentCocktails;*/
    private SharedPreferences.Editor mEditor;
    private static final String TAG = CocktailsListActivity.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    /*  @BindView(R.id.cocktailsListView)
      ListView mCocktailsListView;*/
    private CocktailListAdapter mAdapter;
    /*@BindView(R.id.findCocktailsButton)
    Button mCocktailsButton;*/

    public ArrayList<Cocktail> cocktails = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktails);
        ButterKnife.bind(this);
        /*mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRecentCocktails = mSharedPreferences.getString(Constants.PREFERENCES_DRINK_KEY, null);
        if (mRecentCocktails != null ) {
            getCocktails(mRecentCocktails);
        }*/

        Intent intent = getIntent();
        String name = intent.getStringExtra("Cocktail");

        /*CocktailsArrayAdapter adapter  = new CocktailsArrayAdapter(this, android.R.layout.simple_list_item_1, cocktails);
        mCocktailsListView.setAdapter(adapter);*/

        getCocktails(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addToSharedPreferences(query);
                getCocktails(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getCocktails(String name) {
        final CocktailService cocktailService = new CocktailService();
        cocktailService.findCocktails(name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                cocktails = cocktailService.processResults(response);

                CocktailsListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new CocktailListAdapter(getApplicationContext(), cocktails);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CocktailsListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);

                        String[] cocktailNames = new String[cocktails.size()];
                        for (int i = 0; i < cocktailNames.length; i++) {
                            cocktailNames[i] = cocktails.get(i).getDrink();
                        }
                       /* ArrayAdapter adapter = new ArrayAdapter(CocktailsListActivity.this, android.R.layout.simple_list_item_1, cocktailNames);
                        mCocktailsListView.setAdapter(adapter);*/
                        for (Cocktail cocktail : cocktails) {
                            Log.d(TAG, "Drink" + cocktail.getDrink());
                            Log.d(TAG, "Image" + cocktail.getDrinkThumb());
                        }
                    }
                });
            }
        });
    }
    public void addToSharedPreferences (String name) {
        mEditor.putString(Constants.PREFERENCES_DRINK_KEY, name).apply();
    }
}



