package com.example.eisen.sesam;
//TestKommentar für Commit
//TestKommentar für Commit 3
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

public class MainActivity extends AppCompatActivity {

    ExpandableListView expListView;
    ExpandableListAdapter listAdapterGeneralSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        listAdapterGeneralSettings = new ExpandableListAdapter(this);

        expListView.setAdapter(listAdapterGeneralSettings);
    }

}
