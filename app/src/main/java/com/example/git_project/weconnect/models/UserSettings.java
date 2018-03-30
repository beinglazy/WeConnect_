package com.example.git_project.weconnect.models;

/**
 * Created by Alpa on 25-03-2018.
 */

public class UserSettings {
    private static final String TAG = "UserSettings";
    private User mUser;
    private UserAccountSettings mUserAccountSettings;

    public UserSettings(User mUser, UserAccountSettings mUserAccountSettings) {
        this.mUser = mUser;
        this.mUserAccountSettings = mUserAccountSettings;
    }
    public UserSettings() {

    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public UserAccountSettings getmUserAccountSettings() {
        return mUserAccountSettings;
    }

    public void setmUserAccountSettings(UserAccountSettings mUserAccountSettings) {
        this.mUserAccountSettings = mUserAccountSettings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "mUser=" + mUser +
                ", mUserAccountSettings=" + mUserAccountSettings +
                '}';
    }
}
