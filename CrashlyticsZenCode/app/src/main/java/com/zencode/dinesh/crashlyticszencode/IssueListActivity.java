package com.zencode.dinesh.crashlyticszencode;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class IssueListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView issueRecyclerView;
    String ISSUE_API_URL = "https://api.github.com/repos/crashlytics/secureudid/issues";
    GetCrashlyticsIssueListRepository issueListRepository;
    IssueRecyclerListAdapter issueRecyclerListAdapter;
    List<Issues> issuesList;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_list);
        Stetho.initializeWithDefaults(this);
        try{
            intializeViews();
            invokeAPICall(false);
        }catch (Exception e){
            Log.d("parm oncreate",e.getMessage());
        }

    }

    private void invokeAPICall(final boolean b) {
        issueListRepository = new GetCrashlyticsIssueListRepository(this,false);
        issueListRepository.setIssueListFromUrl(new GetCrashlyticsIssueListRepository.GetIssueListFromUrl() {
            @Override
            public void mIssueSuccessCB(List<Issues> issuesListItm) {
                if(issuesListItm != null &&issuesListItm.size() > 0){
                    issuesList = new ArrayList<Issues>(issuesListItm);
                    if(NetworkUtils.checkIfNetworkAvailable(IssueListActivity.this)){
                        issueListRepository.crashlyticsBulkInsert(issuesListItm);
                    }
                    onBindRecyclerViewList();

                }
            }

            @Override
            public void mIssueFailureCB(String message) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if(NetworkUtils.isNetworkAvailable(this)){
            swipeRefreshLayout.setRefreshing(false);
            issueListRepository.mAPICallForIssueList(ISSUE_API_URL);
        }else{
            swipeRefreshLayout.setRefreshing(false);
            issueListRepository.getCrashlyticsFromLocalDB();
        }

    }

    private void intializeViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        issueRecyclerView = (RecyclerView)findViewById(R.id.issueRecyclerView);
        issuesList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void onBindRecyclerViewList() {
        if(issuesList != null && issuesList.size() > 0){
            issueRecyclerView.setHasFixedSize(true);
            issueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            issueRecyclerListAdapter = new IssueRecyclerListAdapter(issuesList,this,false);
            issueRecyclerView.setAdapter(issueRecyclerListAdapter);

            issueRecyclerListAdapter.setOnItemClickListener(new IssueRecyclerListAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Issues issuesObject = issueRecyclerListAdapter.getItemAt(position);
                    Intent intent = new Intent(IssueListActivity.this,IssueDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("IntentObject", issuesObject);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
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

    @Override
    public void onRefresh() {

        invokeAPICall(true);
    }
}
