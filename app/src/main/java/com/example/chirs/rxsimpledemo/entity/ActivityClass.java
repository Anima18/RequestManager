package com.example.chirs.rxsimpledemo.entity;

/**
 * Created by Admin on 2015/10/21.
 */
public class ActivityClass {
    private String activityName;
    private Class activityClass;

    public ActivityClass() {}

    public ActivityClass(String activityName, Class activityClass) {
        this.activityName = activityName;
        this.activityClass = activityClass;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }

    @Override
    public String toString() {
        return activityName;
    }

}
