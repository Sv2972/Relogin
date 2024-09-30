//package com.example.relogin
//
//import android.bluetooth.BluetoothAdapter
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class BluetoothReceiver : BroadcastReceiver(){
//    override fun onReceive(context: Context? , intent: Intent?) {
//        val action = intent?.action
//        if (action == BluetoothAdapter.ACTION_STATE_CHANGED)
//            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
//                BluetoothAdapter.STATE_ON -> {
//                    Log.d("message1", "State On")
//                }
//
//                BluetoothAdapter.STATE_OFF -> {
//                    Log.d("message1", "State OFF")
//                }
//
//                BluetoothAdapter.STATE_TURNING_ON -> {
//                    Log.d("message1", "Turning On")
//                }
//                BluetoothAdapter.STATE_TURNING_OFF -> {
//                    Log.d("message1", "Turning OFF")
//                }
//            }
//    }
//
//}

package com.example.relogin

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_ON -> {
                    Log.d("BluetoothReceiver", "Bluetooth is ON")
                }

                BluetoothAdapter.STATE_OFF -> {
                    Log.d("BluetoothReceiver", "Bluetooth is OFF")
                }

                BluetoothAdapter.STATE_TURNING_ON -> {
                    Log.d("BluetoothReceiver", "Bluetooth is TURNING ON")
                }

                BluetoothAdapter.STATE_TURNING_OFF -> {
                    Log.d("BluetoothReceiver", "Bluetooth is TURNING OFF")
                }
            }
        }
    }
}
