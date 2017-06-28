package com.zencode.dinesh.crashlyticszencode;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dinesh on 6/28/2017.
 */

class IssueRecyclerListAdapter extends RecyclerView.Adapter<IssueRecyclerListAdapter.ViewHolder>{
    public static MyClickListener myClickListener;
    public Activity mcontext;
    List<Issues> issuesList;
    boolean isFromDetails;

    public IssueRecyclerListAdapter (List<Issues> issuesList,Activity activity,Boolean isFrom){
        mcontext = activity;
        this.issuesList = issuesList;
        isFromDetails = isFrom;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Issues issues =issuesList.get(position);
        if(issues != null){

            if(!isFromDetails){
                holder.title.setText(issues.getTitle());
                holder.dec.setText(Html.fromHtml(issues.getBody()).toString().trim());
                holder.name.setText(issues.getLogin());
                holder.updateDate.setText(dateConvert(issues.getUpdated_at()));
                if(issues.getAvatar_url() != null){
                    Ion.with(mcontext).load(issues.getAvatar_url()).intoImageView(holder.avatar);
                }


                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(myClickListener != null){
                            myClickListener.onItemClick(position,v);
                        }
                    }
                });
            }else{
                holder.title.setVisibility(View.GONE);
                holder.dec.setText(Html.fromHtml(issues.getBody()).toString().trim());
                holder.name.setText(issues.getLogin());
                holder.updateDate.setText(dateConvert(issues.getUpdated_at()));
                if(issues.getAvatar_url() != null){
                    Ion.with(mcontext).load(issues.getAvatar_url()).intoImageView(holder.avatar);
                }


                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(myClickListener != null){
                            myClickListener.onItemClick(position,v);
                        }
                    }
                });
            }


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
    public int getItemCount() {
        return issuesList.size();
    }
    public Issues getItemAt(final int position){
        return issuesList.get(position);
    }


    public interface MyClickListener {
        public void onItemClick(int position,View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
        View parentView = (View)itemView.findViewById(R.id.parentLayout1);
        TextView title = (TextView)itemView.findViewById(R.id.TitleTv);
        TextView name = (TextView)itemView.findViewById(R.id.NameTV);
        TextView dec = (TextView)itemView.findViewById(R.id.DecTv);
        TextView updateDate = (TextView)itemView.findViewById(R.id.DateTV);
        ImageView avatar = (ImageView)itemView.findViewById(R.id.avatar);

    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}
