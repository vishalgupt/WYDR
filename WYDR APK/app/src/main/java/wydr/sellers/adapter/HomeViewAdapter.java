package wydr.sellers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import wydr.sellers.R;
import wydr.sellers.modal.GridRow;
import wydr.sellers.network.ImageLoader;

public class HomeViewAdapter extends ArrayAdapter<GridRow> {
    ImageLoader imageLoader;
    private int layout;
    private Context c;
    private List<GridRow> objects;

    public HomeViewAdapter(Context context, int textViewResourceId,
                           List<GridRow> objects) {
        super(context, textViewResourceId, objects);
        layout = textViewResourceId;
        c = context;
        this.objects = objects;
        imageLoader = new ImageLoader(c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder = null;
        LayoutInflater inflater = ((Activity) c).getLayoutInflater();
        view = inflater.inflate(layout, parent, false);
        holder = new Holder();
        holder.demo = objects.get(position);
        holder._view = (ImageView) view.findViewById(R.id.imageViewCategory);
        holder._title = (TextView) view.findViewById(R.id.textViewCategoryName);
        view.setTag(holder);
        setUp(holder);

        return view;

    }


    private void setUp(Holder holder) {
        holder._title.setText("" + holder.demo.title.toUpperCase());
        imageLoader.DisplayImage2(holder.demo.getUrl(), holder._view);

    }

    public static class Holder {
        GridRow demo;
        ImageView _view;
        TextView _title;
    }
}
