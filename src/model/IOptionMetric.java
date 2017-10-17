package model;

/**
 * Describes functionality for accessing data necessary in determining
 * the optimality of this OptionMetric's option.
 */
public interface IOptionMetric {

  /**
   * Returns the option that this OptionMetric has data about.
   * @return the option this OptionMetric is measuring
   */
  ITimeSet getOption();

  /**
   * Returns the sum of scheduling options of all events still to schedule
   * in this ToSchedule.
   * @return the total number of scheduling options between all events in this ToSchedule
   */
  int totalOptions();

  /**
   * Returns the number of times we could schedule the given event, given this
   * ToSchedule's chunkSize and the other events to schedule.
   * @param event the event whose number of scheduling options to return
   * @return the number of scheduing options for the given event
   */
  int optionsOf(IUnscheduledEvent event);

  /**
   * Returns whether or not at least one of the events in this OptionMetric
   * have zero options.
   * @return whether or not this OptionMetric has an event with zero options
   */
  boolean hasZero();

  /**
   * Returns the chunkSize used in gathering data about this OptionMetric's option.
   * @return the chunkSize used in gathering this OptionMetric's data
   */
  double chunkSize();
}