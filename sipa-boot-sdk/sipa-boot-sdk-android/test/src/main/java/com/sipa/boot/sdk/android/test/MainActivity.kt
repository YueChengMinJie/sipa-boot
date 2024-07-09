package com.sipa.boot.sdk.android.test

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sipa.boot.sdk.android.SipaAndroid
import com.sipa.boot.sdk.android.logan.Logan
import com.sipa.boot.sdk.android.logan.LoganConfig
import com.sipa.boot.sdk.android.logan.SendLogDefaultRunnable
import com.sipa.boot.sdk.android.test.MainActivity.Companion.appVersion
import com.sipa.boot.sdk.android.test.MainActivity.Companion.buildVersion
import com.sipa.boot.sdk.android.test.ui.theme.SIPASDKAndroidTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLogan()
        initPackage()
        setContent {
            SIPASDKAndroidTheme {
                Greeting()
            }
        }
    }

    private fun initPackage() {
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(getPackageName(), 0)
            appVersion = pInfo.versionName
            buildVersion = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun initLogan() {
        // 存储的path，建议不要改，符合安卓规范
        val path = (applicationContext.getExternalFilesDir(null)!!.absolutePath + File.separator + "logan_files")
        // mmap缓存地址，建议不要改，符合安卓规范
        val cachePath = applicationContext.filesDir.absolutePath
        // 初始化
        val config: LoganConfig = LoganConfig.Builder()
            .setPath(path)
            .setCachePath(cachePath)
            .setEncryptKey16("0dxooe7yd57kxzdh".toByteArray()) // 写死 每个环境都一样
            .setEncryptIV16("3f5b7lppqyhi81tj".toByteArray()) // 写死 每个环境都一样
            .build()
        Logan.init(config)
        // 打印logan日志
        Logan.setDebug(true)
        // 观察者模式，观察clogan
        Logan.setOnLoganProtocolStatus { cmd, code -> Log.i("MainActivity", "clogan > cmd : $cmd | code : $code") }
    }

    companion object {
        var buildVersion = ""
        var appVersion = ""
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Greeting() {
    var ip by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(modifier = Modifier
            .width(Dp(200f))
            .height(Dp(50f)), onClick = { singleInput() }) {
            Text(text = "测试Logan单条写入")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(modifier = Modifier
            .width(Dp(200f))
            .height(Dp(50f)), onClick = { batchInput() }) {
            Text(text = "测试Logan批量写入")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Button(modifier = Modifier
                .width(Dp(160f))
                .height(Dp(50f)),
                onClick = { input(ip) }) {
                Text(text = "测试Logan上报")
            }

            Spacer(modifier = Modifier.width(10.dp))

            TextField(value = ip, onValueChange = { ip = it }, modifier = Modifier
                .width(200.dp), singleLine = true, label = { Text("请输入server的ip地址") })
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(modifier = Modifier
            .width(Dp(200f))
            .height(Dp(50f)), onClick = { defaultInput() }) {
            Text(text = "测试Logan默认上报方式")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(modifier = Modifier
            .width(Dp(200f))
            .height(Dp(50f)), onClick = { info = showAllLog() }) {
            Text(text = "查看所有日志文件信息")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = info)
    }
}

fun singleInput() {
    Logan.write("单条写入", 2)
}

fun batchInput() {
    object : Thread() {
        override fun run() {
            super.run()
            try {
                for (i in 0..8) {
                    Log.d("MainActivity", "times : $i")
                    Logan.write(i.toString(), 1)
                    sleep(5)
                }
                Log.d("MainActivity", "write log end")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }.start()
}

fun input(ip: String) {
    val dataFormat = SimpleDateFormat("yyyy-MM-dd")
    val d = dataFormat.format(Date(System.currentTimeMillis()))
    val dates = arrayOfNulls<String>(1)
    dates[0] = d

    val sendLogRunnable = SendLogDefaultRunnable()
    if (!TextUtils.isEmpty(ip)) {
        sendLogRunnable.uploadLogUrl = "http://$ip/logan/write"
    }
    Logan.send(dates, sendLogRunnable)
}

fun defaultInput() {
    // 地址写的网关地址，这个是dev的地址
    val url = "http://gateway-dev.sipa.com/base-service-server/logan/write"
    // 当天的日志
    val dataFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = dataFormat.format(Date(System.currentTimeMillis()))
    // 上报
    // deviceId = deviceNo 12位设备号
    Logan.send(url, date, SipaAndroid.APPLICATION_ID_TODO, SipaAndroid.REQUEST_FROM_TODO, "A3HESRM1NLP4", buildVersion, appVersion) { statusCode, data ->
        // callback
        val resultData = if (data != null) String(data) else ""
        Log.d("MainActivity", "日志上传结果, http状态码: $statusCode, 详细: $resultData")
    }
}

fun showAllLog(): String {
    val map = Logan.getAllFilesInfo()
    if (map != null) {
        val info = StringBuilder()
        for (entry in map) {
            info.append("文件日期：").append(entry.key).append("  文件大小（bytes）：").append(entry.value).append("\n")
        }
        return info.toString()
    }
    return ""
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SIPASDKAndroidTheme {
        Greeting()
    }
}