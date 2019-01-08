package recorder.model

import android.media.AudioFormat

enum class AudioChannel {
    STEREO,
    MONO;

    val channel: Int
        get() {
            when (this) {
                MONO -> return AudioFormat.CHANNEL_IN_MONO
                else -> return AudioFormat.CHANNEL_IN_STEREO
            }
        }
}