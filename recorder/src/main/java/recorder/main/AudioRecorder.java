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

import java.io.File;

public final class AudioRecorder {
    private AudioRecorder() {
    }

    public static Recorder pcm(PullTransport pullTransport, File file) {
        return new Pcm(pullTransport, file);
    }

    public static Recorder wav(PullTransport pullTransport, File file) {
        return new Wav(pullTransport, file);
    }
}
