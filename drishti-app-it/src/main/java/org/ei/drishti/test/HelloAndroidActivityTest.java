package org.ei.drishti.test;

import android.test.ActivityInstrumentationTestCase2;
import org.ei.drishti.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super(HelloAndroidActivity.class); 
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}
