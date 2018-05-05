package com.projects.melih.baking.di;

import com.projects.melih.baking.BakingApplication;
import com.projects.melih.baking.repository.remote.ApiModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Melih Gültekin on 23.04.2018
 */
@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        SingletonModule.class,
        ActivityBuilder.class,
        ApiModule.class
})
public interface SingletonComponent extends AndroidInjector<BakingApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<BakingApplication> {
    }
}