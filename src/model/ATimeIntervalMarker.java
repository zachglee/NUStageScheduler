package model;

import java.util.Objects;

/**
 * Abstract class outlining the general structure of a start or end point of
 * a time interval. Represents times as doubles measuring hours,
 * in the range [0, 168] for each of the 168 possible hours in a 7-day week.
 */
public abstract class ATimeIntervalMarker implements ITimeIntervalMarker {

  private double time;
  private double counterpartTime;

  public ATimeIntervalMarker(double time, double counterpartTime) {
    if (time < 0 || counterpartTime < 0) {
      throw new IllegalArgumentException("Times must be positive.");
    }
    if (time == counterpartTime) {
      throw new IllegalArgumentException("The given times for this interval cannot be equal.");
    }
    this.time = time;
    this.counterpartTime = counterpartTime;
  }

  @Override
  public double getTime() {
    return time;
  }

  @Override
  public double getCounterpartTime() {
    return counterpartTime;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ATimeIntervalMarker) {
      ATimeIntervalMarker that = (ATimeIntervalMarker) obj;
      return this.isStart() == that.isStart()
          && this.getTime() == that.getTime()
          && this.getCounterpartTime() == that.getCounterpartTime();
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, counterpartTime, this.isStart());
  }

  @Override
  public String toString() {
    String type;
    if (this.isStart()) {
      return "Start marker of interval [" + this.getTime() + ", " + this.getCounterpartTime() + "]";
    } else {
      return "End marker of interval [" + this.getCounterpartTime() + ", " + this.getTime() + "]";
    }
  }

  @Override
  public String getTimeTextOutput() {
    return numericTimeToString(this.getTime());
  }

  @Override
  public String getCounterpartTimeTextOutput() {
    return numericTimeToString(this.getCounterpartTime());
  }

  //non-public helper functions

  /**
   * Returns the given time as a String of the format h:mm[pm/am],
   * aka the format it would appear in on a digital clock.
   * [pm/am] indicates that either pm or am will appear after the time.
   * @param time the time to convert to h:mm format
   * @return the given time as a String, in the form it would appear in a digital clock
   */
  private String numericTimeToString(double time) {
    String ampm = "am";
    String hoursComponent = "";
    String minutesComponent = "";
    double hourOfDay = (time % 24.0); //get hour of day
    hoursComponent = Integer.toString((int) Math.floor(hourOfDay));
    if (hourOfDay >= 12.0) {
      ampm = "pm";
    }
    double minutesOfHour = (hourOfDay % 1.0); //get minutes component of hour as a double
    //convert the decimal representing minutes to an int in range [0, 59]
    double minutesAsDouble = minutesOfHour * 60;
    int minutesAsInteger = (int) Math.floor(minutesAsDouble);
    //properly convert minutesAsInteger to a String, padding zeroes as necessary
    if (minutesAsInteger < 10) {
      minutesComponent = "0" + Integer.toString(minutesAsInteger);
    } else {
      minutesComponent = Integer.toString(minutesAsInteger);
    }
    return hoursComponent + ":" + minutesComponent + ampm;
  }
}
