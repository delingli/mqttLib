package com.example.idsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public
class SingleSelectedAdapter extends RecyclerView.Adapter<SingleSelectedAdapter.SelectedHolder> {
    private List<ItemBean> mList;
    private Context mContext;

    public SingleSelectedAdapter(List<ItemBean> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    public ItemBean getItemBean() {
        ItemBean itemBean = null;
        for (ItemBean it : mList) {
            if (it.getSelected()) {
                itemBean = it;
                break;
            }
        }
        return itemBean;
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void item(ItemBean itemBean);
    }

    @NonNull
    @Override
    public SelectedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedHolder(LayoutInflater.from(mContext).inflate(R.layout.item_selected, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedHolder holder, int position) {
        ItemBean mItem = mList.get(position);
        holder.tv_text.setText(mItem.getName());
        if (mItem.getSelected()) {
            holder.iv_icon.setImageResource(R.drawable.selected_ic_baseline_accessible_forward_24);
        } else {
            holder.iv_icon.setImageResource(R.drawable.unselected_ic_baseline_accessible_forward_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    for (ItemBean it : mList) {
                        it.setSelected(false);
                    }
                    mItem.setSelected(true);
                    notifyDataSetChanged();
                    onItemClickListener.item(mItem);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class SelectedHolder extends RecyclerView.ViewHolder {
        private TextView tv_text;
        private ImageView iv_icon;

        public SelectedHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
