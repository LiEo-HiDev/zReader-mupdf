/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charsperline.maxwidth;


import com.zreader.main.BuildConfig;

import android.text.Layout;
import android.util.Log;
import android.widget.TextView;

/**
 * Helper utilities useful during debugging that compute the maximum number of characters per line
 * in a {@link TextView} or {@link Layout}.
 */
public class CharsPerLineUtil {
    private static final String TAG = "CharsPerLineUtil";

    public static final int RECOMMENDED_MIN_CPL = 45;
    public static final int RECOMMENDED_MAX_CPL = 75;

    /**
     * Compute the maximum number of characters per line in the given {@link TextView}.
     */
    public static int getMaxCharsPerLine(TextView textView) {
        return getMaxCharsPerLine(textView.getLayout());
    }

    /**
     * Compute the maximum number of characters per line in the given {@link Layout}.
     */
    public static int getMaxCharsPerLine(Layout layout) {
        int maxChars = 0;
        int maxIndex = -1;
        for (int i = layout.getLineCount() - 1; i >= 0; i--) {
            int chars = layout.getLineEnd(i) - layout.getLineStart(i);
            if (chars > maxChars) {
                maxChars = chars;
                maxIndex = i;
            }
        }
        if (BuildConfig.DEBUG && maxIndex >= 0) {
            CharSequence line = layout.getText().subSequence(
                    layout.getLineStart(maxIndex),
                    layout.getLineEnd(maxIndex));
            Log.d(TAG, "Max line: '" + line + "' (length=" + line.length() + ")");
        }
        return maxChars;
    }
}
