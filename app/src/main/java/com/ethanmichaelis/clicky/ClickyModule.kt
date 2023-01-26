package com.ethanmichaelis.clicky

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClickyModule {
    @Singleton
    @Provides
    fun providesClickyStore(
        @ApplicationContext context: Context
    ): ClickyStore = ClickyStore(context)
}