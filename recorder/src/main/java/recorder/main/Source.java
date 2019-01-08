package recorder.main;

import android.media.AudioRecord;


interface Source {
  AudioRecord audioRecord();

  AudioRecordConfig config();

  int minimumBufferSize();

  class Default implements Source {
    private final AudioRecord audioRecord;
    private final AudioRecordConfig config;
    private final int minimumBufferSize;

    Default(AudioRecordConfig config) {
      this.config = config;
      this.minimumBufferSize = new MinimumBufferSize(config).asInt();
      this.audioRecord =
          new AudioRecord(config.audioSource(), config.frequency(), config.channelPositionMask(),
              config.audioEncoding(), minimumBufferSize);
    }

    @Override public AudioRecord audioRecord() {
      return audioRecord;
    }

    @Override public AudioRecordConfig config() {
      return config;
    }

    @Override public int minimumBufferSize() {
      return minimumBufferSize;
    }
  }
}
