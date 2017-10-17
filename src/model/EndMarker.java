package model;

/**
 * Represents the end of a time interval.
 */
public class EndMarker extends ATimeIntervalMarker {

  public EndMarker(double startTime, double endTime) {
    super(endTime, startTime);
    if (startTime > endTime) {
      throw new IllegalArgumentException(
          "You cannot construct a EndMarker whose end time is less than its start time.");
    }
  }

  @Override
  public boolean isStart() {
    return false;
  }
}
