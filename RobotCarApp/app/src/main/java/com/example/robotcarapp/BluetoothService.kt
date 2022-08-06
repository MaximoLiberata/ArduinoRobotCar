package com.example.robotcarapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class BluetoothService {

    companion object {

        private var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothAdapter: BluetoothAdapter? = null
        var bluetoothSocket: BluetoothSocket? = null
        var bluetoothManager: BluetoothManager? = null
        var deviceSelected: BluetoothDevice? = null

        fun initializeBluetoothManager (activity: Activity?) {
            bluetoothManager = activity?.getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager?.adapter
        }

        fun getPairedDeviceList (activity: Activity): ArrayList<BluetoothDevice> {

            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != -1/*PackageManager.PERMISSION_GRANTED*/
            ) {
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
        fun Connect(device: BluetoothDevice, activity: Activity, itemView: View) {

            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Bluetooth")

            if (isConnected()) {

                Disconnect()

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

        fun Disconnect() {

            if (bluetoothSocket != null) {

                try {
                    bluetoothSocket!!.close()
                    bluetoothSocket = null
                } catch (e: IOException) { }

            }

        }

        fun isConnected (): Boolean {
            return bluetoothSocket?.isConnected ?: false
        }

        fun SendCommand(text: String) {

            if (bluetoothSocket != null) {

                try {
                    bluetoothSocket?.outputStream?.write(text.toByteArray(Charsets.UTF_8))
                } catch (e: IOException) { }

            }

        }

        fun read (): Int? {
            try {
                return bluetoothSocket?.inputStream?.read()
            } catch (e: IOException) { null }

            return null
        }

    }

}