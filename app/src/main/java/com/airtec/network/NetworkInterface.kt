package com.airtec.network

import com.airtec.model.postdelivery.DeliveryNoteDetailsData
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface NetworkInterface {

    @GET("login/get")  //username=driver1&password=123
    fun login(@Query("username") username: String,
                      @Query("password") password: String
                      ):
            Observable<String>

    @GET("TripDetails/GetTripDetails")
    fun getTripDetails(@Query("apiParameters") apiParameters: String,
              @Query("roleID") roleID: String
    ):
            Observable<String>

    @GET("DeliveryDetails/GetDeliveryDetailsByTripNumber")
    fun getDeliveryDetails(@Query("apiParameters") apiParameters: String
    ):
            Observable<String>

    @GET("TripDetails/GetTripStatus")
    fun getTripStatus(@Query("apiParameters") apiParameters: String
    ):
            Observable<String>

    @GET("Customer/GetTripCustomersByTripNumber")
    fun getCustomerDetails(@Query("apiParameters") apiParameters: String
    ):
            Observable<String>

    @GET("DeliveryDetails/GetSummaryDeliveryDetailsByTripNumber")
    fun getSummaryDeliveryDetails(@Query("apiParameters") apiParameters: String
    ):
            Observable<String>

    @POST("DeliveryDetails/GetPostDeliveryNoteDetailsTransaction")
    fun postDeliveryNoteDetails(@Body apiParameters: DeliveryNoteDetailsData
    ):
            Observable<String>


    companion object {
        fun create(): NetworkInterface {
            val interceptor = HttpLoggingInterceptor()

            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create()).client(okHttpClient)
                .baseUrl("http://149.202.70.54:8044/api/")
                .build()

            return retrofit.create(NetworkInterface::class.java)
        }
    }



}