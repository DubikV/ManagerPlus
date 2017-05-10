package com.gmail.vanyadubik.managerplus.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;

import java.util.List;

public class ActivityUtils {

    public ActivityUtils() {
    }

    public void showMessage(String textMessage, Context context) {
        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.questions_title_info));
        builder.setMessage(textMessage);

        builder.setNeutralButton(context.getString(R.string.questions_answer_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));
        Button button3 = (Button) alert.findViewById(android.R.id.button3);
        button3.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));
        // TODO: (end stub) ------------------
    }

    public void setVisiblyElements(List<View> views, Boolean  visible){
        int visibileEl = View.VISIBLE;
        if(visible){
            visibileEl = View.VISIBLE;
        }else{
            visibileEl = View.GONE;
        }
        for(View view : views){
           view.setVisibility(visibileEl);
        }
    }

    public void setVisiblyElement(View view, Boolean  visible){
        int visibileEl = View.VISIBLE;
        if(visible){
            visibileEl = View.VISIBLE;
        }else{
            visibileEl = View.GONE;
        }

        view.setVisibility(visibileEl);
    }

}
