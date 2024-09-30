//package com.example.relogin
//
//import android.Manifest
//import android.bluetooth.*
//import android.bluetooth.le.ScanCallback
//import android.bluetooth.le.ScanResult
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import java.util.UUID
//
//class MainActivity2 : AppCompatActivity() {
//
//    private lateinit var bluetoothAdapter: BluetoothAdapter
//    private var bluetoothGatt: BluetoothGatt? = null
//    private val REQUEST_ENABLE_BT = 1
//    private val REQUEST_BLUETOOTH_PERMISSION = 101
//    private val DEVICE_ADDRESS = "CB:F6:AA:FB:84:F3" // Replace with your BLE Device's MAC Address
//    private val TARGET_SERVICE_UUID: UUID = UUID.fromString("ef680400-9bfe-4933-9b10-25ffa9740042") // Replace with your BLE device's service UUID
//    private val TARGET_CHARACTERISTIC_UUID: UUID = UUID.fromString("ef680440-9bfe-4933-9b10-25ffa9740042") // Replace with your characteristic UUID
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize Bluetooth Manager and Adapter
//        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        bluetoothAdapter = bluetoothManager.adapter
//
//        // Check if Bluetooth is supported
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG)
//                .show()
//            finish()
//            return
//        }
//
//        // Check if Bluetooth is enabled
//        if (!bluetoothAdapter.isEnabled) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//
//        } else {
//            checkPermissionsAndStartScanning()
//        }
//
//        findViewById<Button>(R.id.BLE_start_button).setOnClickListener {
//            startReadingData()
//        }
//        findViewById<Button>(R.id.BLE_stop_button).setOnClickListener {
//            stopReadingData()
//        }
//    }
//
//
//
//
//
//    private fun startReadingData() {
//        val service = bluetoothGatt?.getService(TARGET_SERVICE_UUID)
//        if (service != null) {
//            val characteristic = service.getCharacteristic(TARGET_CHARACTERISTIC_UUID)
//            if (characteristic != null) {
//                Log.d("Bluetooth", "Starting to read data...")
//
//                // Enable notifications for the characteristic
//                bluetoothGatt?.setCharacteristicNotification(characteristic, true)
//
//                // Write to the descriptor to enable notifications at the GATT level
//                val descriptor = characteristic.getDescriptor(UUID.fromString("0x2902"))
//                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//                bluetoothGatt?.writeDescriptor(descriptor)
//            } else {
//                Log.d("Bluetooth", "Target characteristic not found")
//            }
//        } else {
//            Log.d("Bluetooth", "Target service not found")
//        }
//    }
//    private fun stopReadingData() {
//        val service = bluetoothGatt?.getService(TARGET_SERVICE_UUID)
//        if (service != null) {
//            val characteristic = service.getCharacteristic(TARGET_CHARACTERISTIC_UUID)
//            if (characteristic != null) {
//                Log.d("Bluetooth", "Stopping data reading...")
//
//                // Disable notifications for the characteristic
//                bluetoothGatt?.setCharacteristicNotification(characteristic, false)
//
//                // Write to the descriptor to disable notifications at the GATT level
//                val descriptor = characteristic.getDescriptor(UUID.fromString("0x2902"))
//                descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
//                bluetoothGatt?.writeDescriptor(descriptor)
//            }
//        }
//    }
//
//
//
//    // Check BLE permissions for Android 12+ and start scanning for BLE devices
//    private fun checkPermissionsAndStartScanning() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
//            != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.BLUETOOTH_SCAN,
//                    Manifest.permission.BLUETOOTH_CONNECT,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ),
//                REQUEST_BLUETOOTH_PERMISSION
//            )
//        } else {
//            startBLEScan()
//        }
//    }
//
//    // Start scanning for BLE devices
//    private fun startBLEScan() {
//        val scanner = bluetoothAdapter.bluetoothLeScanner
//        scanner.startScan(bleScanCallback)
//        Log.d("Bluetooth", "Scanning for BLE devices...")
//    }
//
//    // Callback when a BLE device is found during the scan
//    private val bleScanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            Log.d("Bluetooth", "Found device:")
//            val device: BluetoothDevice? = result.device
//            if (device != null && device.address == DEVICE_ADDRESS) {
//                Log.d("Bluetooth", "Found device: ${device.name}, ${device.address}")
//                // Stop scanning after the device is found
//
//                bluetoothAdapter.bluetoothLeScanner.stopScan(this)
//                connectToDevice(device)
//            }
//        }
//        override fun onScanFailed(errorCode: Int) {
//            Log.e("Bluetooth", "Scan failed with error code: $errorCode")
//            // Handle the failure, like restarting the scan or showing an error message
//        }
//    }
//
//    // Connect to a specific BLE device using GATT
//    private fun connectToDevice(device: BluetoothDevice) {
//        bluetoothGatt = device.connectGatt(this, false, gattCallback)
//        Log.d("Bluetooth", "Attempting to connect to GATT server...")
//    }
//
//    // GATT callback to handle connection, disconnection, and services discovery
//    private val gattCallback = object : BluetoothGattCallback() {
//        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//            when (newState) {
//                BluetoothProfile.STATE_CONNECTED -> {
//                    Log.d("Bluetooth", "Connected to GATT server")
//                    bluetoothGatt?.discoverServices() // Discover services after connection
//                }
//                BluetoothProfile.STATE_DISCONNECTED -> {
//                    Log.d("Bluetooth", "Disconnected from GATT server")
//                }
//                else -> {
//                    Log.d("Bluetooth", "Connection state changed: $newState")
//                }
//            }
//        }
//
//        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//
//                Log.d("Bluetooth", "Services discovered")
//                // Iterate over services to find the target service
//                val service = gatt?.getService(TARGET_SERVICE_UUID)
//                if (service != null) {
//                    val characteristic = service.getCharacteristic(TARGET_CHARACTERISTIC_UUID)
//                    if (characteristic != null) {
//                        Log.d("Bluetooth", "Target characteristic found")
//                        // You can now read/write or enable notifications on the characteristic
//                    } else {
//                        Log.d("Bluetooth", "Target characteristic not found")
//                    }
//                } else {
//                    Log.d("Bluetooth", "Target service not found")
//                }
//            } else {
//                Log.d("Bluetooth", "Failed to discover services, status: $status")
//            }
//        }
//
//        // Handle characteristic read/write (optional)
//        override fun onCharacteristicRead(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic?,
//            status: Int
//        ) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.d("Bluetooth", "Characteristic read successfully: ${characteristic?.value}")
//            } else {
//                Log.d("Bluetooth", "Failed to read characteristic, status: $status")
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        bluetoothGatt?.close() // Close the GATT connection when the activity is destroyed
//    }
//
//    // Handle Bluetooth permission request result
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startBLEScan()
//            } else {
//                Toast.makeText(this, "Bluetooth permissions are required", Toast.LENGTH_LONG).show()
//                finish() // Exit the activity if permissions are not granted
//            }
//        }
//    }
//}






package com.example.relogin

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class MainActivity2 : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_BLUETOOTH_PERMISSION = 101
    private val DEVICE_ADDRESS = "CB:F6:AA:FB:84:F3" // Replace with your BLE Device's MAC Address
    private val TARGET_SERVICE_UUID: UUID = UUID.fromString("ef680400-9bfe-4933-9b10-25ffa9740042") // Replace with your BLE device's service UUID
    private val TARGET_CHARACTERISTIC_UUID: UUID = UUID.fromString("ef680440-9bfe-4933-9b10-25ffa9740042") // Replace with your characteristic UUID
    private val TARGET_NOTIFICATION_CHARACTERISTIC_UUID: UUID = UUID.fromString("ef680420-9bfe-4933-9b10-25ffa9740042") // Replace with your characteristic UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2) // Make sure this is the right layout file

        // Initialize Bluetooth Manager and Adapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Check if Bluetooth is supported
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            checkPermissionsAndStartScanning()
        }

        // Start button - Begins reading data or enabling notifications
        findViewById<Button>(R.id.BLE_start_button).setOnClickListener {
//            startReadingData()
        }

        // Stop button - Stops reading data or disabling notifications
        findViewById<Button>(R.id.BLE_stop_button).setOnClickListener {
            stopReadingData()
        }


    }

    // Check BLE permissions for Android 12+ and start scanning for BLE devices
    private fun checkPermissionsAndStartScanning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            startBLEScan()
        }
    }

    // Start scanning for BLE devices
    private fun startBLEScan() {
        val scanner = bluetoothAdapter.bluetoothLeScanner
        scanner.startScan(bleScanCallback)
        Log.d("Bluetooth", "Scanning for BLE devices...")
    }

    // Callback when a BLE device is found during the scan
    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("Bluetooth", "Found device:")
            val device: BluetoothDevice? = result.device
            if (device != null && device.address == DEVICE_ADDRESS) {
                Log.d("Bluetooth", "Found device: ${device.name}, ${device.address}")
                // Stop scanning after the device is found
                bluetoothAdapter.bluetoothLeScanner.stopScan(this)
                connectToDevice(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("Bluetooth", "Scan failed with error code: $errorCode")
        }
    }

    // Connect to a specific BLE device using GATT
    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Log.d("Bluetooth", "Attempting to connect to GATT server...")
    }

    // GATT callback to handle connection, disconnection, and services discovery
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("Bluetooth", "Connected to GATT server")

                    bluetoothGatt?.discoverServices() // Discover services after connection
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("Bluetooth", "Disconnected. Attempting to reconnect...")
                    connectToDevice(gatt?.device ?: return) // Try reconnecting
                }
                else -> {
                    Log.d("Bluetooth", "Connection state changed: $newState")
                }
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("Bluetooth", "Write successful")
            } else {
                Log.e("Bluetooth", "Write failed with status: $status")
            }
        }
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("Bluetooth", "Services discovered")
                startReadingData()
                // Ready to start/stop reading data based on user interaction
            } else {
                Log.d("Bluetooth", "Failed to discover services, status: $status")
            }
        }

        // Handle characteristic read/write (optional)
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic?.value
                Log.d("Bluetooth", "Characteristic read: ${data?.joinToString()}")
            } else {
                Log.d("Bluetooth", "Failed to read characteristic, status: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            val data = characteristic?.value
            Log.d("Bluetooth", "Notification received: ${data?.joinToString()}")
        }
    }
//
//    private fun convertFromInteger(i: Int): UUID {
//        val MSB = 0x0000000000001000L
//        val LSB : long= 0x800000805F9B34FBL as long
//        val value = i.toLong() and 0xFFFFFFFFL
//        return UUID(MSB or (value shl 32), LSB)
//    }




    // Start reading data (Enable notifications)
    private fun startReadingData() {
        Log.d("Bluetooth", "Getting GATT service...")
        val service = bluetoothGatt?.getService(TARGET_SERVICE_UUID)
        if (service != null) {
            val characteristic = service.getCharacteristic(TARGET_CHARACTERISTIC_UUID)
            Log.d("Bluetooth", "Target characteristic found    ,${characteristic.uuid}" )
            if (characteristic != null) {
                Log.d("Bluetooth", "Starting to read data...")
                // Enable notifications for the characteristic
                bluetoothGatt?.setCharacteristicNotification(characteristic, true)
                Log.d("Bluetooth", "Characteristic found")
                // Write to the descriptor to enable notifications at the GATT level

                val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                Log.d("Bluetooth", "found    ,${descriptor}"  )

                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt?.writeDescriptor(descriptor)
                    Log.d("Bluetooth" , "xxxxxxxxx Descriptor with UUID 0x2902 found xxxxxxxxxxx!")
                } else {
                    Log.e("Bluetooth", "Descriptor with UUID 0x2902 not found!")
                }

            } else {
                Log.d("Bluetooth", "Target characteristic not found")
            }
        } else {
            Log.d("Bluetooth", "Target service not found")
        }
    }

    // Stop reading data (Disable notifications)
    private fun stopReadingData() {
        val service = bluetoothGatt?.getService(TARGET_SERVICE_UUID)
        if (service != null) {
            val characteristic = service.getCharacteristic(TARGET_NOTIFICATION_CHARACTERISTIC_UUID)
            if (characteristic != null) {
                Log.d("Bluetooth", "Stopping data reading...")

                // Disable notifications for the characteristic
                bluetoothGatt?.setCharacteristicNotification(characteristic, false)

                // Write to the descriptor to disable notifications at the GATT level
                val descriptor = characteristic.getDescriptor(UUID.fromString("FF02"))
                descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close() // Close the GATT connection when the activity is destroyed
    }

    // Handle Bluetooth permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBLEScan()
            } else {
                Toast.makeText(this, "Bluetooth permissions are required", Toast.LENGTH_LONG).show()
                finish() // Exit the activity if permissions are not granted
            }
        }
    }
}
