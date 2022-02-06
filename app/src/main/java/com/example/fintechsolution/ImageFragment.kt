package com.example.fintechselection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.fintechsolution.R
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.create
import timber.log.Timber
import tools.Api
import tools.BASE_URL
import java.io.IOException
import com.bumptech.glide.request.RequestOptions
import tools.DESCRIPTION_SHARED_PREFS
import tools.MY_SHARED_PREFS


@GlideModule
class MyAppGlideModule : AppGlideModule()


class ImageFragment: Fragment() {
    var coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var ivGifImage: ImageView? = null
    private var tvDescription: TextView? = null
    private var tvError: TextView? = null
    private var currentURL: String? = null
    private var currentDescription: String? = null
    private var totalCount: Int? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        val errorText = when (throwable) {
            is IOException, is HttpException -> "Internet connection error. Try next time"
            is SerializationException -> "Json parsing errors"
            else -> "Unexpected error happened. Please check logs"
        }
        Timber.d("log_error_: $errorText")
        tvError?.text = errorText
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_image, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivGifImage = view.findViewById(R.id.ivPicture)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvError = view.findViewById(R.id.tvError)

        val preferences = this.activity!!.getSharedPreferences(MY_SHARED_PREFS, MODE_PRIVATE)
        val descriptionPrefs = this.activity!!.getSharedPreferences(DESCRIPTION_SHARED_PREFS, MODE_PRIVATE)

        totalCount = arguments?.getInt("NUMBER")

        val url = preferences.getString(totalCount.toString(), null)
        val description = descriptionPrefs.getString(totalCount.toString(), null)
        if (url == null || description == null) {
            coroutineScope.launch(exceptionHandler) {
                loadImage()
                preferences.edit().putString(totalCount.toString(), currentURL).apply()
                descriptionPrefs.edit().putString(totalCount.toString(), currentDescription).apply()
                Timber.d("log_ shared prefs: ${preferences.getString(totalCount.toString(), null)}")
            }
        } else {
            coroutineScope.launch(exceptionHandler) {
                showImage(url, description)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        coroutineScope.cancel("Time to stop")
    }

    override fun onDestroy() {
        ivGifImage = null
        tvDescription = null
        super.onDestroy()
    }

    private suspend fun loadImage() {
        Timber.d("log_: start")
        Timber.d("log_: before retrofit")
        val image = RetrofitModule.randomImageApi.getRandomImageInfo()
        Timber.d("log_description: ${image.description}")
        currentURL = image.gifURL
        currentDescription = image.description
        showImage(currentURL!!, currentDescription!!)
        Timber.d("log_: made set up image")
    }


    @SuppressLint("CheckResult")
    private suspend fun showImage(gifURL: String, description: String) = withContext(Dispatchers.Main) {
        tvDescription?.text = description
        val options = RequestOptions()
        options.centerCrop()
        GlideApp.with(ivGifImage!!.context)
            .load(gifURL)
            .placeholder(R.drawable.default_picture)
            .apply(options)
            .into(ivGifImage!!)
    }

    companion object {
        fun newInstance(number: Int) : ImageFragment {
            val args = Bundle()
            args.putInt("NUMBER", number)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}


private object RetrofitModule {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val randomImageApi: Api = retrofit.create()
}