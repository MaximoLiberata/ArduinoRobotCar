package com.example.robotcarapp

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SelectBluetoothDevice: Fragment(R.layout.select_bluetooth_device) {

    var isBluetoothEnable = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view: View = inflater.inflate(R.layout.select_bluetooth_device, container ,false)
        BluetoothService.initializeBluetoothManager(activity);

        val btnSelectDeviceRefresh: Button = view.findViewById(R.id.btnSelectDeviceRefresh)

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

        if (BluetoothService.bluetoothAdapter == null) {
            Toast.makeText(activity, "Tu dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
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

        val bluetoothDevices = BluetoothService.getPairedDeviceList(requireActivity());
        val adapter = CustomAdapter(bluetoothDevices);
        val selectDeviceList: RecyclerView = view.findViewById(R.id.selectDeviceList)
        selectDeviceList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        selectDeviceList.adapter = adapter

    }

}
