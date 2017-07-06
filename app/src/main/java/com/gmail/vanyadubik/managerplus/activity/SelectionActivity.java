package com.gmail.vanyadubik.managerplus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.SelectionListAdapter;
import com.gmail.vanyadubik.managerplus.model.documents.SelectionItem;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {

    public static final String SELECTION_LIST_PARAM = "selection_list_param";
    public static final String SELECTION_LIST_NAME = "selection_list_name";
    public static final String SELECTION_ID_SELECTED_ITEM = "id_selected_item";

    private List<SelectionItem> list;
    private SelectionItem selectionItem;
    private SelectionListAdapter adapter;
    private ListView listView;
    private TextView capTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);


        listView = (ListView) findViewById(R.id.selection_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAskAlert((SelectionItem) adapter.getItem(position));

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showAskAlert((SelectionItem) adapter.getItem(position));
                return true;
            }
        });

        final EditText searchEditText = (EditText) findViewById(R.id.search_textView);
        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = searchEditText.getRight()
                            - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        searchEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        FloatingActionButton closeView = (FloatingActionButton) findViewById(R.id.selection_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        capTextView = (TextView) findViewById(R.id.selection_cap);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        String textCap;
        if (extras != null) {
            list = (ArrayList<SelectionItem>) getIntent().getSerializableExtra(SELECTION_LIST_PARAM);
            textCap = extras.getString(SELECTION_LIST_NAME);
        }else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.selection_list_not_found), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if(!(textCap==null || textCap.isEmpty())){

            capTextView.setText(textCap);

        }


        adapter = new SelectionListAdapter(this, list);
        listView.setAdapter(adapter);

    }

    public void showAskAlert(final SelectionItem selectionItem){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selection));
        builder.setMessage(getString(R.string.selected_element));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(SELECTION_ID_SELECTED_ITEM, selectionItem.getExternalId());
                setResult(RESULT_OK, intent);
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.selected)
                                + ": " + selectionItem.getPresentation(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        // TODO: (end stub) ------------------

    }

}