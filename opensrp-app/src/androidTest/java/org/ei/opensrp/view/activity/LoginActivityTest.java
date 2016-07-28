package org.ei.opensrp.view.activity;

import android.test.ActivityInstrumentationTestCase2;

import org.ei.opensrp.Context;
import org.ei.opensrp.util.DrishtiSolo;
import org.ei.opensrp.util.FakeDrishtiService;
import org.ei.opensrp.util.FakeUserService;

import java.util.Date;

import static org.ei.opensrp.domain.LoginResponse.SUCCESS;
import static org.ei.opensrp.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.ei.opensrp.util.FakeContext.setupService;
import static org.ei.opensrp.util.Wait.waitForFilteringToFinish;
import static org.ei.opensrp.util.Wait.waitForProgressBarToGoAway;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private DrishtiSolo solo;
    private FakeUserService userService;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        FakeDrishtiService drishtiService = new FakeDrishtiService(String.valueOf(new Date().getTime() - 1));
        userService = new FakeUserService();

        setupService(drishtiService, userService, -1000).updateApplicationContext(getActivity());
        Context.getInstance().session().setPassword(null);

        solo = new DrishtiSolo(getInstrumentation(), getActivity());
    }

    public void ignoreTestShouldAllowLoginWithoutCheckingRemoteLoginWhenLocalLoginSucceeds() throws Exception {
        userService.setupFor("user", "password", true, true, UNKNOWN_RESPONSE);

        solo.assertCanLogin("user", "password");

        userService.assertOrderOfCalls("local", "login");
    }

    public void ignoreTestShouldTryRemoteLoginWhenThereIsNoRegisteredUser() throws Exception {
        userService.setupFor("user", "password", false, false, SUCCESS);

        solo.assertCanLogin("user", "password");

        userService.assertOrderOfCalls("remote", "login");
    }

    public void ignoreTestShouldFailToLoginWhenBothLoginMethodsFail() throws Exception {
        userService.setupFor("user", "password", false, false, UNKNOWN_RESPONSE);

        solo.assertCannotLogin("user", "password");

        userService.assertOrderOfCalls("remote");
    }

    public void ignoreTestShouldNotTryRemoteLoginWhenRegisteredUserExistsEvenIfLocalLoginFails() throws Exception {
        userService.setupFor("user", "password", true, false, SUCCESS);

        solo.assertCannotLogin("user", "password");
        userService.assertOrderOfCalls("local");
    }

    public void ignoreTestShouldNotTryLocalLoginWhenRegisteredUserDoesNotExist() throws Exception {
        userService.setupFor("user", "password", false, true, SUCCESS);

        solo.assertCanLogin("user", "password");
        userService.assertOrderOfCalls("remote", "login");
    }

    @Override
    public void tearDown() throws Exception {
        waitForFilteringToFinish();
        waitForProgressBarToGoAway(getActivity());
        solo.finishOpenedActivities();
    }
}
