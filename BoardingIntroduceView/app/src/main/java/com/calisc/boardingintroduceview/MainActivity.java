package com.calisc.boardingintroduceview;

import android.graphics.Color;
import android.os.Bundle;

import com.calisc.boardingintroduceview.Example.BlankFragment;
import com.calisc.boardingintroduceview.Fragment.BoardFragment;
import com.calisc.boardingintroduceview.Models.BoardModel;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnRightOutListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        BoardFragment fragment = BoardFragment.newInstance(exampleData());
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        fragment.setmOnRightOutListener(new PaperBoardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment bg = new BlankFragment();
                fragmentTransaction.replace(R.id.fragment_container, bg);
                fragmentTransaction.commit();
            }
        });

    }

    private ArrayList<BoardModel> exampleData() {
        ArrayList<BoardModel> data = new ArrayList<>();

        BoardModel item1 = new BoardModel("Hotels","desc1", Color.parseColor("#678FB4"), R.drawable.hotels, R.drawable.key);

        BoardModel item2 = new BoardModel("Hotels","desc1", Color.parseColor("#678FB4"), R.drawable.hotels, R.drawable.key);

        BoardModel item3 = new BoardModel("Hotels","desc1", Color.parseColor("#678FB4"), R.drawable.hotels, R.drawable.key);

        data.add(item1);
        data.add(item2);
        data.add(item3);


        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
