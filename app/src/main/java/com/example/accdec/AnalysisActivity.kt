package com.example.accdec

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.accdec.CameraActivity.Companion.IMAGE_URL_KEY
import com.example.accdec.CameraActivity.Companion.LATITUDE_KEY
import com.example.accdec.CameraActivity.Companion.LONGITUDE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_analysis.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class AnalysisActivity : AppCompatActivity() {

    private lateinit var retroClient: Retrofit
    private var psNumber:Int? = null
    private var hosNumber:Int? = null
    private val PERMISSION_CODE = 1000;

    val BASE_URL:String = "https://api.myjson.com/bins/18x78c/";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        title = "Analysis "
        instantiateRetro()
        val imageUrl = intent.getStringExtra(IMAGE_URL_KEY)!!
        val lat = intent.getDoubleExtra(LATITUDE_KEY,23.81)
        val long = intent.getDoubleExtra(LONGITUDE_KEY,86.43)

        getResponse(imageUrl, lat, long)

        psNum.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + psNumber.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_DENIED ){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CALL_PHONE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    startActivity(callIntent)
                }
            }
            else{
                //system os is < marshmallow
                startActivity(callIntent)
            }



        }

        hosNum.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + hosNumber.toString())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_DENIED ){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CALL_PHONE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    startActivity(callIntent)
                }
            }
            else{
                //system os is < marshmallow
                startActivity(callIntent)
            }
        }
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

    fun getResponse(imageUrl: String, lat: Double, long: Double) {

        retroClient.create(AccidentPostApi::class.java).getAccidentResponse(Accident(imageUrl,lat,long))
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    status.text = it.status
                    psdesc.text = it.policeStation
                    psNum.text = it.psNum.toString()
                    hosDesc.text = it.hospital
                    hosNum.text = it.hNum.toString()
                    psRec.text = it.psRec
                    hosRec.text = it.hRec

                    psNumber = it.psNum
                    hosNumber = it.hNum

                },
                onError = {
//                    Toast.makeText(this,"Unable to Fetch Response, Try Again",Toast.LENGTH_LONG).show()
//                    onBackPressed()
                }
            )
    }
}
