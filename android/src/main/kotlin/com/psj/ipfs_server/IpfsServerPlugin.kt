package com.psj.ipfs_server

import android.content.Context
import androidx.annotation.NonNull
import com.psj.ipfs_server.services.CmdIntentService
import com.psj.ipfs_server.utils.IpfsExec
import com.psj.ipfs_server.utils.LogUtils

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.ipfsbox.library.IpfsBox
import org.ipfsbox.library.entity.Add
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class IpfsServerPlugin : FlutterPlugin, MethodCallHandler {

    private lateinit var channel: MethodChannel

    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

        context = flutterPluginBinding.applicationContext;
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ipfs_server")
        channel.setMethodCallHandler(this)

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

        if (call.method == "getPlatformVersion") {

            result.success("Android ${android.os.Build.VERSION.RELEASE}")

        } else if ((call.method == "nodeList")) {

            try {
                val nodeList = IpfsExec.getInstance(context).nodeList()
                result.success(nodeList)
            } catch (e: Exception) {
                result.success(null)
                e.printStackTrace()
            }

        } else if ((call.method == "removeNode")) {

            try {
                val nodeUrl = call.arguments as String
                IpfsExec.getInstance(context).rmNode(nodeUrl)
                result.success(true)
            } catch (e: Exception) {
                result.success(false)
                e.printStackTrace()
            }

        } else if ((call.method == "addNode")) {

            try {
                val nodeUrl = call.arguments as String
                IpfsExec.getInstance(context).addNode(nodeUrl)
                result.success(true)
            } catch (e: Exception) {
                result.success(false)
                e.printStackTrace()
            }

        } else if ((call.method == "uploadFileToIPFS")) {

            val filePath = call.arguments as String

            val ipfsBox = IpfsBox()

            ipfsBox.add(object : Callback<Add?> {

                override fun onResponse(call: Call<Add?>, response: Response<Add?>) {
                    val add = response.body()
                    if (add != null) {
                        val hashCode = add.hash

                        result.success(hashCode)

                    } else {
                        try {
                            result.success(null)
                            LogUtils.e(response.errorBody()!!.string())
                        } catch (e: IOException) {
                            e.printStackTrace()
                            result.success(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Add?>, t: Throwable) {
                    LogUtils.e(t.message)
                    result.success(null)
                }

            }, filePath)

        } else if ((call.method == "getUrlByCID")) {

            val cid = call.arguments as String
            result.success("http://127.0.0.1:8080/ipfs/$cid")

        } else if ((call.method == "closeIPFS")) {

            try {
                CmdIntentService.startActionShutdown(context)
                result.success(true)
            } catch (e: Exception) {
                result.success(false)
                e.printStackTrace()
            }

        } else if ((call.method == "openIPFS")) {

            try {
                CmdIntentService.startActionDaemon(context)
                result.success(true)
            } catch (e: Exception) {
                result.success(false)
                e.printStackTrace()
            }

        }  else if ((call.method == "getCurrentIdInfo")) {

            try {
                val nodeInfo = IpfsExec.getInstance(context).nodeIdInfo()
                result.success(nodeInfo)
            } catch (e: Exception) {
                result.success(null)
                e.printStackTrace()
            }

        }else {
            result.notImplemented()
        }

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

}
