package com.projects.melih.baking;

import android.os.StrictMode;

import com.projects.melih.baking.di.DaggerSingletonComponent;
import com.projects.melih.baking.di.SingletonComponent;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
public class BakingApplication extends DaggerApplication {

    @Inject
    SingletonComponent singletonComponent;

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected AndroidInjector<? extends BakingApplication> applicationInjector() {
        return DaggerSingletonComponent.builder().create(this);
    }

    public SingletonComponent getSingletonComponent() {
        return singletonComponent;
    }
}