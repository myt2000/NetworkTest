package com.brooks.newworktest

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: java.lang.Exception)
}