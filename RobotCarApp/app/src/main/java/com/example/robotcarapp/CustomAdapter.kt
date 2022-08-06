package com.example.robotcarapp

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class CustomAdapter(private val bluetoothDevices: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var itemViewSelected: View? = null
    }

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(bluetoothDevices[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return bluetoothDevices.size
    }

    //the class is holding the list view
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("MissingPermission", "ClickableViewAccessibility")
        fun bindItems(device: BluetoothDevice) {
            val cardBluetoothDevice: CardView = itemView.findViewById(R.id.cardBluetoothDevice)
            val txtBluetoothName: TextView = itemView.findViewById(R.id.txtBluetoothName)
            val txtBluetoothAddress: TextView  = itemView.findViewById(R.id.txtBluetoothAddress)

            txtBluetoothName.text = device.name

            if (BluetoothService.deviceSelected?.address == device.address) {
                txtBluetoothAddress.text = itemView.resources.getString(R.string.Connected)
            }

            cardBluetoothDevice.setOnTouchListener(object: View.OnTouchListener {

                var x: Float = 0F
                var y: Float = 0F
                var currentTime = System.currentTimeMillis()

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when(event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = event.x
                            y = event.y
                            currentTime = System.currentTimeMillis()
                            cardBluetoothDevice.background.setTint(ContextCompat.getColor(itemView.context, R.color.grey_2))
                            return true
                        }
                        MotionEvent.ACTION_CANCEL,
                        MotionEvent.ACTION_UP -> {
                            cardBluetoothDevice.background.setTint(ContextCompat.getColor(itemView.context, R.color.grey_1))

                            if (x == event.x && y == event.y && (currentTime + 200) >= System.currentTimeMillis()) {
                                BluetoothService.Connect(device, itemView.context as Activity, itemView)
                            }

                            return true
                        }
                    }

                    return false
                }

            })

        }

    }

}