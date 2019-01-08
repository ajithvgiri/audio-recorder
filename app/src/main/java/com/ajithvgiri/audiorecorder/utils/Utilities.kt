package com.ajithvgiri.audiorecorder.utils


object Utilities {

    fun formatSeconds(seconds: Int): String {
        return (getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60))
    }

    private fun getTwoDecimalsValue(value: Int): String {
        return if (value in 0..9) {
            "0$value"
        } else {
            value.toString() + ""
        }
    }

    fun recordingFilename(): String {
        val time = System.currentTimeMillis()
        return "Recordings$time.wav"
    }



}