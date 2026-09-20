package com.example.signalboostmonitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.widget.*
import android.graphics.Color
import android.view.Gravity

class MainActivity : android.app.Activity() {
    private lateinit var tm: TelephonyManager
    private lateinit var signalText: TextView
    private lateinit var typeText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32,32,32,32)
            setBackgroundColor(Color.rgb(16,17,20))
        }

        fun tv(text:String, size:Float):TextView = TextView(this).apply {
            this.text=text; textSize=size; setTextColor(Color.WHITE)
            gravity=Gravity.CENTER
            setPadding(0,16,0,16)
        }

        root.addView(tv("📡 Signal Boost Monitor", 26f))
        signalText=tv("Signal: -- dBm", 34f); root.addView(signalText)
        typeText=tv("Network: checking…", 20f); root.addView(typeText)
        statusText=tv("Status: checking…", 18f); root.addView(statusText)
        root.addView(tv("Note: This app monitors signal; it cannot physically amplify the antenna signal.", 14f))
        setContentView(root)

        tm=getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE), 10)

        register()
    }

    private fun register() {
        @Suppress("DEPRECATION")
        tm.listen(object: PhoneStateListener() {
            override fun onSignalStrengthsChanged(s: SignalStrength) {
                super.onSignalStrengthsChanged(s)
                val dbm = if (android.os.Build.VERSION.SDK_INT >= 23) s.level else -1
                signalText.text = "Signal level: $dbm / 4"
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)

        val type = when (tm.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA -> "3G"
            TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
            else -> "Unknown"
        }
        typeText.text="Network: $type"
        statusText.text="Status: Connected"
    }
}
