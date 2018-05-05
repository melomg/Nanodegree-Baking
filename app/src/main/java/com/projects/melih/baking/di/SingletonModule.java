package com.projects.melih.baking.di;

import android.content.Context;

import com.projects.melih.baking.BakingApplication;

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
}