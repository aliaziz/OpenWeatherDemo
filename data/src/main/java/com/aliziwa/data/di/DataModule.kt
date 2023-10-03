package com.aliziwa.data.di

import android.content.Context
import android.preference.PreferenceManager
import com.aliziwa.data.repository.OpenWeatherApiRepoImpl
import com.aliziwa.data.repository.OpenWeatherDataRepoImpl
import com.aliziwa.data.repository.local.storage.WeatherProtoDataStoreImpl
import com.aliziwa.data.repository.local.storage.WeatherStorage
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherDataService
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherGeoService
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import com.aliziwa.domain.repository.OpenWeatherDataRepository
import com.aliziwa.domain.usecase.GetLastSearchUseCase
import com.aliziwa.domain.usecase.GetLastSearchUseCaseImpl
import com.aliziwa.domain.usecase.GetWeatherByCityNameUseCase
import com.aliziwa.domain.usecase.GetWeatherByCityNameUseCaseImpl
import com.aliziwa.domain.usecase.GetWeatherByLatLonUseCase
import com.aliziwa.domain.usecase.GetWeatherByLatLonUseCaseImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provides [Moshi] object. Moshi and it's adapters help autogenerate adapters as well as Serialize and
     * Deserialize responses and requests from/to the Openweather api
     */
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides the retrofit object for the making weather data requests
     *
     * @param - moshi
     * @param - okHttpClient
     *
     * @return [Retrofit]
     */
    @RetrofitGeoService
    @Provides
    fun provideGeoServiceRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RetrofitOpenWeatherGeoService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides the retrofit object for the making Geocode requests
     *
     * @param - moshi
     * @param - okHttpClient
     *
     * @return - [Retrofit]
     */
    @RetrofitDataService
    @Provides
    fun provideDataServiceRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RetrofitOpenWeatherDataService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides [OkHttpClient] object that intercepts each query to append an appid
     */
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient().newBuilder()
            .addInterceptor(QueryInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofitOpenWeatherDataService(@RetrofitDataService retrofit: Retrofit) =
        retrofit.create(RetrofitOpenWeatherDataService::class.java)

    @Provides
    fun provideRetrofitOpenWeatherGeoService(@RetrofitGeoService retrofit: Retrofit) =
        retrofit.create(RetrofitOpenWeatherGeoService::class.java)

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class OpenWeatherRepoModule {
    @Singleton
    @Binds
    abstract fun bindOpenWeatherRepo(repoImpl: OpenWeatherApiRepoImpl): OpenWeatherApiRepository

    @Singleton
    @Binds
    abstract fun bindOpenWeatherDataRepo(dataRepository: OpenWeatherDataRepoImpl): OpenWeatherDataRepository

    @Binds
    abstract fun bindGetGeoForCityUseCase(useCaseImpl: GetWeatherByCityNameUseCaseImpl): GetWeatherByCityNameUseCase

    @Binds
    abstract fun bindGetWeatherByLatLonUseCase(useCaseImpl: GetWeatherByLatLonUseCaseImpl): GetWeatherByLatLonUseCase

    @Binds
    abstract fun bindGetLastSearchUseCase(useCaseImpl: GetLastSearchUseCaseImpl): GetLastSearchUseCase

    @Binds
    @Singleton
    abstract fun bindWeatherStorageApi(storageImpl: WeatherProtoDataStoreImpl): WeatherStorage<Weather>
}

/**
 * A convenience interceptor to add the Open weather appid Key to all requests by default.
 */
internal class QueryInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Get current request
        val request = chain.request()

        //Get current api key
        val apiKey = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("api_key", "")

        //Cancel request on no app id key
        if (apiKey.isNullOrEmpty())
            return Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(403)
                .message("Provide appid")
                .body(ResponseBody.create(null, "Provide appid in settings"))
                .build()

        //Build new url from current request's URL, adding the appid
        val url =
            request.url.newBuilder()
                .addQueryParameter("appid", apiKey)
                .addQueryParameter("units", "imperial")
                .build()

        //Proceed making the api call with the new url
        return chain.proceed(request.newBuilder().url(url).build())
    }
}

/**
 * A [RetrofitOpenWeatherDataService] qualifier to identify what [Retrofit] object is returned
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitGeoService

/**
 * A [RetrofitOpenWeatherDataService] qualifier to identify what [Retrofit] object is returned
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitDataService
