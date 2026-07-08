package com.bussiness.curemegptapp.di

import android.content.Context
import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.RepositoryImpl
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bussiness.curemegptapp.BuildConfig



@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun cureMeGptOkHttpClient(
        @ApplicationContext context: Context,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5 MB
        val cache = Cache(context.cacheDir, cacheSize)
        val logging = HttpLoggingInterceptor { message ->
            Log.d("RetrofitLog", message)
            Log.d("API", String.format("Response: %s", message));
        }
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }else{
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(networkInterceptor)
            .addInterceptor(logging)
            .addInterceptor { chain -> // Corrected lambda syntax
                val response = chain.proceed(chain.request())
                Log.d("@@@@@@@@", "response: ${response.code}") // Use Kotlin string interpolation
                // If the server responds with a 401 Unauthorized, handle logout
                if (response.code == 401) {
                    SessionEventBus.emitSessionExpired()
                }
                response // Return the response
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun cureMeGptRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun cureMeGptApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindRepository(
            impl: RepositoryImpl
        ): Repository
    }
}