package com.serpentcs.odoorpc.core.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit2Helper @JvmOverloads constructor(
        _protocol: Protocol = Protocol.HTTP,
        _host: String = "false"
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
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return _retrofit!!
        }
}
