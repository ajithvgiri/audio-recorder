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

import java.io.*;

abstract class AbstractRecorder implements Recorder {
  protected final PullTransport pullTransport;
  protected final File file;
  private final OutputStream outputStream;

  protected AbstractRecorder(PullTransport pullTransport, File file) {
    this.pullTransport = pullTransport;
    this.file = file;
    this.outputStream = this.outputStream(file);
  }

  public void startRecording() {
    (new Thread(new Runnable() {
      public void run() {
        try {
          AbstractRecorder.this.pullTransport.start(AbstractRecorder.this.outputStream);
        } catch (IOException var2) {
          new RuntimeException(var2);
        }

      }
    })).start();
  }

  private OutputStream outputStream(File file) {
    if (file == null) {
      throw new RuntimeException("file is null !");
    } else {
      try {
        OutputStream outputStream = new FileOutputStream(file);
        return outputStream;
      } catch (FileNotFoundException var4) {
        throw new RuntimeException("could not build OutputStream from this file" + file.getName(), var4);
      }
    }
  }

  public void stopRecording() {
    this.pullTransport.stop();
  }

  public void pauseRecording() {
    this.pullTransport.source().isEnableToBePulled(false);
  }

  public void resumeRecording() {
    this.pullTransport.source().isEnableToBePulled(true);
    this.startRecording();
  }
}