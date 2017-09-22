package wydr.sellers.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import wydr.sellers.R;
import wydr.sellers.acc.ListLoader;
import wydr.sellers.activities.EditQuery;
import wydr.sellers.modal.QueryModal;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by surya on 19/8/15.
 */
public class MyQueryAdapter extends BaseAdapter implements Filterable
{
    private static LayoutInflater inflater = null;
    public ListLoader imageLoader;
    public int pos;
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
    private Activity activity;
    private ArrayList<QueryModal> data;
    private ArrayList<QueryModal> memberData;

    public MyQueryAdapter(Activity a, ArrayList<QueryModal> d)
    {
        activity = a;
        data = d;
        this.memberData = d;
        imageLoader = new ListLoader(activity);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount()
    {
        if(data!=null)
        return data.size();
        else
            return  0;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        pos = position;
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.my_query_layout, null);

        TextView text = (TextView) vi.findViewById(R.id.txtMyQueryText); // title
        ImageView image = (ImageView) vi.findViewById(R.id.imageView2);
        final ImageView menu_btn = (ImageView) vi.findViewById(R.id.menu_btn);
        QueryModal modal = data.get(pos);

        try
        {
            text.setText(Html.fromHtml("<b>" + modal.getTitle() + "</b>" + "<br>" + "Product Code : <b>" + modal.getCode() + "</b> Quantity : <b>" + modal.getQuantity() + "</b> Order Type : <b>" + modal.getType() + " </b> Location : <b>" + modal.getLocation() + "</b>" + " Needed By : <b>" + format2.format(format.parse(modal.getNeeded())) + "</b>"));
        }

        catch (ParseException e) {
            e.printStackTrace();
        }

        if (!modal.getImageurls().equalsIgnoreCase(""))
        {
            Picasso.with(context)
                    .load(modal.getImageurls())
                    .placeholder(R.drawable.default_product)
                    .into(image);
        }// imageLoader.DisplayImage2(modal.getImageurls(), image, R.drawable.default_product);

        Log.i("data.get(position)", data.get(position).getTitle());
        menu_btn.setTag(data.get(position));
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("data.get(position)", data.get(pos).getTitle());
//                Dialog dialog = new Dialog(activity);
                final PopupWindow popupWindow = new PopupWindow(activity);
                View view = LayoutInflater.from(activity).inflate(R.layout.query_item_menu, null);
                view.findViewById(R.id.like_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.share_wrap).setVisibility(View.GONE);
                view.findViewById(R.id.edit_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.delete_wrap).setVisibility(View.VISIBLE);
                view.findViewById(R.id.edit_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QueryModal editobj = (QueryModal) menu_btn.getTag();
                        Intent edit_query_intent = new Intent(activity, EditQuery.class);
                        Log.i("MyQueryAdapter--", editobj.getCategoryID() + "/" + editobj.getSubCategoryID() + "/" + data.get(pos).getChild_categoryid() + "/" + data.get(pos).getGrand_category_id());
                        edit_query_intent.putExtra("query_id", data.get(position).getQuery_id());
                        edit_query_intent.putExtra("category_id", data.get(position).getCategoryID());
                        edit_query_intent.putExtra("sub_category_id", data.get(position).getSubCategoryID());
                        edit_query_intent.putExtra("child_category_id", data.get(position).getChild_categoryid());
                        edit_query_intent.putExtra("grand_child_category_id", data.get(position).getGrand_category_id());
                        edit_query_intent.putExtra("title", data.get(position).getTitle());
                        edit_query_intent.putExtra("prod_code", data.get(position).getCode());
                        edit_query_intent.putExtra("quantity", data.get(position).getQuantity());
                        edit_query_intent.putExtra("query_type", data.get(position).getType());
                        edit_query_intent.putExtra("needed_by", data.get(position).getNeeded());
                        edit_query_intent.putExtra("location", data.get(position).getLocation());
                        String[] image_urls = new String[data.get(position).getImageURLs().size()];
                        data.get(position).getImageURLs().toArray(image_urls);
                        edit_query_intent.putExtra("image_urls", image_urls);
                        // activity.finish();
                        activity.startActivity(edit_query_intent);
                        popupWindow.dismiss();
                    }
                });


                view.findViewById(R.id.delete_wrap).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Confirm Delete")
                                .setMessage("Do you really want to delete this query?")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AsyncTask<Void, Void, Void>() {
                                            private int flag = 0;
                                            private ProgressDialog progressDialog;
                                            private AlertDialog.Builder alertDialog;

                                            @Override
                                            protected void onPreExecute() {
                                                super.onPreExecute();
                                                progressDialog = new ProgressDialog(activity);
                                                progressDialog.setMessage("Deleting...");
                                                progressDialog.setCancelable(false);
                                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.show();
                                                alertDialog = new AlertDialog.Builder(activity)
                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }

                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                JSONObject json = new UserFunctions().deleteQuery(data.get(position).getQuery_id(), activity);
                                                if (json == null) {
                                                    flag = 1;
                                                } else {
                                                    if (json.has("error")){
                                                        try {
                                                            if(json.getString("error").equalsIgnoreCase(activity.getString(R.string.no_content)))
                                                                flag = 0;
                                                            else
                                                                flag=1;
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                }
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {
                                                super.onPostExecute(aVoid);
                                                progressDialog.dismiss();
                                                if (flag == 0) {
                                                    //data.remove(position);
                                                    //MyQueryAdapter.this.notifyDataSetChanged();
                                                    alertDialog.setTitle("Success");
                                                    alertDialog.setMessage("Query Deleted");
                                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub
//                                                            activity.startActivity(new Intent(activity, MyQuery.class));
//                                                            activity.finish();
                                                            data.remove(position);
                                                            MyQueryAdapter.this.notifyDataSetChanged();
                                                        }
                                                    });
                                                    alertDialog.show();

                                                } else if (flag == 1) {

                                                    alertDialog.setTitle(activity.getResources().getString(R.string.sorry));
                                                    alertDialog.setMessage(activity.getResources().getString(R.string.server_error));
                                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    alertDialog.show();

                                                } else if (flag == 2) {

                                                    alertDialog.setTitle(activity.getResources().getString(R.string.error));
                                                    alertDialog.setMessage(activity.getResources().getString(R.string.page_not_found));
                                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    alertDialog.show();

                                                }
                                            }
                                        }.execute();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setContentView(view);
                popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int xOffset = -(view.getMeasuredWidth() - (menu_btn.getWidth() / 2) + 10);
                int yOffset = -(menu_btn.getHeight());
                popupWindow.showAsDropDown(menu_btn, xOffset, yOffset);
            }
        });

        return vi;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // values
                ArrayList<QueryModal> filterlist = new ArrayList<QueryModal>();

                if (memberData == null) {
                    memberData = new ArrayList<>();

                }
                if (constraint != null && memberData != null && memberData.size() > 0) {

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < memberData.size(); i++)
                    {
                        String data = memberData.get(i).getTitle();
                        Log.i("data", data);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            QueryModal modal = new QueryModal();
                            modal.setQuery_id(memberData.get(i).getQuery_id());
                            modal.setCode(memberData.get(i).getCode());
                            modal.setLocation(memberData.get(i).getLocation());
                            modal.setNeeded(memberData.get(i).getNeeded());
                            modal.setTitle(memberData.get(i).getTitle());
                            modal.setQuantity(memberData.get(i).getQuantity());
                            modal.setId(memberData.get(i).getId());
                            modal.setType(memberData.get(i).getType());
                            modal.setCategoryID(memberData.get(i).getCategoryID());
                            modal.setSubCategoryID(memberData.get(i).getSubCategoryID());
                            modal.setChild_categoryid(memberData.get(i).getChild_categoryid());
                            modal.setImageurls(memberData.get(i).getImageurls());
                            modal.setImageURLs(memberData.get(i).getImageURLs());
                            modal.setGrand_category_id(memberData.get(i).getGrand_category_id());
                            filterlist.add(modal);
                        }
                    }
                    results.values = filterlist;

                }

                // }

                return results;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                data = (ArrayList<QueryModal>) results.values;
                notifyDataSetChanged();
            }
        }

                ;

    }
}
