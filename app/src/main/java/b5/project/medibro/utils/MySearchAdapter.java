package b5.project.medibro.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import b5.project.medibro.R;
import b5.project.medibro.SearchActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Abhijith on 3/2/2016.
 */
public class MySearchAdapter extends RecyclerView.Adapter<MySearchAdapter.VHItem> {

    Context context;
    ArrayList<String> topics;
    private static final int TYPE_ITEM = 1;
    private final LayoutInflater mInflater;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    ItemClickListener onItemClickListener;

    public MySearchAdapter(Context context, ArrayList<String> topics) {
        this.context = context;
        this.topics = topics;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.layout_search_topic_item, parent, false);
        return new VHItem(v);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        final String dataItem = getItem(position);
        holder.bind(dataItem);
        holder.mSearchTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked at :" + dataItem, Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, SearchActivity.class);
                i.putExtra("topic", dataItem);
                i.putStringArrayListExtra("topics", topics);
                context.startActivity(i);
            }
        });
    }

    public void setItemClickListener(final ItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    private String getItem(int position) {
        return topics.get(position);
    }


    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.search_topic_tv)
        TextView mSearchTopic;

        public VHItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(String topic) {
            mSearchTopic.setText(topic);
        }

        @Override
        public void onClick(View v) {
            Log.d("Grid Adapter", "Item Clicked");
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
