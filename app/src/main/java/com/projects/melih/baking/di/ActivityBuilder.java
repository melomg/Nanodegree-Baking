package com.projects.melih.baking.di;

import com.projects.melih.baking.ui.main.MainActivity;
import com.projects.melih.baking.ui.main.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Melih Gültekin on 23.04.2018
 */
@Module
public abstract class ActivityBuilder {

    @ScopeActivity
    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity bindMainActivity();
}