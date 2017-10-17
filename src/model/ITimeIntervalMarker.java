package model;

/**
 * Describes functionality for the start or end point of a
 * time interval.
 */
public interface ITimeIntervalMarker {

  /**
   * Returns whether or not this TimeIntervalMarker is the start of an interval
   * or not. (Note that if this method returns false, it implies that this marks
   * the END of an interval.)
   * @return whether or not this is the start of an interval
   */
  boolean isStart();

  /**
   * Returns the time that this TimeIntervalMarker marks.
   * @return the time that this TimeIntervalMarker marks
   */
  double getTime();

  /**
   * Returns the time that the other end of this interval marks.
   * @return the time that the other end of this interval marks
   */
  double getCounterpartTime();

  /**
   * Returns a user friendly string representing the time that this interval marks
   * @return a user friendly string representing the time that this interval marks
   */
  String getTimeTextOutput();

  /**
   * Returns a user friendly string representing the time that
   * the other end of this interval marks
   * @return the time at the other end of this interval as a user friendly string
   */
  String getCounterpartTimeTextOutput();
}
