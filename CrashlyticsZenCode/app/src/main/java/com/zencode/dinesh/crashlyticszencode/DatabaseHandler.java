package com.zencode.dinesh.crashlyticszencode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dinesh on 6/28/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ZenCodeCrashlytics.db";
    Context mContext;
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("parm","gggg");
        db.execSQL(GetCrashlyticsIssueListRepository.createCrashlyticsMaster());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("parm","upgrade");
    }
}

