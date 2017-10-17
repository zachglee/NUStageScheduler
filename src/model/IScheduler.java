package model;

import java.util.List;

/**
 * Describes functionality for a class meant to control and
 * carry out the process of constructing a schedule.
 */
public interface IScheduler {

  /**
   * Returns the Schedule object representing the most optimal schedule
   * we can find, given the events we had to schedule and the availabilities
   * of people and locations involved, and this Scheduler's chunkSize.
   * @param toSchedule the events to schedule
   * @return the most optimal schedule we can find with the given data
   */
  ISchedule schedule(List<IUnscheduledEvent> toSchedule);
}
