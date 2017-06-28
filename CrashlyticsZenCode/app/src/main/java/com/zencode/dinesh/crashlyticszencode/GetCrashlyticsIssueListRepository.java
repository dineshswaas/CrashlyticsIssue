package com.zencode.dinesh.crashlyticszencode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dinesh on 6/28/2017.
 */

class GetCrashlyticsIssueListRepository {
    public static final String TABLE_CRASHLYTICS_ISSUE = "mst_Crashlytics_Issue";
    public static final String _Id = "_id";
    public static final String TITLE = "Title";
    public static final String USERNAME = "User_Name";
    public static final String DESCRIPATION = "Descripation";
    public static final String UPDATEDATE = "UpdateDate";
    private DatabaseHandler dbHandler = null;
    private SQLiteDatabase database = null;

    Context context;
    private ProgressDialog pDialog;
    private GetIssueListFromUrl getIssueListFromUrl;
    String  issueApiStr;
    boolean isFromDetails;

    public GetCrashlyticsIssueListRepository(Context context,Boolean isFrom) {
        dbHandler = new DatabaseHandler(context);
        this.context = context;
        isFromDetails = isFrom;
    }


    public void mAPICallForIssueList(String url){
        issueApiStr= url;
        new CrashlyticsIssue().execute();
    }


    public interface GetIssueListFromUrl {
        void mIssueSuccessCB(List<Issues> issuesList);

        void mIssueFailureCB(String message);
    }

    public void setIssueListFromUrl(GetIssueListFromUrl getIssueListFromUrl){
        this.getIssueListFromUrl = getIssueListFromUrl;
    }


    private class CrashlyticsIssue extends AsyncTask<Void,Void,Void> {
        List<Issues> issuesList = new ArrayList<>();
        Issues issuesObject;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler httpHandler = new HttpHandler();
            String json = httpHandler.makeServiceCall(issueApiStr);
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i=0;i<jsonArray.length();i++){
                    issuesObject = new Issues();
                    JSONObject e = jsonArray.getJSONObject(i);
                    if(!isFromDetails){
                        issuesObject.setId(e.getInt("id"));
                        issuesObject.setTitle(e.getString("title"));
                        issuesObject.setBody(e.getString("body"));
                        issuesObject.setComments_url(e.getString("comments_url"));
                        JSONObject userJsonObject = e.getJSONObject("user");
                        issuesObject.setAvatar_url(userJsonObject.getString("avatar_url"));
                        issuesObject.setLogin(userJsonObject.getString("login"));
                        issuesObject.setUpdated_at(e.getString("updated_at"));
                    }else{
                        issuesObject.setId(e.getInt("id"));
                        issuesObject.setBody(e.getString("body"));
                        JSONObject userJsonObject = e.getJSONObject("user");
                        issuesObject.setAvatar_url(userJsonObject.getString("avatar_url"));
                        issuesObject.setLogin(userJsonObject.getString("login"));
                        issuesObject.setUpdated_at(e.getString("updated_at"));
                    }
                    issuesList.add(issuesObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                getIssueListFromUrl.mIssueFailureCB(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
            if(issuesList != null){
                getIssueListFromUrl.mIssueSuccessCB(issuesList);
            }

        }
    }




    public static String createCrashlyticsMaster() {
        String CREATE_DCR_MASTER = "CREATE TABLE IF NOT EXISTS " + TABLE_CRASHLYTICS_ISSUE + "(" + _Id + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT," + USERNAME + " TEXT,"
                + DESCRIPATION + " TEXT," + UPDATEDATE + " TEXT)";
        return CREATE_DCR_MASTER;
    }

    public void crashlyticsBulkInsert(List<Issues> issuesList) {
        try {
            DBConnectionOpen();
            database.delete(TABLE_CRASHLYTICS_ISSUE, null, null);
            ContentValues contentValues = new ContentValues();
            int count = 0;
            for (Issues issues : issuesList) {
                contentValues.clear();
                contentValues.put(_Id, issues.getId());
                contentValues.put(TITLE, issues.getTitle());
                contentValues.put(USERNAME, issues.getLogin());
                contentValues.put(DESCRIPATION, issues.getBody());
                contentValues.put(UPDATEDATE, issues.getUpdated_at());
               long rowCount = database.insert(TABLE_CRASHLYTICS_ISSUE, null, contentValues);
            }
        } catch (Exception e) {

        } finally {
            DBConnectionClose();
        }
    }


    public void getCrashlyticsFromLocalDB() {
        try {
            Cursor cursor = null;
            List<Issues> issuesList = new ArrayList<>();
            DBConnectionOpen();
            cursor = database.rawQuery("select Title,User_Name,Descripation,UpdateDate from mst_Crashlytics_Issue", null);
            issuesList = getIssueFromCursor(cursor);
            getIssueListFromUrl.mIssueSuccessCB(issuesList);
        } catch (Exception e) {
            getIssueListFromUrl.mIssueFailureCB(e.getMessage());
        } finally {
            DBConnectionClose();
        }
    }

    private List<Issues> getIssueFromCursor(Cursor cursor) {
        List<Issues> issuesList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int title = cursor.getColumnIndex(TITLE);
            int userName = cursor.getColumnIndex(USERNAME);
            int dec = cursor.getColumnIndex(DESCRIPATION);
            int updateTime = cursor.getColumnIndex(UPDATEDATE);
            do {
                Issues issues = new Issues();
                issues.setTitle(cursor.getString(title));
                issues.setLogin(cursor.getString(userName));
                issues.setBody(cursor.getString(dec));
                issues.setUpdated_at(cursor.getString(updateTime));
                issuesList.add(issues);
            } while (cursor.moveToNext());
        }
        return issuesList;
    }


    public void DBConnectionOpen() {
        database = dbHandler.getWritableDatabase();
    }

    public void DBConnectionClose() {
        if (database.isOpen()) {
            database.close();
        }
    }

}
