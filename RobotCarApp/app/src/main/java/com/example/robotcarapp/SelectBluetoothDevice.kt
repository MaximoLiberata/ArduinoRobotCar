package com.example.robotcarapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.NotNull


class SelectBluetoothDevice: Fragment(R.layout.select_bluetooth_device) {

    private var isBluetoothEnable = false
    private lateinit var thisContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view: View = inflater.inflate(R.layout.select_bluetooth_device, container ,false)
        BluetoothService.initializeBluetoothManager(activity)

        val btnSelectDeviceRefresh: Button = view.findViewById(R.id.btnSelectDeviceRefresh)
        thisContext = inflater.context;

        btnSelectDeviceRefresh.setOnClickListener {
            pairedDeviceList(view)
        }

        pairedDeviceList(view)


        return view

    }

    private val getActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        isBluetoothEnable = it.resultCode != Activity.RESULT_CANCELED
    }


    private fun askForBluetooth (): Boolean {

        val grantedCode: Int = if (android.os.Build.VERSION.SDK_INT < 30) -1 else PackageManager.PERMISSION_GRANTED

        if (BluetoothService.bluetoothAdapter == null) {
            Toast.makeText(activity, "Tu dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
        }
        else if (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.BLUETOOTH_CONNECT) !== grantedCode) {

            Toast.makeText(activity, "Es requerido el acceso al Bluetooth", Toast.LENGTH_SHORT).show()

            ActivityCompat.requestPermissions(
                thisContext as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )

        }
        else if (BluetoothService.bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            getActivityResult.launch(enableBtIntent)
        }

        isBluetoothEnable = BluetoothService.bluetoothAdapter?.isEnabled?:false

        return isBluetoothEnable

    }

    private fun pairedDeviceList (view: View) {

        if (!askForBluetooth()) {
            return
        }

        val bluetoothDevices = BluetoothService.getPairedDeviceList(requireActivity())
        val adapter = CustomAdapter(bluetoothDevices)
        val selectDeviceList: RecyclerView = view.findViewById(R.id.selectDeviceList)
        selectDeviceList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        selectDeviceList.adapter = adapter

    }

}
