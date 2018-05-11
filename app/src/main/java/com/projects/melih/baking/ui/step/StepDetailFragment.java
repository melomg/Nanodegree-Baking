package com.projects.melih.baking.ui.step;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.databinding.FragmentStepDetailBinding;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.ui.base.BaseFragment;
import com.projects.melih.baking.ui.main.RecipesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 06.05.2018
 */
public class StepDetailFragment extends BaseFragment implements View.OnClickListener {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentStepDetailBinding binding;
    private RecipesViewModel recipesViewModel;
    private StepPagerAdapter adapter;
    private VideoPlayerObserver videoPlayerObserver;

    public static StepDetailFragment newInstance() {
        return new StepDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoPlayerObserver = new VideoPlayerObserver();
        getLifecycle().addObserver(videoPlayerObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_detail, container, false);
        recipesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipesViewModel.class);

        recipesViewModel.getStepListLiveData().observe(this, steps -> {
            if (CollectionUtils.isNotEmpty(steps)) {
                ArrayList<String> uris = new ArrayList<>();
                for (Step step : steps) {
                    uris.add(step.getVideoUrl());
                }
                videoPlayerObserver.setUris(uris);
            }
            adapter.setStepList(steps);
        });
        recipesViewModel.getSelectedStepPositionLiveData().observe(this, position -> {
            if (position != null) {
                List<Step> steps = recipesViewModel.getStepListLiveData().getValue();
                final int stepListSize = CollectionUtils.size(steps);
                binding.next.setVisibility(((stepListSize - 1) == position) ? View.GONE : View.VISIBLE);

                if (stepListSize > 0) {
                    @SuppressWarnings("ConstantConditions")
                    Step selectedStep = steps.get(position);
                    // if the video url of this step is empty show an image
                    if (TextUtils.isEmpty(selectedStep.getVideoUrl())) {
                        String thumbnailUrl = selectedStep.getThumbnailUrl();
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .placeholder(R.drawable.ic_recipe_placeholder)
                                .error(R.drawable.ic_recipe_placeholder);
                        Glide.with(context)
                                .asBitmap()
                                .apply(options)
                                .load(thumbnailUrl)
                                .thumbnail(0.1f)
                                .into(binding.stepImage);
                        videoPlayerObserver.stopMediaPlayer();

                        binding.stepImage.setVisibility(View.VISIBLE);
                        binding.playerView.setVisibility(View.GONE);
                    } else {
                        binding.stepImage.setVisibility(View.GONE);
                        binding.playerView.setVisibility(View.VISIBLE);
                        videoPlayerObserver.setSelectedVideoIndex(position);
                    }
                }
                if (position == 0) {
                    binding.previous.setVisibility(View.GONE);
                    binding.stepCount.setText("");
                } else {
                    binding.previous.setVisibility(View.VISIBLE);
                    binding.stepCount.setText(context.getString(R.string.step_count, position, (stepListSize - 1)));
                }

                binding.viewPager.setCurrentItem(position);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new StepPagerAdapter(context);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCanScroll(false);

        videoPlayerObserver.setPlayerView(context, binding.playerView);

        binding.previous.setOnClickListener(this);
        binding.next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Utils.await(v);
        switch (v.getId()) {
            case R.id.previous:
                recipesViewModel.goToPreviousStep();
                break;
            case R.id.next:
                recipesViewModel.goToNextStep();
                break;
        }
    }
}