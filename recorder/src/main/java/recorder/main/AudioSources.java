package recorder.main;

import android.media.AudioRecord;

public interface AudioSources {
    AudioRecord audioRecorder();

    int channelPositionMask();

    int frequency();

    int minimumBufferSize();

    byte bitsPerSample();

    void isEnableToBePulled(boolean var1);

    boolean isEnableToBePulled();

    public static class Smart implements AudioSources {
        private final int audioSource;
        private final AudioRecord audioRecord;
        private final int channelPositionMask;
        private final int audioEncoding;
        private final int frequency;
        private volatile boolean pull;

        public Smart(int audioSource, int audioEncoding, int channelPositionMask, int frequency) {
            this.audioSource = audioSource;
            this.audioEncoding = audioEncoding;
            this.channelPositionMask = channelPositionMask;
            this.frequency = frequency;
            this.audioRecord = new AudioRecord(audioSource, frequency, channelPositionMask, audioEncoding, this.minimumBufferSize());
        }

        public AudioRecord audioRecorder() {
            return this.audioRecord;
        }

        public int channelPositionMask() {
            return this.channelPositionMask;
        }

        public int frequency() {
            return this.frequency;
        }

        public int minimumBufferSize() {
            return AudioRecord.getMinBufferSize(this.frequency, this.channelPositionMask, this.audioEncoding);
        }

        public byte bitsPerSample() {
            if (this.audioEncoding == 2) {
                return 16;
            } else {
                return (byte) (this.audioEncoding == 3 ? 8 : 16);
            }
        }

        public void isEnableToBePulled(boolean enabledToBePulled) {
            this.pull = enabledToBePulled;
        }

        public boolean isEnableToBePulled() {
            return this.pull;
        }
    }
}
