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


import com.zreader.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class CharsPerLineActivity extends Activity {
    private static final String TAG = "CharsPerLineActivity";

    private TextView mStatusView;
    private View mContainerView;
    private TextView mTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mStatusView = (TextView) findViewById(R.id.status);
        mContainerView = findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);

        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d(TAG, "Parent: " + mContainerView.getWidth()
                                + ", Child: " + mTextView.getWidth());
                        int maxCharsPerLine = CharsPerLineUtil.getMaxCharsPerLine(mTextView);
                        boolean badCpl = maxCharsPerLine < CharsPerLineUtil.RECOMMENDED_MIN_CPL
                                || maxCharsPerLine > CharsPerLineUtil.RECOMMENDED_MAX_CPL;
                        mStatusView.setTextColor(badCpl ? 0xffff4444 : 0x88ffffff);
                        mStatusView.setText("Maximum measure: " + maxCharsPerLine + " CPL");
                    }
                });
    }
}
