package com.example.robotcarapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import android.os.Handler
import android.view.View
import android.widget.TextView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class BluetoothService {

    companion object {

        private var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothAdapter: BluetoothAdapter? = null
        private var bluetoothSocket: BluetoothSocket? = null
        private var bluetoothManager: BluetoothManager? = null
        var deviceSelected: BluetoothDevice? = null

        fun initializeBluetoothManager (activity: Activity?) {
            bluetoothManager = activity?.getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager?.adapter
        }

        fun getPairedDeviceList (activity: Activity): ArrayList<BluetoothDevice> {

            val grantedCode: Int = if (android.os.Build.VERSION.SDK_INT < 30) -1 else PackageManager.PERMISSION_GRANTED

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) !== grantedCode) {
                return ArrayList()
            }

            val pairedDevices = bluetoothAdapter!!.bondedDevices
            val bluetoothDevices: ArrayList<BluetoothDevice> = ArrayList()

            if (pairedDevices.isNotEmpty()) {
                for (device: BluetoothDevice in pairedDevices) {
                    bluetoothDevices.add(device)
                }
            }

            return bluetoothDevices

        }

        @SuppressLint("MissingPermission")
        fun connect(device: BluetoothDevice, activity: Activity, itemView: View) {

            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Bluetooth")

            if (isConnected()) {

                disconnect()

                if (deviceSelected != null && deviceSelected?.address != device.address) {

                    val txtBluetoothAddress: TextView? = CustomAdapter.itemViewSelected?.findViewById(R.id.txtBluetoothAddress)
                    txtBluetoothAddress?.text = CustomAdapter.itemViewSelected?.resources?.getString(R.string.Not_connected)

                }

            }

            val loadingDialog = LoadingDialog(activity)
            loadingDialog.start("Conectando...")

            val executor: ExecutorService = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.execute(Runnable {

                run {

                    bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                    bluetoothAdapter?.cancelDiscovery()

                    try {
                        bluetoothSocket?.connect()
                        deviceSelected = device
                    } catch (e: IOException) {
                        bluetoothSocket = null
                        deviceSelected = null
                    }

                    handler.post(Runnable {

                        loadingDialog.dismiss()
                        val txtBluetoothAddress: TextView = itemView.findViewById(R.id.txtBluetoothAddress)

                        if (bluetoothSocket == null) {
                            alertDialog.setMessage("No se pudo establecer la conexión.")
                            alertDialog.show()
                            txtBluetoothAddress.text = itemView.resources.getString(R.string.Not_connected)
                        }
                        else {
                            CustomAdapter.itemViewSelected = itemView
                            txtBluetoothAddress.text = itemView.resources.getString(R.string.Connected)
                        }

                    })

                }

            })

        }

        private fun disconnect() {

            if (bluetoothSocket != null) {

                try {
                    bluetoothSocket!!.close()
                    bluetoothSocket = null
                } catch (_: IOException) { }

            }

        }

        private fun isConnected (): Boolean {
            return bluetoothSocket?.isConnected ?: false
        }

        fun sendCommand(text: String) {

            if (bluetoothSocket != null) {

                try {
                    bluetoothSocket?.outputStream?.write(text.toByteArray(Charsets.UTF_8))
                } catch (_: IOException) { }

            }

        }

        fun read (): Int? {
            try {
                return bluetoothSocket?.inputStream?.read()
            } catch (_: IOException) { }

            return null
        }

    }

}