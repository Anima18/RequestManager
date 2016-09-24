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

    /*@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(activityName).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActivityClass))
            return false;
        if (obj == this)
            return true;

        ActivityClass rhs = (ActivityClass) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(activityName, rhs.activityName).
                        isEquals();
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityClass that = (ActivityClass) o;

        return activityName != null ? activityName.equals(that.activityName) : that.activityName == null;

    }

    @Override
    public int hashCode() {
        return activityName != null ? activityName.hashCode() : 0;
    }
}
