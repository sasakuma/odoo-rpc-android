package com.serpentcs.odoorpc.core

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import com.serpentcs.odoorpc.core.entities.HttpError
import com.serpentcs.odoorpc.core.entities.authenticate.Authenticate
import com.serpentcs.odoorpc.core.entities.authenticate.AuthenticateReqBody
import com.serpentcs.odoorpc.core.entities.authenticate.Params
import com.serpentcs.odoorpc.core.entities.list.List
import com.serpentcs.odoorpc.core.entities.list.ListReqBody
import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfo
import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfoReqBody
import com.serpentcs.odoorpc.core.utils.Retrofit2Helper
import com.serpentcs.odoorpc.core.utils.logD
import com.serpentcs.odoorpc.core.web.database.list.ListRequest
import com.serpentcs.odoorpc.core.web.session.authenticate.AuthenticateRequest
import com.serpentcs.odoorpc.core.web.webclient.versionInfo.VersionInfoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Odoo {

    private val TAG = "Odoo"
    var protocol: Retrofit2Helper.Companion.Protocol = Retrofit2Helper.Companion.Protocol.HTTP
        set(value) {
            field = value
            retrofit2Helper.protocol = value
        }
    var host: String = "false"
        set(value) {
            field = value
            retrofit2Helper.host = host
        }
    var login: String = "false"
    var password: String = "false"
    var database: String = "false"
    val androidName: String
        get() = "$login[$database]"
    var serverVersion = "false"
    var timeZone = "false"

    val toBundle: Bundle
        get() = Bundle().apply {
            putString("protocol", protocol.ordinal.toString())
            putString("host", host)
            putString("login", login)
            putString("password", password)
            putString("database", database)
            putString("serverVersion", serverVersion)
            putString("timeZone", timeZone)
            putString("active", true.toString())
        }

    fun fromAccount(manager: AccountManager, account: Account) = OdooUser(
            manager.getUserData(account, "protocol").toInt(),
            manager.getUserData(account, "host"),
            manager.getUserData(account, "login"),
            manager.getUserData(account, "password"),
            manager.getUserData(account, "database"),
            manager.getUserData(account, "serverVersion"),
            manager.getUserData(account, "timeZone"),
            manager.getUserData(account, "active").toBoolean(),
            account
    )

    fun initFromUser(user: OdooUser) {
        protocol = when (user.protocol) {
            0 -> {
                Retrofit2Helper.Companion.Protocol.HTTP
            }
            else -> {
                Retrofit2Helper.Companion.Protocol.HTTPS
            }
        }
        host = user.host
        login = user.login
        password = user.password
        database = user.database
        serverVersion = user.serverVersion
        timeZone = user.timeZone
    }

    private val retrofit2Helper = Retrofit2Helper(
            protocol,
            host
    )
    private val retrofit
        get() = retrofit2Helper.retrofit

    private var jsonRpcId: String = "0"
        get() {
            field = (field.toInt() + 1).toString()
            if (mAuthenticate.isAuthenticated) {
                return "r" + field
            }
            return field
        }

    val supportedOdooVersions = listOf("10.0")
    private var mVersionInfo = VersionInfo()

    private var mAuthenticate = Authenticate()

    fun versionInfo(callback: (VersionInfo) -> Unit) {
        val request = retrofit.create(VersionInfoRequest::class.java)
        val requestBody = VersionInfoReqBody(id = jsonRpcId)
        val call = request.versionInfo(requestBody)
        call.enqueue(object : Callback<VersionInfo> {
            override fun onFailure(call: Call<VersionInfo>, t: Throwable) {
                logD(TAG, "versionInfo::onFailure: " + t.message)
                mVersionInfo = VersionInfo(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                ))
                callback(mVersionInfo)
            }

            override fun onResponse(call: Call<VersionInfo>, response: Response<VersionInfo>) {
                mVersionInfo = if (response.isSuccessful) {
                    // logD(TAG, "versionInfo::onResponse: Success " + response.body())
                    response.body()!!
                } else {
                    logD(TAG, "versionInfo::onResponse Error: " + response.errorBody()!!.string())
                    VersionInfo(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                }
                callback(mVersionInfo)
            }
        })
    }

    fun list(callback: (List) -> Unit) {
        val request = retrofit.create(ListRequest::class.java)
        val requestBody = ListReqBody(id = jsonRpcId)
        val call = request.list(requestBody)
        call.enqueue(object : Callback<List> {
            override fun onFailure(call: Call<List>, t: Throwable) {
                logD(TAG, "list::onFailure: " + t.message)
                callback(List(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                )))
            }

            override fun onResponse(call: Call<List>, response: Response<List>) {
                val list = if (response.isSuccessful) {
                    // logD(TAG, "list::onResponse: Success " + response.body())
                    response.body()!!
                } else {
                    logD(TAG, "list::onResponse Error: " + response.errorBody()!!.string())
                    List(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                }
                callback(list)
            }
        })
    }

    fun authenticate(callback: (Authenticate) -> Unit) {
        val request = retrofit.create(AuthenticateRequest::class.java)
        val requestBody = AuthenticateReqBody(id = jsonRpcId, params = Params(
                host, login, password, database
        ))
        val call = request.authenticate(requestBody)
        call.enqueue(object : Callback<Authenticate> {
            override fun onFailure(call: Call<Authenticate>, t: Throwable) {
                logD(TAG, "authenticate::onFailure: " + t.message)
                mAuthenticate = Authenticate(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                ))
                callback(mAuthenticate)
            }

            override fun onResponse(call: Call<Authenticate>, response: Response<Authenticate>) {
                mAuthenticate = if (response.isSuccessful) {
                    // logD(TAG, "authenticate::onResponse: Success " + response.body())
                    val authenticateBody = response.body()!!
                    serverVersion = authenticateBody.result.serverVersion
                    timeZone = authenticateBody.result.userContext.get("tz").asString
                    authenticateBody
                } else {
                    logD(TAG, "authenticate::onResponse Error: " + response.errorBody()!!.string())
                    Authenticate(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                }
                callback(mAuthenticate)
            }
        })
    }
}