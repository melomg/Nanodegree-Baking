package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.ui.recipe.RecipeListFragment;
import com.projects.melih.baking.util.EspressoTestUtil;
import com.projects.melih.baking.util.RecyclerViewMatcher;
import com.projects.melih.baking.util.ViewModelUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeListTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private final MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private RecipesViewModel viewModel;

    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(activityTestRule);
        RecipeListFragment fragment = RecipeListFragment.newInstance();
        viewModel = mock(RecipesViewModel.class);
        fragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        when(viewModel.getRecipesLiveData()).thenReturn(recipesLiveData);
        when(viewModel.getLoadingLiveData()).thenReturn(loadingLiveData);
        doNothing().when(viewModel).fetchData();
        try {
            activityTestRule.runOnUiThread(() -> activityTestRule.getActivity().replaceFragment(fragment));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void loading() {
        loadingLiveData.postValue(true);
        onView(withId(R.id.swipe_refresh)).check(matches(isDisplayed()));
    }

    @Test
    public void nullRecipeList() {
        recipesLiveData.postValue(null);
        onView(listMatcher().atPosition(0)).check(doesNotExist());
    }

    @Test
    public void recipeListTest() {
        List<Recipe> recipes = recipesLiveData.getValue();
        if (recipes != null) {
            final int size = recipes.size();
            for (int pos = 0; pos < size; pos++) {
                Recipe recipe = recipes.get(pos);
                onView(listMatcher().atPosition(pos)).check(
                        matches(hasDescendant(withText(recipe.getName()))));
            }
        }
    }

    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}