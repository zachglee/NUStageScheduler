package model;

/**
 * Describes functionality for a schedule that is meant to be
 * a final product, mainly for the purpose of carrying and
 * displaying information.
 */
public interface ISchedule {

  //for now we'll just have a toString()
  String toString();

  //later maybe ill have other methods here for displaying this in different ways
  //and for getting straight info from the schedule, like people, groups, events
  //maybe convenience querying methods as well

  /**
   * Returns the event at the given time and location.
   * Returns null if no such event can be found.
   * @param time the time to get the event at
   * @param location the location to get the event at
   * @return the event at the given time and location
   */
  IScheduledEvent getEventAt(ITimeSet time, ILocation location);

  /**
   * Returns the event at the given time and location.
   * Returns null if no such event can be found.
   * @param time the time to get the event at
   * @param locationName the name of the location to get the event at
   * @return the event at the given time and location
   */
  IScheduledEvent getEventAt(ITimeSet time, String locationName);

  /**
   * Returns the info in this schedule as a user friendly string
   * Very similar to toString, but gives times as human readable ones
   * (e.g. 8:00pm instead of 20.0)
   * @return the info in this schedule as a user friendly string
   */
  String toTextOutput();
}
