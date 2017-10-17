package model;

/**
 * Describes functionality for an event that has already been scheduled.
 */
public interface IScheduledEvent {
  //nothing for now

  ITimeSet getTime();

  IGroup getGroup();

  ILocation getLocation();

  String getName();

  /**
   * Returns the info in this event as a user friendly string
   * @return the info in this event as a user friendly string
   */
  String toTextOutput();
}
