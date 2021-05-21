package dev.timwalsh.angleassault;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends Activity {
    public static final int HIGHSCORE_REQUEST = 0;
    ListView scoreListView, nameListView;
    DatabaseHelper database;
    List<Integer> scoreItems = new ArrayList<>();
    List<String> nameItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        scoreListView = findViewById (R.id.scores_list);
        nameListView = findViewById (R.id.names_list);
        loadData();
        buildHighScoreView();
    }

    @SuppressWarnings("rawtypes")
    private void buildHighScoreView() {
        // Creating adapters for list
        ArrayAdapter nameAdaptor = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView viewText = view.findViewById(android.R.id.text1);
                viewText.setTextColor(this.getContext().getColor(R.color.colorTextLight));
                return view;
            }
        };
        // Different formatting for score
        ArrayAdapter scoreAdaptor = new ArrayAdapter(this, android.R.layout.simple_list_item_1, scoreItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView viewText = view.findViewById(android.R.id.text1);
                viewText.setTextColor(this.getContext().getColor(R.color.colorTextLight));
                viewText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                return view;
            }
        };
        nameListView.setAdapter(nameAdaptor);
        scoreListView.setAdapter(scoreAdaptor);
    }

    public void loadData() {
        database = new DatabaseHelper(this);
        int highScoreCount = 10;
        List<HighScoreItem> scores = database.getHighScoresDesc();

        int i = 0;
        // Build a pair of parallel lists containing the top #highScoreCount#
        for (HighScoreItem score : scores) {
            // Don't load null/empty names, ensure we get up to 10 real top scores
            if (score.getName() != null && !score.getName().isEmpty()) {
                if (i < highScoreCount) {
                    i++;
                    nameItems.add(score.getName());
                    scoreItems.add(score.getScore());
                } else {
                    break;
                }
            }
        }
    }

    public void exitGameButton(View v) {
        finish();
    }
}