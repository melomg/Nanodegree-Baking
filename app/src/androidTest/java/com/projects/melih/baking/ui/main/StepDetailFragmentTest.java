package com.projects.melih.baking.ui.main;


import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.projects.melih.baking.BakingApplication;
import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.ui.recipe.RecipeActivity;
import com.projects.melih.baking.ui.recipe.RecipeViewModel;
import com.projects.melih.baking.ui.step.StepDetailFragment;
import com.projects.melih.baking.util.EspressoTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StepDetailFragmentTest {

    @Rule
    public ActivityTestRule<RecipeActivity> activityTestRule =
            new ActivityTestRule<RecipeActivity>(RecipeActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    final Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    return RecipeActivity.newIntent(targetContext, 1);
                }
            };
    private final MutableLiveData<List<Step>> stepListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedStepPositionLiveData = new MutableLiveData<>();
    private RecipeViewModel viewModel;
    private CountingIdlingResource idlingResource;

    @Before
    public void init() {
        final BakingApplication application = (BakingApplication) activityTestRule.getActivity().getApplication();
        idlingResource = application.getSingletonComponent().idlingResource();
        IdlingRegistry.getInstance().register(idlingResource);

        EspressoTestUtil.disableProgressBarAnimations(activityTestRule);
        StepDetailFragment fragment = StepDetailFragment.newInstance();
        viewModel = mock(RecipeViewModel.class);

        when(viewModel.getStepListLiveData()).thenReturn(stepListLiveData);
        when(viewModel.getSelectedStepPositionLiveData()).thenReturn(selectedStepPositionLiveData);

        try {
            activityTestRule.runOnUiThread(() -> activityTestRule.getActivity().replaceFragment(fragment));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void recipeStepTest() {
        onView(ViewMatchers.withId(R.id.player_view)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.previous)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.next)).check(matches(withText("NEXT")));
    }
}