package com.zencode.dinesh.crashlyticszencode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssueDetailsActivity extends AppCompatActivity {

    TextView title;
    TextView name,dec,updateDate;
    ImageView avatar;
    RecyclerView commentRecyclerView;
    Issues issuesObject;
    List<Issues> commentsList;
    GetCrashlyticsIssueListRepository issueListRepository;
    IssueRecyclerListAdapter issueRecyclerListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);
        intializeViews();
        getIntentValues();
        bindValues();
    }

    private void bindValues() {
        if(issuesObject != null){
            title.setText(issuesObject.getTitle());
            name.setText(issuesObject.getLogin());
            dec.setText(Html.fromHtml(issuesObject.getBody()).toString().trim());
            updateDate.setText(dateConvert(issuesObject.getUpdated_at()));
            if(issuesObject.getAvatar_url() != null){
                if(NetworkUtils.checkIfNetworkAvailable(this)) {
                    Ion.with(this).load(issuesObject.getAvatar_url()).intoImageView(avatar);
                }else{
                    avatar.setImageResource(R.mipmap.ic_launcher_round);
                }
            }

            issueListRepository.setIssueListFromUrl(new GetCrashlyticsIssueListRepository.GetIssueListFromUrl() {
                @Override
                public void mIssueSuccessCB(List<Issues> issuesListItm) {
                    if(issuesListItm != null &&issuesListItm.size() > 0){
                        commentsList = new ArrayList<Issues>(issuesListItm);
                        onBindRecyclerViewList();
                    }
                }

                @Override
                public void mIssueFailureCB(String message) {

                }
            });
            if(NetworkUtils.isNetworkAvailable(this)){
                issueListRepository.mAPICallForIssueList(issuesObject.getComments_url());
            }

        }

    }

    private void getIntentValues() {
        if(getIntent().getSerializableExtra("IntentObject") != null){
            issuesObject = (Issues) getIntent().getSerializableExtra("IntentObject");
        }
    }

    private void intializeViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        title = (TextView)findViewById(R.id.TitleTv);
        name = (TextView)findViewById(R.id.NameTV);
        dec = (TextView)findViewById(R.id.DecDetails);
        updateDate = (TextView)findViewById(R.id.DateTV);
        avatar = (ImageView)findViewById(R.id.avatar);
        commentRecyclerView = (RecyclerView)findViewById(R.id.commentRecycler);
        issueListRepository = new GetCrashlyticsIssueListRepository(this,true);
        commentsList = new ArrayList<>();

    }



    private void onBindRecyclerViewList() {
        if(commentsList != null && commentsList.size() > 0){
            commentRecyclerView.setHasFixedSize(true);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            issueRecyclerListAdapter = new IssueRecyclerListAdapter(commentsList,this,true);
            commentRecyclerView.setAdapter(issueRecyclerListAdapter);
        }
    }

    private String dateConvert(String updated_at) {
        updated_at = updated_at.split("T")[0];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        Date date;
        try {
            date = formatter.parse(updated_at);
            SimpleDateFormat convertFormat = new SimpleDateFormat("mm-dd-yyyy");
            return convertFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
