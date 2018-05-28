package com.example.bumb.miser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ShopListFragment.NotifyAdapterInterface, BoughtListFragment.NotifyAdapterInterface {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    public Database db;

    public static List<Item> itemShopList;
    public static List<Item> itemBoughtList;

    static {
        itemShopList = new ArrayList<>();
        itemBoughtList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new Database(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create new activity for Add Item
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        notifyAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Item item;
        if(requestCode == 1 && resultCode == 1) {
            item = new Item(
                        data.getExtras().getString("name"),
                        data.getExtras().getString("image"),
                        data.getExtras().getDouble("price"),
                        data.getExtras().getString("url"),
                        data.getExtras().getBoolean("alarm"),
                        data.getExtras().getInt("percentage")
            );

            try {
                Database db = new Database(getApplicationContext());

                long id = db.addItem(item);

                if (id == -1) {
                    Toast.makeText(MainActivity.this, "There is an error!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                }

                notifyAdapter();
            } catch (Exception e){
                Toast.makeText(MainActivity.this, "There is an error!", Toast.LENGTH_SHORT).show();
            }

            SectionsPageAdapter spa = (SectionsPageAdapter) mViewPager.getAdapter();
            for (int i = 0; i < spa.getCount(); i++) {
                Fragment f = spa.getItem(i);

                if (f instanceof ShopListFragment) {
                    ((ShopListFragment) f).notifyAdapter();
                } else if (f instanceof BoughtListFragment) {
                    ((BoughtListFragment) f).notifyAdapter();
                }
            }

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShopListFragment(), "ALL ITEMS");
        adapter.addFragment(new BoughtListFragment(), "FAVORITE ITEMS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void notifyAdapter() {
        itemShopList.clear();
        itemShopList.addAll(db.getAllItems());

        itemBoughtList.clear();
        for (Item item : itemShopList) {
            if (item.isFavorite()) {
                itemBoughtList.add(item);
            }
        }

        SectionsPageAdapter spa = (SectionsPageAdapter) mViewPager.getAdapter();
        for (int i = 0; i < spa.getCount(); i++) {
            Fragment f = spa.getItem(i);

            if (f instanceof ShopListFragment) {
                ((ShopListFragment) f).notifyAdapter();
            } else if (f instanceof BoughtListFragment) {
                ((BoughtListFragment) f).notifyAdapter();
            }
        }
    }

    public void removeElementAt(int i) {
        db.deleteItem(itemShopList.get(i).getItemName());
        notifyAdapter();
    }
}
