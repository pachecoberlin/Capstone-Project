package de.pacheco.bakingapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class RecipesViewModel(application: Application) : AndroidViewModel(application) {
    val recipes: MutableLiveData<List<Recipe>> by lazy {
        val liveData = MutableLiveData<List<Recipe>>()
        getRecipes(liveData)
        liveData
    }

    private fun getRecipes(liveData: MutableLiveData<List<Recipe>>) {
        SERVICE.getRecipes().enqueue(object : Callback<List<Recipe?>?> {
            override fun onResponse(call: Call<List<Recipe?>?>, response: Response<List<Recipe?>?>) {
                val recipes = response.body()
                recipes?.apply { liveData.setValue(recipes as List<Recipe>) }
            }

            override fun onFailure(call: Call<List<Recipe?>?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    interface GetRecipesAPI {
        @GET("/topher/2017/May/59121517_baking/baking.json")
        fun getRecipes(): Call<List<Recipe?>?>
    }

    companion object {
        private const val BASE_URL = "https://d17h27t6h515a5.cloudfront.net"
        val SERVICE = recipeService
        private val recipeService: GetRecipesAPI
            get() {
                val restAdapter = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                return restAdapter.create(GetRecipesAPI::class.java)
            }
    }
}