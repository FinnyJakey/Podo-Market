package com.example.podomarket.common

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class CommonUtil {
    companion object {
        fun priceToString(price:Number) : String {
            val priceString = price.toString()

            val reversed = priceString.reversed()
            val stringBuilder = StringBuilder()

            for ((index, char) in reversed.withIndex()) {
                stringBuilder.append(char)
                if ((index + 1) % 3 == 0 && index + 1 != reversed.length) {
                    stringBuilder.append(',')
                }
            }
            return stringBuilder.reverse().toString()+"원"
        }
        fun getDeviceWidth(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }
}