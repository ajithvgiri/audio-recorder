package recorder.utils

import android.media.AudioFormat
import recorder.main.AudioSources
import recorder.model.AudioChannel
import recorder.model.AudioSampleRate
import recorder.model.AudioSource

object RecorderUtils {
    fun getMic(source: AudioSource, channel: AudioChannel, sampleRate: AudioSampleRate): AudioSources {
        return AudioSources.Smart(source.source, AudioFormat.ENCODING_PCM_16BIT, channel.channel, sampleRate.sampleRate)
    }

}