/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package recorder.main;

import android.media.AudioRecord;
import recorder.main.AudioChunk.Bytes;
import recorder.main.AudioChunk.Shorts;

import java.io.IOException;
import java.io.OutputStream;

import static recorder.main.Recorder.OnSilenceListener;


public interface PullTransport {
    void start(OutputStream var1) throws IOException;

    void stop();

    AudioSources source();

    public static final class Noise extends PullTransport.AbstractPullTransport {
        private final Shorts audioChunk;
        private final long silenceTimeThreshold;
        private final OnSilenceListener silenceListener;
        private final WriteAction writeAction;
        private long firstSilenceMoment;
        private int noiseRecordedAfterFirstSilenceThreshold;

        public Noise(AudioSources audioRecordSource, PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener, WriteAction writeAction, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            super(audioRecordSource, onAudioChunkPulledListener);
            this.firstSilenceMoment = 0L;
            this.noiseRecordedAfterFirstSilenceThreshold = 0;
            this.writeAction = writeAction;
            this.silenceListener = silenceListener;
            this.silenceTimeThreshold = silenceTimeThreshold;
            this.audioChunk = new Shorts(new short[audioRecordSource.minimumBufferSize()]);
        }

        public Noise(AudioSources audioRecordSource, PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            this(audioRecordSource, onAudioChunkPulledListener, new WriteAction.Default(), silenceListener, silenceTimeThreshold);
        }

        public Noise(AudioSources audioRecordSource, WriteAction writeAction, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, writeAction, silenceListener, silenceTimeThreshold);
        }

        public Noise(AudioSources audioRecordSource, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, new WriteAction.Default(), silenceListener, silenceTimeThreshold);
        }

        public Noise(AudioSources audioRecordSource, OnSilenceListener silenceListener) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, new WriteAction.Default(), silenceListener, 200L);
        }

        public Noise(AudioSources audioRecordSource) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, new WriteAction.Default(), (OnSilenceListener)null, 200L);
        }

        public void start(OutputStream outputStream) throws IOException {
            AudioRecord audioRecord = this.audioRecordSource.audioRecorder();
            audioRecord.startRecording();
            this.audioRecordSource.isEnableToBePulled(true);

            while(true) {
                while(true) {
                    do {
                        if (!this.audioRecordSource.isEnableToBePulled()) {
                            return;
                        }

                        this.audioChunk.numberOfShortsRead = audioRecord.read(this.audioChunk.shorts, 0, this.audioChunk.shorts.length);
                    } while(-3 == this.audioChunk.numberOfShortsRead);

                    if (this.onAudioChunkPulledListener != null) {
                        this.postPullEvent(this.audioChunk);
                    }

                    if (this.audioChunk.peakIndex() > -1) {
                        this.writeAction.execute(this.audioChunk.toBytes(), outputStream);
                        this.firstSilenceMoment = 0L;
                        ++this.noiseRecordedAfterFirstSilenceThreshold;
                    } else {
                        if (this.firstSilenceMoment == 0L) {
                            this.firstSilenceMoment = System.currentTimeMillis();
                        }

                        long silenceTime = System.currentTimeMillis() - this.firstSilenceMoment;
                        if (this.firstSilenceMoment != 0L && silenceTime > this.silenceTimeThreshold) {
                            if (silenceTime > 1000L && this.noiseRecordedAfterFirstSilenceThreshold >= 3) {
                                this.noiseRecordedAfterFirstSilenceThreshold = 0;
                                if (this.silenceListener != null) {
                                    this.postSilenceEvent(this.silenceListener, silenceTime);
                                }
                            }
                        } else {
                            this.writeAction.execute(this.audioChunk.toBytes(), outputStream);
                        }
                    }
                }
            }
        }
    }

    public static final class Default extends PullTransport.AbstractPullTransport {
        private final WriteAction writeAction;

        public Default(AudioSources audioRecordSource, PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener, WriteAction writeAction) {
            super(audioRecordSource, onAudioChunkPulledListener);
            this.writeAction = writeAction;
        }

        public Default(AudioSources audioRecordSource, WriteAction writeAction) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, writeAction);
        }

        public Default(AudioSources audioRecordSource, PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this(audioRecordSource, onAudioChunkPulledListener, new WriteAction.Default());
        }

        public Default(AudioSources audioRecordSource) {
            this(audioRecordSource, (PullTransport.OnAudioChunkPulledListener)null, new WriteAction.Default());
        }

        void startPoolingAndWriting(AudioRecord audioRecord, int minimumBufferSize, OutputStream outputStream) throws IOException {
            while(this.audioRecordSource.isEnableToBePulled()) {
                AudioChunk audioChunk = new Bytes(new byte[minimumBufferSize]);
                if (-3 != audioRecord.read(audioChunk.toBytes(), 0, minimumBufferSize)) {
                    if (this.onAudioChunkPulledListener != null) {
                        this.postPullEvent(audioChunk);
                    }

                    this.writeAction.execute(audioChunk.toBytes(), outputStream);
                }
            }

        }
    }

    public abstract static class AbstractPullTransport implements PullTransport {
        final AudioSources audioRecordSource;
        final PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener;
        private final UiThread uiThread = new UiThread();

        AbstractPullTransport(AudioSources audioRecordSource, PullTransport.OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.audioRecordSource = audioRecordSource;
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
        }

        public void start(OutputStream outputStream) throws IOException {
            this.startPoolingAndWriting(this.preparedSourceToBePulled(), this.audioRecordSource.minimumBufferSize(), outputStream);
        }

        void startPoolingAndWriting(AudioRecord audioRecord, int minimumBufferSize, OutputStream outputStream) throws IOException {
        }

        public void stop() {
            this.audioRecordSource.isEnableToBePulled(false);
            this.audioRecordSource.audioRecorder().stop();
        }

        public AudioSources source() {
            return this.audioRecordSource;
        }

        AudioRecord preparedSourceToBePulled() {
            AudioRecord audioRecord = this.audioRecordSource.audioRecorder();
            audioRecord.startRecording();
            this.audioRecordSource.isEnableToBePulled(true);
            return audioRecord;
        }

        void postSilenceEvent(final OnSilenceListener onSilenceListener, final long silenceTime) {
            this.uiThread.execute(new Runnable() {
                public void run() {
                    onSilenceListener.onSilence(silenceTime);
                }
            });
        }

        void postPullEvent(final AudioChunk audioChunk) {
            this.uiThread.execute(new Runnable() {
                public void run() {
                    AbstractPullTransport.this.onAudioChunkPulledListener.onAudioChunkPulled(audioChunk);
                }
            });
        }
    }

    public interface OnAudioChunkPulledListener {
        void onAudioChunkPulled(AudioChunk var1);
    }
}
