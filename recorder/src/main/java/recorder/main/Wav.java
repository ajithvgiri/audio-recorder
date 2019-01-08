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

import java.io.*;


final class Wav extends AbstractRecorder {
    private final RandomAccessFile wavFile;

    public Wav(PullTransport pullTransport, File file) {
        super(pullTransport, file);
        this.wavFile = this.randomAccessFile(file);
    }

    private RandomAccessFile randomAccessFile(File file) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            return randomAccessFile;
        } catch (FileNotFoundException var4) {
            throw new RuntimeException(var4);
        }
    }

    public void stopRecording() {
        super.stopRecording();

        try {
            this.writeWavHeader();
        } catch (IOException var2) {

        }

    }

    private void writeWavHeader() throws IOException {
        long totalAudioLen = (new FileInputStream(this.file)).getChannel().size();

        try {
            this.wavFile.seek(0L);
            this.wavFile.write((new WavHeader(this.pullTransport.source(), totalAudioLen)).toBytes());
            this.wavFile.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }
}