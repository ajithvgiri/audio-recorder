/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package recorder.main;

final class WavHeader {
    private final AudioSources audioRecordSource;
    private final long totalAudioLength;

    WavHeader(AudioSources audioRecordSource, long totalAudioLength) {
        this.audioRecordSource = audioRecordSource;
        this.totalAudioLength = totalAudioLength;
    }

    public byte[] toBytes() {
        long sampleRateInHz = (long)this.audioRecordSource.frequency();
        int channels = this.audioRecordSource.channelPositionMask() == 16 ? 1 : 2;
        byte bitsPerSample = this.audioRecordSource.bitsPerSample();
        return this.wavFileHeader(this.totalAudioLength, this.totalAudioLength + 36L, sampleRateInHz, channels, (long)bitsPerSample * sampleRateInHz * (long)channels / 8L, bitsPerSample);
    }

    private byte[] wavFileHeader(long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate, byte bitsPerSample) {
        byte[] header = new byte[]{82, 73, 70, 70, (byte)((int)(totalDataLen & 255L)), (byte)((int)(totalDataLen >> 8 & 255L)), (byte)((int)(totalDataLen >> 16 & 255L)), (byte)((int)(totalDataLen >> 24 & 255L)), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte)channels, 0, (byte)((int)(longSampleRate & 255L)), (byte)((int)(longSampleRate >> 8 & 255L)), (byte)((int)(longSampleRate >> 16 & 255L)), (byte)((int)(longSampleRate >> 24 & 255L)), (byte)((int)(byteRate & 255L)), (byte)((int)(byteRate >> 8 & 255L)), (byte)((int)(byteRate >> 16 & 255L)), (byte)((int)(byteRate >> 24 & 255L)), (byte)(channels * (bitsPerSample / 8)), 0, bitsPerSample, 0, 100, 97, 116, 97, (byte)((int)(totalAudioLen & 255L)), (byte)((int)(totalAudioLen >> 8 & 255L)), (byte)((int)(totalAudioLen >> 16 & 255L)), (byte)((int)(totalAudioLen >> 24 & 255L))};
        return header;
    }
}
