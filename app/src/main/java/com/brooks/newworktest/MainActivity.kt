package com.brooks.newworktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.net.CacheResponse
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.SAXParserFactory
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendRequestBtn.setOnClickListener {
            sendRequestWithOkHttp()
            HttpUtil.sendOkHttpRequest(address = "http://192.168.16.30/get_data.json", object: Callback{
                override fun onResponse(call: Call, response: Response) {
                    // 得到服务器返回的具体内容
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        parseJSONWithGSON(responseData)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // 对异常情况进行处理
                }
            })

//            HttpUtil.sendHttpRequest(address= "http://192.168.16.30/get_data.json", object: HttpCallbackListener {
//                override fun onFinish(response: String) {
//                    // 得到服务器返回的具体内容
//                }
//                override fun onError(e: Exception) {
//                    // 在这里对异常情况进行处理
//            }
//            })
        }
    }


    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://192.168.16.30/get_data.json")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    parseJSONWithGSON(responseData)
                    showRespones(responseData)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseJSONWithGSON(jsonData: String) {
        val gson = Gson()
        val typeOf = object: TypeToken<List<App>>() {}.type
        val appList = gson.fromJson<List<App>>(jsonData, typeOf)
        for (app in appList) {
            Log.d("GSON数据解析", "id is ${app.id}")
            Log.d("GSON数据解析", "name is ${app.name}")
            Log.d("GSON数据解析", "version is ${app.version}")
        }
    }


    private fun parseJSONWithObject(jsonData: String) {
        try {
            val jsonArray = JSONArray(jsonData)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val name = jsonObject.getString("name")
                val version = jsonObject.getString("version")
                Log.d("JSON数据解析", "id is $id")
                Log.d("JSON数据解析", "name is $name")
                Log.d("JSON数据解析", "version is $version")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun parseXMLwITHSAX(xmlData: String) {
        try {
            val factory = SAXParserFactory.newInstance()
            val xmlReader = factory.newSAXParser().xmlReader
            val handler = ContentHandler()
            // 将ContentHandler的实例设置到xmlReader中
            xmlReader.contentHandler = handler
            xmlReader.parse(InputSource(StringReader(xmlData)))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showRespones(response: String) {
        runOnUiThread {
            // 在这里进行UI操作，将结果显示到界面上
            responseText.text = response
        }
    }

    private fun parseXMLWithPull(xmlData: String) {
        try {
            val factory = XmlPullParserFactory.newInstance()
            val xmlPullParser = factory.newPullParser()
            xmlPullParser.setInput(StringReader(xmlData))
            var eventType = xmlPullParser.eventType
            var id = ""
            var name = ""
            var version = ""
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val nodeName = xmlPullParser.name
                when (eventType) {
                    // 开始解析某个节点
                    XmlPullParser.START_TAG -> {
                        when (nodeName) {
                            "id" -> id = xmlPullParser.nextText()
                            "name" -> name = xmlPullParser.nextText()
                            "version" -> version = xmlPullParser.nextText()
                        }
                    }
                    // 完成解析某个节点
                    XmlPullParser.END_TAG -> {
                        if ("app" == nodeName) {
                            Log.d("xml解析", "id is $id ")
                            Log.d("xml解析", "name is $name ")
                            Log.d("xml解析", "version is $version ")
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}