package com.example.cache.di.component

import android.app.Application
import com.example.cache.CacheApp
import com.example.cache.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuildersModule::class,
    FragmentBuildersModule::class,
    ViewModelFactoryModule::class,
    RepositoryModule::class
])
interface AppComponent : AndroidInjector<CacheApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}