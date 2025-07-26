package com.example.contractorsapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contractorsapp.R;
import com.example.contractorsapp.data.model.Contragent;
import java.util.ArrayList;
import java.util.List;

public class ContragentAdapter extends RecyclerView.Adapter<ContragentAdapter.ContragentViewHolder> {

    private List<Contragent> contragents = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ContragentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contragent, parent, false);
        return new ContragentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContragentViewHolder holder, int position) {
        Contragent currentContragent = contragents.get(position);
        holder.textViewName.setText(currentContragent.getName());

        String phone = currentContragent.getPhone();

        // Проверяваме дали телефонният номер не е празен или null
        if (phone != null && !phone.trim().isEmpty()) {
            // Взимаме контекста от itemView, за да достъпим string ресурсите
            android.content.Context context = holder.itemView.getContext();
            // Форматираме низа, като заменяме %1$s с реалния номер
            String formattedPhoneText = context.getString(R.string.phone_label, phone);

            holder.textViewPhone.setText(formattedPhoneText);
            holder.textViewPhone.setVisibility(android.view.View.VISIBLE); // Уверяваме се, че полето е видимо
        } else {
            // Ако няма телефон, скриваме полето изцяло, за да е по-чисто
            holder.textViewPhone.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contragents.size();
    }

    public void setContragents(List<Contragent> contragents) {
        this.contragents = contragents;
        notifyDataSetChanged();
    }

    class ContragentViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewPhone;

        public ContragentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPhone = itemView.findViewById(R.id.text_view_phone);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(contragents.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Contragent contragent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}