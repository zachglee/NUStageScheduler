package model;

/**
 * Describes functionality for a location at which events
 * are to be scheduled.
 */
public interface ILocation {

  //TODO make this real thing

  /**
   * Returns the times at which this location is in use or otherwise unavailable.
   * @return the times at which this location is unavailable
   */
  ITimeSet getUnavailability();

  /**
   * Adds the given scheduled event to this location, and updates this
   * location's information accordingly.
   * @param event the event to add
   */
  void addEvent(IScheduledEvent event);

  /**
   * Returns the name of this location.
   * @return the name of this location
   */
  String getName();

  /**
   * Returns a copy of this location.
   * @return a copy of this location
   */
  ILocation copy();

  /**
   * Resets this Location's events and unavailability to their original state,
   * as they were before anything was scheduled.
   */
  void reset();

}
