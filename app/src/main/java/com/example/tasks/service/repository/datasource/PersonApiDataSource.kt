package com.example.tasks.service.repository.datasource

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.repository.BaseRepository
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.remote.PersonService
import com.example.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonApiDataSource(val context: Context) : BaseRepository(context), PersonRepository {

    private val mRemote = RetrofitClient.createSerive(PersonService::class.java)

    override fun login(email: String, password: String, listener: APIListener<HeaderModel>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val call: Call<HeaderModel> = mRemote.login(email, password)

        // Assíncrona
        call.enqueue(object : Callback<HeaderModel> {
            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {

                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    val validation =
                        Gson().fromJson(response.errorBody()!!.string(), String::class.java)
                    listener.onFailure(validation)
                } else {
                    response.body()?.let { listener.onSucess(it) }
                }
            }

            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

        })
    }

    override fun create(
        name: String,
        email: String,
        password: String,
        listener: APIListener<HeaderModel>
    ) {
        val call: Call<HeaderModel> = mRemote.create(name, email, password, true)

        // Assíncrona
        call.enqueue(object : Callback<HeaderModel> {
            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {

                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    val validation =
                        Gson().fromJson(response.errorBody()!!.string(), String::class.java)
                    listener.onFailure(validation)
                } else {
                    response.body()?.let { listener.onSucess(it) }
                }
            }

            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

        })
    }
}