/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.car.settings.wifi.details;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.when;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceControllerTestUtil;
import com.android.car.settings.testutils.TestLifecycleOwner;
import com.android.wifitrackerlib.WifiEntry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class WifiMacAddressPreferenceControllerTest {
    private static final String MAC_ADDRESS = "mac_address";

    private Context mContext = ApplicationProvider.getApplicationContext();
    private LifecycleOwner mLifecycleOwner;
    private WifiDetailsPreference mPreference;
    private WifiMacAddressPreferenceController mPreferenceController;
    private CarUxRestrictions mCarUxRestrictions;

    @Mock
    private FragmentController mFragmentController;
    @Mock
    private WifiEntry mMockWifiEntry;
    @Mock
    private WifiInfoProvider mMockWifiInfoProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLifecycleOwner = new TestLifecycleOwner();

        mCarUxRestrictions = new CarUxRestrictions.Builder(/* reqOpt= */ true,
                CarUxRestrictions.UX_RESTRICTIONS_BASELINE, /* timestamp= */ 0).build();

        mPreference = new WifiDetailsPreference(mContext);
        mPreferenceController = new WifiMacAddressPreferenceController(mContext,
                /* preferenceKey= */ "key", mFragmentController, mCarUxRestrictions);
        mPreferenceController.init(mMockWifiEntry, mMockWifiInfoProvider);
        PreferenceControllerTestUtil.assignPreference(mPreferenceController, mPreference);
    }

    @Test
    public void onCreate_shouldHaveDetailTextSet() {
        when(mMockWifiEntry.getConnectedState()).thenReturn(WifiEntry.CONNECTED_STATE_CONNECTED);
        when(mMockWifiEntry.getMacAddress()).thenReturn(MAC_ADDRESS);
        mPreferenceController.onCreate(mLifecycleOwner);

        assertThat(mPreference.isVisible()).isTrue();
        assertThat(mPreference.getDetailText()).isEqualTo(MAC_ADDRESS);
    }

    @Test
    public void onCreate_isNotActive_preferenceNotVisible() {
        when(mMockWifiEntry.getConnectedState()).thenReturn(WifiEntry.CONNECTED_STATE_DISCONNECTED);
        when(mMockWifiEntry.getMacAddress()).thenReturn(null);
        mPreferenceController.onCreate(mLifecycleOwner);

        assertThat(mPreference.isVisible()).isFalse();
    }
}
