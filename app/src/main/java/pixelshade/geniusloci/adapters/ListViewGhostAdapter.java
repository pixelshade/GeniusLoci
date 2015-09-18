package pixelshade.geniusloci.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pixelshade.geniusloci.R;
import pixelshade.geniusloci.model.GhostEntry;

/**
 * Created by pixelshade on 18/09/15.
 */
public class ListViewGhostAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<GhostEntry> ghostEntries;

    public ListViewGhostAdapter(LayoutInflater inflater, List<GhostEntry> ghostEntries) {
        this.inflater = inflater;
        this.ghostEntries = ghostEntries;
    }


    @Override
    public int getCount() {
        return ghostEntries.size();
    }

    @Override
    public GhostEntry getItem(int i) {
        return ghostEntries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.ghost_entry_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        if(ghostEntries.get(position).content.contains("imgur")) {
            Picasso.with(inflater.getContext())
                    .load(ghostEntries.get(position).content)
                    .into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.INVISIBLE);

        } else {
            holder.content.setText("content");
            holder.content.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.INVISIBLE);
        }
        holder.text.setText(ghostEntries.get(position).name);



        return view;
    }

    static class ViewHolder {
        @Bind(R.id.image_in_item)
        ImageView image;
        @Bind(R.id.textview_in_item)
        TextView text;
        @Bind(R.id.content_in_item)
        TextView content;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
