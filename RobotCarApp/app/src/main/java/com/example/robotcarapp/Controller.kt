package com.example.robotcarapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*
import java.util.concurrent.*
import kotlin.concurrent.timerTask


class Controller: Fragment(R.layout.controller) {

    var lineTrackingStarted = false
    var autoControl = false

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view: View = inflater.inflate(R.layout.controller, container ,false)

        val btnUp: ImageButton = view.findViewById(R.id.btnUp)
        val btnDown: ImageButton = view.findViewById(R.id.btnDown)
        val btnLeft: ImageButton = view.findViewById(R.id.btnLeft)
        val btnRight: ImageButton = view.findViewById(R.id.btnRight)
        val btnLineTracking: Button = view.findViewById(R.id.btnLineTracking)
        val btnAutoControl: Button = view.findViewById(R.id.btnAutoControl)

        btnUp.setOnTouchListener(BtnTouchListener(btnUp, "0"))

        btnDown.setOnTouchListener(BtnTouchListener(btnDown, "1"))

        btnLeft.setOnTouchListener(BtnTouchListener(btnLeft, "2"))

        btnRight.setOnTouchListener(BtnTouchListener(btnRight, "3"))

        btnLineTracking.setOnClickListener {
            if (lineTrackingStarted) {
                lineTrackingStarted = false;
                btnLineTracking.background.setTint(ContextCompat.getColor(view.context, R.color.green))
                BluetoothService.SendCommand("5")
            }
            else {
                lineTrackingStarted = true;
                btnLineTracking.background.setTint(ContextCompat.getColor(view.context, R.color.red))
                BluetoothService.SendCommand("4")
            }
        }

        btnAutoControl.setOnClickListener {
            if (autoControl) {
                autoControl = false;
                btnAutoControl.background.setTint(ContextCompat.getColor(view.context, R.color.green))
                BluetoothService.SendCommand("7")
            }
            else {
                autoControl = true;
                btnAutoControl.background.setTint(ContextCompat.getColor(view.context, R.color.red))
                BluetoothService.SendCommand("6")
            }

        }

        return view

    }

    private class BtnTouchListener (button: ImageButton, command: String) : OnTouchListener {

        private val mmButton: ImageButton = button
        private val mmCommand = command
        var timer: Timer? = null

        override fun onTouch(v: View, event: MotionEvent?): Boolean {
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {

                    mmButton.background.setTint(ContextCompat.getColor(v.context, R.color.grey_4))
                    timer = Timer()

                    timer?.scheduleAtFixedRate(timerTask {
                        BluetoothService.SendCommand("$mmCommand-")
                    }, 0, 100)

                    return true
                }
                MotionEvent.ACTION_UP -> {
                    mmButton.background.setTint(ContextCompat.getColor(v.context, R.color.grey_3))
                    timer?.cancel()
                    return true
                }
            }

            return false
        }

    }


}
