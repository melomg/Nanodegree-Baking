package com.projects.melih.baking.ui.recipe;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.databinding.ItemStepListBinding;
import com.projects.melih.baking.model.Step;

import java.util.List;

/**
 * Created by Melih Gültekin on 05.05.2018
 */
class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepViewHolder> {
    private final ItemClickListener recipeItemListener;
    private final AsyncListDiffer<Step> differ = new AsyncListDiffer<>(this, Step.DIFF_CALLBACK);

    StepListAdapter(@NonNull ItemClickListener recipeItemListener) {
        this.recipeItemListener = recipeItemListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StepViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_step_list, parent, false), recipeItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bindTo(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    void submitStepList(@Nullable List<Step> recipeList) {
        differ.submitList(recipeList);
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final ItemStepListBinding binding;

        StepViewHolder(@NonNull ItemStepListBinding binding, @NonNull final ItemClickListener recipeItemListener) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                Utils.await(v);
                final int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    recipeItemListener.onItemClick(adapterPosition);
                }
            });
        }

        void bindTo(@Nullable final Step step) {
            if (step != null) {
                binding.setStep(step);
                if (step.isSelected()) {
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
                } else {
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                }
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}