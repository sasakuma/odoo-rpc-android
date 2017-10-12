package com.serpentcs.odoorpc.core.utils

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit2Helper(
        _protocol: Protocol,
        _host: String
) {
    companion object {
        @JvmField
        val TAG = "Retrofit2Helper"

        enum class Protocol {
            HTTP, HTTPS
        }
    }

    var protocol: Protocol = _protocol
        set(value) {
            field = value
            _retrofit = null
        }

    var host: String = _host
        set(value) {
            field = value
            _retrofit = null
        }

    private var _retrofit: Retrofit? = null

    val retrofit: Retrofit
        get() {
            if (_retrofit == null) {
                _retrofit = Retrofit.Builder()
                        .baseUrl(when (protocol) {
                            Protocol.HTTP -> {
                                "http://"
                            }
                            Protocol.HTTPS -> {
                                "https://"
                            }
                        } + host)
                        .client(OkHttpClient().newBuilder().cookieJar(object : CookieJar {

                            private var cookies: MutableList<Cookie>? = mutableListOf()

                            override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                                if (url.toString().contains("/web/session/authenticate")) {
                                    this.cookies = cookies
                                }
                            }

                            override fun loadForRequest(url: HttpUrl?): MutableList<Cookie>? =
                                    cookies
                        }).build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return _retrofit!!
        }
}
