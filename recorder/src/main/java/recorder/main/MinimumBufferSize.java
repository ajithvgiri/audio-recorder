package recorder.main;

import android.media.AudioRecord;

final class MinimumBufferSize {
  private final int minimumBufferSize;

  MinimumBufferSize(AudioRecordConfig audioRecordConfig) {
    this.minimumBufferSize = AudioRecord.getMinBufferSize(audioRecordConfig.frequency(),
        audioRecordConfig.channelPositionMask(), audioRecordConfig.audioEncoding());
  }

  int asInt() {
    return minimumBufferSize;
  }
}
