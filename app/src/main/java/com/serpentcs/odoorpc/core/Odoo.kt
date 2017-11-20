package com.serpentcs.odoorpc.core

import android.accounts.Account
import android.accounts.AccountManager
import com.google.gson.JsonObject
import com.serpentcs.odoorpc.core.entities.HttpError
import com.serpentcs.odoorpc.core.entities.authenticate.Authenticate
import com.serpentcs.odoorpc.core.entities.authenticate.AuthenticateParams
import com.serpentcs.odoorpc.core.entities.authenticate.AuthenticateReqBody
import com.serpentcs.odoorpc.core.entities.list.ListReqBody
import com.serpentcs.odoorpc.core.entities.searchRead.SearchRead
import com.serpentcs.odoorpc.core.entities.searchRead.SearchReadParams
import com.serpentcs.odoorpc.core.entities.searchRead.SearchReadReqBody
import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfo
import com.serpentcs.odoorpc.core.entities.versionInfo.VersionInfoReqBody
import com.serpentcs.odoorpc.core.utils.OdooList
import com.serpentcs.odoorpc.core.utils.Retrofit2Helper
import com.serpentcs.odoorpc.core.utils.logW
import com.serpentcs.odoorpc.core.utils.toJsonObject
import com.serpentcs.odoorpc.core.web.database.list.ListRequest
import com.serpentcs.odoorpc.core.web.dataset.searchRead.SearchReadRequest
import com.serpentcs.odoorpc.core.web.session.authenticate.AuthenticateRequest
import com.serpentcs.odoorpc.core.web.webclient.versionInfo.VersionInfoRequest
import okhttp3.Cookie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Odoo {

    val TAG = "Odoo"

    var protocol: Retrofit2Helper.Companion.Protocol = Retrofit2Helper.Companion.Protocol.HTTP
        set(value) {
            field = value
            retrofit2Helper.protocol = value
        }
    var host: String = String()
        set(value) {
            field = value
            retrofit2Helper.host = value
        }

    var user: OdooUser = OdooUser()
        set(value) {
            field = value
            protocol = value.protocol
            host = value.host
        }

    fun fromAccount(manager: AccountManager, account: Account) = OdooUser(
            Retrofit2Helper.Companion.Protocol.valueOf(
                    manager.getUserData(account, "protocol")
            ),
            manager.getUserData(account, "host"),
            manager.getUserData(account, "login"),
            manager.getUserData(account, "password"),
            manager.getUserData(account, "database"),
            manager.getUserData(account, "serverVersion"),
            manager.getUserData(account, "isAdmin").toBoolean(),
            manager.getUserData(account, "id").toInt(),
            manager.getUserData(account, "name"),
            manager.getUserData(account, "imageSmall"),
            manager.getUserData(account, "context").toJsonObject(),
            manager.getUserData(account, "active").toBoolean(),
            account
    )

    private val retrofit2Helper = Retrofit2Helper(
            protocol,
            host
    )
    val retrofit
        get() = retrofit2Helper.retrofit

    var jsonRpcId: String = "0"
        get() {
            field = (field.toInt() + 1).toString()
            if (user.id > 0) {
                return "r" + field
            }
            return field
        }

    val supportedOdooVersions = listOf("10.0", "10.saas~16+e")

    inline fun versionInfo(crossinline callback: VersionInfo.() -> Unit) {
        val request = retrofit.create(VersionInfoRequest::class.java)
        val requestBody = VersionInfoReqBody(id = jsonRpcId)
        val call = request.versionInfo(requestBody)
        call.enqueue(object : Callback<VersionInfo> {
            override fun onFailure(call: Call<VersionInfo>, t: Throwable) {
                logW(TAG, "versionInfo::onFailure: " + t.message)
                callback(VersionInfo(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                )))
            }

            override fun onResponse(call: Call<VersionInfo>, response: Response<VersionInfo>) {
                callback(if (response.isSuccessful) {
                    // logI(TAG, "versionInfo::onResponse: Success " + response.body())
                    response.body()!!
                } else {
                    logW(TAG, "versionInfo::onResponse Error: " + response.errorBody()!!.string())
                    VersionInfo(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                })
            }
        })
    }

    inline fun list(crossinline callback: OdooList.() -> Unit) {
        val request = retrofit.create(ListRequest::class.java)
        val requestBody = ListReqBody(id = jsonRpcId)
        val call = request.list(requestBody)
        call.enqueue(object : Callback<OdooList> {
            override fun onFailure(call: Call<OdooList>, t: Throwable) {
                logW(TAG, "list::onFailure: " + t.message)
                callback(OdooList(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                )))
            }

            override fun onResponse(call: Call<OdooList>, response: Response<OdooList>) {
                callback(if (response.isSuccessful) {
                    // logI(TAG, "list::onResponse: Success " + response.body())
                    response.body()!!
                } else {
                    logW(TAG, "list::onResponse Error: " + response.errorBody()!!.string())
                    OdooList(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                })
            }
        })
    }

    val pendingAuthenticateCallbacks = mutableListOf<(Authenticate) -> Unit>()
    val pendingAuthenticateCookies: MutableList<Cookie> = mutableListOf()

    fun authenticate(
            login: String, password: String, database: String,
            quick: Boolean = false, callback: Authenticate.() -> Unit
    ) {
        if (pendingAuthenticateCallbacks.isEmpty()) {
            pendingAuthenticateCallbacks += callback
            val request = retrofit.create(AuthenticateRequest::class.java)
            val requestBody = AuthenticateReqBody(id = jsonRpcId, params = AuthenticateParams(
                    host, login, password, database
            ))
            val call = request.authenticate(requestBody)
            call.enqueue(object : Callback<Authenticate> {
                override fun onFailure(call: Call<Authenticate>, t: Throwable) {
                    logW(TAG, "authenticate::onFailure: " + t.message)
                    (pendingAuthenticateCallbacks.size - 1 downTo 0)
                            .map { pendingAuthenticateCallbacks.removeAt(it) }
                            .forEach {
                                it(Authenticate(httpError = HttpError(
                                        Int.MAX_VALUE,
                                        t.message!!
                                )))
                            }
                }

                override fun onResponse(call: Call<Authenticate>, response: Response<Authenticate>) {
                    if (response.isSuccessful) {
                        // logI(TAG, "authenticate::onResponse: Success " + response.body())
                        val authenticateBody = response.body()!!
                        authenticateBody.result.password = password
                        if (!quick && authenticateBody.isSuccessful) {
                            searchRead(
                                    "res.users", listOf("image"),
                                    listOf(listOf("id", "=", authenticateBody.result.uid)),
                                    0, 0, "id DESC", authenticateBody.result.userContext
                            ) {
                                if (isSuccessful) {
                                    if (result.records.size() > 0) {
                                        val row = result.records[0].asJsonObject
                                        authenticateBody.result.imageSmall =
                                                row.get("image").asString
                                    }
                                }
                                (pendingAuthenticateCallbacks.size - 1 downTo 0)
                                        .map { pendingAuthenticateCallbacks.removeAt(it) }
                                        .forEach {
                                            it(authenticateBody)
                                        }
                            }
                        } else {
                            (pendingAuthenticateCallbacks.size - 1 downTo 0)
                                    .map { pendingAuthenticateCallbacks.removeAt(it) }
                                    .forEach {
                                        it(authenticateBody)
                                    }
                        }
                    } else {
                        logW(TAG, "authenticate::onResponse Error: " + response.errorBody()!!.string())
                        (pendingAuthenticateCallbacks.size - 1 downTo 0)
                                .map { pendingAuthenticateCallbacks.removeAt(it) }
                                .forEach {
                                    it(Authenticate(httpError = HttpError(
                                            response.code(),
                                            response.errorBody()!!.string()
                                    )))
                                }
                    }
                }
            })
        } else {
            pendingAuthenticateCallbacks += callback
        }
    }

    inline fun searchRead(
            model: String,
            fields: List<String> = listOf(),
            domain: List<List<Any>> = listOf(),
            offset: Int = 0,
            limit: Int = 0,
            sort: String = String(),
            context: JsonObject = user.context,
            crossinline callback: SearchRead.() -> Unit
    ) {
        val request = retrofit.create(SearchReadRequest::class.java)
        val requestBody = SearchReadReqBody(id = jsonRpcId, params = SearchReadParams(
                model, fields, domain, offset, limit, sort, context
        ))
        val call = request.searchRead(requestBody)
        call.enqueue(object : Callback<SearchRead> {
            override fun onFailure(call: Call<SearchRead>, t: Throwable) {
                logW(TAG, "searchRead::onFailure: " + t.message)
                callback(SearchRead(httpError = HttpError(
                        Int.MAX_VALUE,
                        t.message!!
                )))
            }

            override fun onResponse(call: Call<SearchRead>, response: Response<SearchRead>) {
                callback(if (response.isSuccessful) {
                    // logI(TAG, "searchRead::onResponse: Success " + response.body())
                    response.body()!!
                } else {
                    logW(TAG, "searchRead::onResponse Error: " + response.errorBody()!!.string())
                    SearchRead(httpError = HttpError(
                            response.code(),
                            response.errorBody()!!.string()
                    ))
                })
            }
        })
    }
}
