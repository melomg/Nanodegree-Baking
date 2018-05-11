package com.projects.melih.baking.ui.step;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.model.Step;

import java.util.List;

/**
 * Created by Melih Gültekin on 08.05.2018
 */
class StepPagerAdapter extends PagerAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private List<Step> stepList;

    StepPagerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.pager_step, container, false);
        TextView textView = view.findViewById(R.id.name);
        Step step = stepList.get(position);
        textView.setText(step.getDescription());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(stepList);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setStepList(@Nullable List<Step> stepList) {
        this.stepList = stepList;
        notifyDataSetChanged();
    }
}