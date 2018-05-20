package com.projects.melih.baking.di;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.projects.melih.baking.BakingApplication;
import com.projects.melih.baking.AppExecutors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Melih Gültekin on 23.04.2018
 */
@Module(includes = {ViewModelModule.class})
public class SingletonModule {

    @Provides
    @Singleton
    Context provideContext(BakingApplication application) {
        return application;
    }

    @Provides
    @Singleton
    AppExecutors provideAppExecutors() {
        return new AppExecutors();
    }

    @VisibleForTesting
    @Provides
    @Singleton
    CountingIdlingResource provideCountingIdlingResource() {
        return new CountingIdlingResource("Network_Call");
    }
}