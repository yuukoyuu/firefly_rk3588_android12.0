/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tv.settings.name;

import android.annotation.Nullable;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;

/**
 * Entry activity for settings suggestions.
 */
public class DeviceNameSuggestionActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, DeviceNameSetFragment.newInstance(),
                    android.R.id.content);
        }
    }
}
