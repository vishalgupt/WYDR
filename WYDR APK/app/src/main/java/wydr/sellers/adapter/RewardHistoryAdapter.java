package wydr.sellers.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.modal.RewardHistoryObject;

/**
 * Created by Ishtiyaq on 4/6/2016.
 */
/*public class RewardHistoryAdapter extends ArrayAdapter<RewardHistoryObject> {
    private final Activity context;
    private final ArrayList<RewardHistoryObject> list;
    private int lastPosition = -1;
    private int startCount;
    //private int count;
    private int stepNumber;

    *//**
     * @param context
     * @param list RewardHistoryObject Array List
     *//*
    public RewardHistoryAdapter(Activity context, ArrayList<RewardHistoryObject> list) {
        super(context, R.layout.reward_row, list);
        this.context = context;
        this.list = list;
        //this.startCount = Math.min(startCount, list.size());
        //this.count = this.startCount;
        //this.stepNumber = stepNumber;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.reward_row, null, true);

        TextView deductionDate_tv = (TextView) rowView.findViewById(R.id.deductionDate_tv);
        deductionDate_tv.setText(list.get(position).getDeductionDate());

        TextView expirationDate_tv = (TextView) rowView.findViewById(R.id.expirationDate_tv);
        expirationDate_tv.setText(list.get(position).getExpirationDate());

        TextView deductionReason_tv = (TextView) rowView.findViewById(R.id.deductionReason_tv);
        deductionReason_tv.setText(list.get(position).getDeductionReason());

        TextView deductionAmount_tv = (TextView) rowView.findViewById(R.id.deductionAmount_tv);
        deductionAmount_tv.setText(list.get(position).getAmount());

        return rowView;
    }

    *//**
     * Show more views, or the bottom
     * @return true if the entire data set is being displayed, false otherwise
     *//*
    *//*public boolean showMore(){
        if(count == list.size()) {
            return true;
        }else{
            count = Math.min(count + stepNumber, list.size()); //don't go past the end
            notifyDataSetChanged(); //the count size has changed, so notify the super of the change
            return endReached();
        }
    }*//*

    *//**
     * @return true if then entire data set is being displayed, false otherwise
     *//*
    *//*public boolean endReached(){
        return count == list.size();
    }*//*

    *//**
     * Sets the ListView back to its initial count number
     *//*
    *//*public void reset(){
        count = startCount;
        notifyDataSetChanged();
    }*//*

    *//*@Override
    public int getCount() {
        return count;
    }*//*
}*/


public class RewardHistoryAdapter extends RecyclerView.Adapter<RewardHistoryAdapter.ViewHolder>
{
    private List<RewardHistoryObject> rewardHistoryObjects;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView deductionDate_tv, expirationDate_tv, deductionReason_tv, deductionAmount_tv;
        public TextView issueOrDeduct, issueOrDeductReason;

        public ViewHolder(View v) {
            super(v);
            //mTextView = v;
            deductionDate_tv = (TextView) v.findViewById(R.id.deductionDate_tv);
            expirationDate_tv = (TextView) v.findViewById(R.id.expirationDate_tv);
            deductionReason_tv = (TextView) v.findViewById(R.id.deductionReason_tv);
            deductionAmount_tv = (TextView) v.findViewById(R.id.deductionAmount_tv);

            issueOrDeduct = (TextView) v.findViewById(R.id.issueOrDeduct);
            issueOrDeductReason = (TextView) v.findViewById(R.id.issueOrDeductReason);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RewardHistoryAdapter(List<RewardHistoryObject> rewardHistoryObjects) {
        this.rewardHistoryObjects = rewardHistoryObjects;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RewardHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.deductionDate_tv.setText(rewardHistoryObjects.get(position).getDeductionDate());
        holder.expirationDate_tv.setText(rewardHistoryObjects.get(position).getExpirationDate());
        holder.deductionReason_tv.setText(rewardHistoryObjects.get(position).getDeductionReason());
        String amount = rewardHistoryObjects.get(position).getAmount();
        holder.deductionAmount_tv.setText(amount);

        if (Double.parseDouble(amount) >= 0) {
            holder.issueOrDeduct.setText("Issue Date");
            holder.issueOrDeductReason.setText("Issue Reason");
        } else {
            holder.issueOrDeduct.setText("Deduction Date");
            holder.issueOrDeductReason.setText("Deduction Reason");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return rewardHistoryObjects == null ? 0 : rewardHistoryObjects.size();
    }
}