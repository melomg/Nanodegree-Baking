package com.projects.melih.baking.di;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import dagger.releasablereferences.CanReleaseReferences;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Melih Gültekin on 22.04.2018
 */
@Retention(RUNTIME)
@CanReleaseReferences
@Scope
public @interface ScopeActivity {
}