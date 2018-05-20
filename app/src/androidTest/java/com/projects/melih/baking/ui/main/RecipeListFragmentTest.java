package com.projects.melih.baking.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.projects.melih.baking.BakingApplication;
import com.projects.melih.baking.R;
import com.projects.melih.baking.model.Recipe;
import com.projects.melih.baking.ui.recipe.RecipeListFragment;
import com.projects.melih.baking.util.EspressoTestUtil;
import com.projects.melih.baking.util.RecyclerViewMatcher;
import com.projects.melih.baking.util.ViewModelUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeListFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private final MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private RecipesViewModel viewModel;
    private CountingIdlingResource idlingResource;

    @Before
    public void init() {
        final BakingApplication application = (BakingApplication) activityTestRule.getActivity().getApplication();
        idlingResource = application.getSingletonComponent().idlingResource();
        IdlingRegistry.getInstance().register(idlingResource);

        EspressoTestUtil.disableProgressBarAnimations(activityTestRule);
        RecipeListFragment fragment = RecipeListFragment.newInstance();
        viewModel = mock(RecipesViewModel.class);

        when(viewModel.getRecipesLiveData()).thenReturn(recipesLiveData);
        when(viewModel.getLoadingLiveData()).thenReturn(loadingLiveData);
        doNothing().when(viewModel).fetchData();
        try {
            activityTestRule.runOnUiThread(() -> activityTestRule.getActivity().replaceFragment(fragment));
            fragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void loading() {
        loadingLiveData.postValue(true);
        onView(ViewMatchers.withId(R.id.swipe_refresh)).check(matches(isDisplayed()));
    }

    @Test
    public void recipeListTest() {
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));

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

    @Test
    public void recipeListFragmentOpensDetailTest() {
        onView(listMatcher().atPosition(1)).perform(click());
        List<Recipe> recipes = recipesLiveData.getValue();
        if ((recipes != null) && (recipes.size() > 0)) {
            onView(withId(R.id.action_bar)).check(matches(withToolbarTitle(is((recipes.get(1).getName())))));
        }
    }

    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view);
    }

    private static Matcher<View> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}