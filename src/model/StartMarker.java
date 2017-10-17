package model;

/**
 * Represents the start of a time interval.
 */
public class StartMarker extends ATimeIntervalMarker {

  public StartMarker(double startTime, double endTime) {
    super(startTime, endTime);
    if (startTime > endTime) {
      throw new IllegalArgumentException(
          "You cannot construct a StartMarker whose start time is greater than its end time.");
    }
  }

  @Override
  public boolean isStart() {
    return true;
  }
}
