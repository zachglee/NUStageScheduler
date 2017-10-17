package model;

import java.util.List;
import java.util.Map;

/**
 * Describes functionality for an unscheduled event, mainly functionality
 * that will be useful in deciding when and where to schedule this event.
 */
public interface IUnscheduledEvent {

  /**
   * Returns the most optimal time to schedule this event for, given that the
   * given events must still be scheduled, and given the given chunkSize.
   * Optimal means maximizing the number of options we have to place other events.
   * @param otherEvents events still to be scheduled
   * @param chunkSize the atomic unit of time to use
   * @return the most optimal time to schedule this event for
   */
  //ITimeSet getOptimalTime(List<IUnscheduledEvent> otherEvents, double chunkSize);

  /**
   * Returns the OptionMetric containing info about the option of scheduling this event at the given time.
   * (We take a map here since we want constant time access to the availabilities of events for efficiency.)
   * @param considered the time we are considering scheduling this event at
   * @param toBeScheduled the events that still need to be scheduled. (Map of events to their availabilities)
   * @param chunkSize the atomic smallest unit that our TimeSets should be made up of
   * @return the OptionMetric containing info about the option of scheduling this event at the given time
   */
  IOptionMetric consider(ITimeSet considered, Map<IUnscheduledEvent, ITimeSet> toBeScheduled, double chunkSize);

  /**
   * Returns the times during which this event could be scheduled. Takes into account both the availability
   * of its members, AND the availability of its location.
   * @return the times during which this event could be scheduled
   */
  ITimeSet getAvailability();

  /**
   * Returns the union of this event's options' times.
   * @param chunkSize the chunkSize to use to determine the options
   * @return the union of this event's options' times.
   */
  ITimeSet getOptionSpace(double chunkSize);

  /**
   * Returns the individual TimeSets that are the discrete options for scheduling this event, given
   * its current unavailability, the given chunkSize, and the event's duration.
   * @return the list of distinct times that are valid options for scheduling this event.
   */
  List<ITimeSet> getOptions(double chunkSize);

  /**
   * Returns whether or not this event is independent from the given event.
   * This means that it does not share any members and does not share a location.
   * Functionally, this means that if we schedule this event it will not affect the other.
   * @param that the event to check for independence with
   * @return whether or not this event is independent from the given event
   */
  boolean independentFrom(IUnscheduledEvent that);

  /**
   * Returns the duration of this event in hours.
   * @return the duration of this event in hours
   */
  double getDuration();

  /**
   * Returns the priority of this event. Higher priority events are scheduled before events with lower priority.
   * @return the priority of this event
   */
  int getPriority();

  /**
   * Returns the Group of people who are a part of this event.
   * @return the Group of people who are a part of this event
   */
  IGroup getGroup();

  /**
   * Returns this event's location
   * @return this event's location
   */
  ILocation getLocation();

  /**
   * Returns this event's name.
   * @return this event's name
   */
  String getName();

  /**
   * Returns a copy of this unscheduled event.
   * @return a copy of this unscheduled event
   */
  IUnscheduledEvent copy();

}
