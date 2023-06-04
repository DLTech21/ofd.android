package io.github.dltech21.pdf.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;

import io.github.dltech21.ofd.R;
import io.github.dltech21.pdf.model.SignatureInformation;

import java.util.ArrayList;


public class SigntureAdapter extends RecyclerView.Adapter<SigntureAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SignatureInformation> list = new ArrayList<>();
    private ItemListener listener;

    public SigntureAdapter(Context context, ArrayList<SignatureInformation> list) {
        this.context = context;
        if (list != null) {
            this.list.addAll(list);
        }
    }

    public ArrayList<SignatureInformation> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_signture, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!StringUtils.isEmpty(list.get(position).getSigner())) {
            holder.tvItem.setText(String.format(context.getString(R.string.text_signer_item), list.get(position).getSigner()));
        } else {
            if (list.get(position).isSignatureValid()) {
                holder.tvItem.setText("未知");
            } else {
                holder.tvItem.setText("无效签名");
            }
        }
        if (list.get(position).isCheck()) {
            if (list.get(position).isSignatureValid()) {
                holder.tvValue.setText("有效");
            } else
                holder.tvValue.setText("无效");
        } else {
            holder.tvValue.setText("未知");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ItemListener getListener() {
        return listener;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem, tvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
            tvValue = itemView.findViewById(R.id.tv_item_value);
        }
    }

    public interface ItemListener {
        void onItemClick(int position);
    }
}
