package com.example.artisansfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class Tutorial {

    private boolean isViewedOncePage;
    private boolean isFirstRun;
    private SharedPreferences sharedPreferences;
    private ArrayList<View> views;
    private Context context;
    private GuideView guideView;

    public Tutorial() {
    }

    public Tutorial(Context context) {
        this.isViewedOncePage = false;
        this.context = context;
    }

    public Tutorial(Context context,ArrayList<View> views) {
        this.views = views;
        this.context = context;
    }

    public void checkIfFirstRun(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isFirstRun = sharedPreferences.getBoolean("FirstRun",true);
    }

    public void requestFocusForView(View view, String title, String content) {

        if (!isViewedOncePage/* && isFirstRun*/) {

            GuideView.Builder builder = new GuideView.Builder(context)
                    .setTitle(title)
                    .setContentText(content)
                    .setGravity(Gravity.auto)
                    .setDismissType(DismissType.anywhere)
                    .setTargetView(view)
                    .setContentTextSize(12)
                    .setTitleTextSize(14);

            guideView = builder.build();
            guideView.show();
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("FirstRun", false);
//            editor.apply();

        }
    }

    public void requestFocusForViews(final HashMap<View, String> title, final HashMap<View, String> content){

        final GuideView.Builder builder;
        final int i = 0;

        if (!isViewedOncePage/* && isFirstRun*/) {

            if(content.get(views.get(i))!=null){

                builder = new GuideView.Builder(context)
                        .setTitle(title.get(views.get(i)))
                        .setContentText(content.get(views.get(i)))
                        .setTargetView(views.get(i))
                        .setGravity(Gravity.auto)
                        .setDismissType(DismissType.anywhere)
                        .setContentTextSize(14)
                        .setTitleTextSize(18);

            } else {

                builder = new GuideView.Builder(context)
                        .setTitle(title.get(views.get(i)))
                        .setTargetView(views.get(i))
                        .setGravity(Gravity.auto)
                        .setDismissType(DismissType.anywhere)
                        .setTitleTextSize(18);

            }

            builder.setGuideListener(new GuideListener() {
                @Override
                public void onDismiss(View view) {
                    int j = views.indexOf(view);
                    if(j == views.size()-1){
                        return;
                    }
                    builder.setTargetView(views.get(j+1))
                            .setTitle(title.get(views.get(j+1)))
                            .setTitleTextSize(18);
                    if(content.get(views.get(j+1))!=null)
                        builder.setContentText(content.get(views.get(j+1)))
                                .setContentTextSize(14);
                    guideView = builder.build();
                    guideView.show();
                }
            });

            guideView = builder.build();
            guideView.show();

//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("FirstRun", false);
//            editor.apply();
//
        }
    }


    public void finishedTutorial() {
        isViewedOncePage = true;
    }

}
