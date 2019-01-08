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

import java.io.IOException;
import java.io.OutputStream;


public interface WriteAction {
    void execute(byte[] var1, OutputStream var2) throws IOException;

    public static final class Default implements WriteAction {
        public Default() {
        }

        public void execute(byte[] data, OutputStream outputStream) throws IOException {
            outputStream.write(data);
        }
    }
}

