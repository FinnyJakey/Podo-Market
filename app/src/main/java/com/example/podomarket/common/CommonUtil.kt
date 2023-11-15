package com.example.podomarket.common

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

    }
}