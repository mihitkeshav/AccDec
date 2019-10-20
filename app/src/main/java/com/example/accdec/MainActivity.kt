package com.example.accdec

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_analysis.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


    private val PERMISSION_CODE: Int = 63
    private lateinit var retroClient: Retrofit
    val BASE_URL:String = "https://api.myjson.com/bins/18x78c/";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        instantiateRetro()
        getResponse()

    }
    private fun instantiateRetro() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        retroClient = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient).build()
    }

    fun getResponse() {

        retroClient.create(AccidentApi::class.java).getIssuesListResponse()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    status.text = it.status
                    psdesc.text = it.policeStation
                    psNum.text = it.psNum.toString()
                    hosDesc.text = it.hospital
                    hosNum.text = it.hNum.toString()
                    psRec.text = it.psRec.toString()
                    hosRec.text = it.hRec.toString()

                },
                onError = {
                    Toast.makeText(this,"Unable to Fetch Response, Try Again",Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
            )
    }



}
