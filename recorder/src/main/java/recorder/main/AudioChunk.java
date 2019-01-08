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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface AudioChunk {
  double maxAmplitude();

  byte[] toBytes();

  short[] toShorts();

  public static final class Shorts implements AudioChunk {
    private static final short SILENCE_THRESHOLD = 2700;
    private static final double REFERENCE = 0.6D;
    final short[] shorts;
    int numberOfShortsRead;

    Shorts(short[] bytes) {
      this.shorts = bytes;
    }

    int peakIndex() {
      int arrLen = this.shorts.length;

      for(int peakIndex = 0; peakIndex < arrLen; ++peakIndex) {
        if (this.shorts[peakIndex] >= 2700 || this.shorts[peakIndex] <= -2700) {
          return peakIndex;
        }
      }

      return -1;
    }

    public double maxAmplitude() {
      int nMaxAmp = 0;
      int arrLen = this.shorts.length;

      for(int peakIndex = 0; peakIndex < arrLen; ++peakIndex) {
        if (this.shorts[peakIndex] >= nMaxAmp) {
          nMaxAmp = this.shorts[peakIndex];
        }
      }

      return (double)((int)(20.0D * Math.log10((double)nMaxAmp / 0.6D)));
    }

    public byte[] toBytes() {
      byte[] buffer = new byte[this.numberOfShortsRead * 2];
      int byteIndex = 0;

      for(int shortIndex = 0; shortIndex != this.numberOfShortsRead; byteIndex += 2) {
        buffer[byteIndex] = (byte)(this.shorts[shortIndex] & 255);
        buffer[byteIndex + 1] = (byte)((this.shorts[shortIndex] & '\uff00') >> 8);
        ++shortIndex;
      }

      return buffer;
    }

    public short[] toShorts() {
      return this.shorts;
    }
  }

  public static final class Bytes implements AudioChunk {
    private static final double REFERENCE = 0.6D;
    final byte[] bytes;
    int numberOfBytesRead;

    Bytes(byte[] bytes) {
      this.bytes = bytes;
    }

    public double maxAmplitude() {
      short[] shorts = this.toShorts();
      int nMaxAmp = 0;
      int arrLen = shorts.length;

      for(int peakIndex = 0; peakIndex < arrLen; ++peakIndex) {
        if (shorts[peakIndex] >= nMaxAmp) {
          nMaxAmp = shorts[peakIndex];
        }
      }

      return (double)((int)(20.0D * Math.log10((double)nMaxAmp / 0.6D)));
    }

    public byte[] toBytes() {
      return this.bytes;
    }

    public short[] toShorts() {
      short[] shorts = new short[this.bytes.length / 2];
      ByteBuffer.wrap(this.bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
      return shorts;
    }
  }
}

