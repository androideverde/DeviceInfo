package com.androideverde.deviceinfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jordi.bernabeu on 03/02/2017.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemHolder> {

    private ArrayList<RecyclerItem> dataList;

    public RecyclerAdapter(ArrayList<RecyclerItem> data) {
        dataList = data;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        RecyclerItem item = dataList.get(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtContent;

        public ItemHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
        }

        public void bindItem(RecyclerItem item) {
            txtTitle.setText(item.getTitle());
            txtContent.setText(item.getContent());
        }
    }
}
