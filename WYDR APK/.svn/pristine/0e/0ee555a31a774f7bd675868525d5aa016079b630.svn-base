package wydr.sellers.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.kenai.jbosh.BOSHClientConnListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wydr.sellers.R;
import wydr.sellers.acc.CartSchema;
import wydr.sellers.modal.CouponModal;
import wydr.sellers.modal.VarientsModal;

/**
 * Created by aksha_000 on 5/3/2016.
 */
public class VarientsAdapter extends RecyclerView.Adapter<VarientsAdapter.MyViewHolder>
{
    private ArrayList<VarientsModal>varientsModals = new ArrayList<>();
    Context context;
    VarientTypeAdapter adapter;

    public VarientsAdapter(ArrayList<VarientsModal>v,Context context)
    {
        this.context = context;
        this.varientsModals = v;
    }

    @Override
    public VarientsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.varients,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(VarientsAdapter.MyViewHolder holder, int position)
    {
        holder.title.setText(varientsModals.get(position).getVar_name());
        if(!varientsModals.get(position).getVar_json().isEmpty())
        {

        try
        {
            JSONArray jsonArray = new JSONArray(varientsModals.get(position).getVar_json());
            ArrayList<VarientsModal>varientsModals= new ArrayList<>();

            for (int j = 0; j < jsonArray.length(); j++)
            {
                JSONObject obj = (JSONObject) jsonArray.get(j);
                VarientsModal varientsModal = new VarientsModal();
                varientsModal.setVar_type_name(obj.getString("variant_name"));
                varientsModal.setVar_type_id(obj.getString("option_id"));
                varientsModals.add(varientsModal);
            }
            adapter = new VarientTypeAdapter(varientsModals,context);
            holder.var_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.var_rv.setAdapter(adapter);
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        }
    }

    @Override
    public int getItemCount()
    {
        return varientsModals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        RecyclerView var_rv;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.var_title);
            var_rv = (RecyclerView)itemView.findViewById(R.id.var_list);
        }
    }


    //-------------------------akshay----------------------------//
    public class VarientTypeAdapter extends RecyclerView.Adapter<VarientTypeAdapter.MyViewHolder2>
    {
        private ArrayList<VarientsModal>varientsModalArrayList = new ArrayList<>();
        private Context context;
        boolean l= false;
        public VarientTypeAdapter(ArrayList<VarientsModal>as,Context context)
        {
            this.varientsModalArrayList = as;
            this.context = context;
        }

        @Override
        public VarientTypeAdapter.MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType)
        {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.varient_val, parent, false);
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(view);
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(final VarientTypeAdapter.MyViewHolder2 holder, final int position)
        {
            holder.val.setText(varientsModalArrayList.get(position).getVar_type_name());
            holder.val.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    List<NameValuePair>param = new ArrayList<NameValuePair>();
                    param.add(new BasicNameValuePair("varientid",varientsModalArrayList.get(position).getVar_type_id()));

                    if(!l)
                    {
                        holder.val.setBackgroundResource(R.drawable.ring_solid);
                        holder.val.setTextColor(Color.WHITE);
                        holder.val.setTypeface(null, Typeface.BOLD);
                        l=true;
                    }
                    else{
                        holder.val.setBackgroundResource(R.drawable.ring_outline);
                        holder.val.setTextColor(Color.BLACK);
                        holder.val.setTypeface(null, Typeface.BOLD);
                        l=false;
                    }
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return varientsModalArrayList.size();
        }

        public class MyViewHolder2 extends RecyclerView.ViewHolder {
            TextView val;
            public MyViewHolder2(View itemView)
            {
                super(itemView);
                val = (TextView)itemView.findViewById(R.id.txt_var);
            }
        }
    }
}
