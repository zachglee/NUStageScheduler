package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an unscheduled event, with all the information a
 * fully scheduled event would have, with the exception of a concrete
 * time it is scheduled at. Also has slightly different functionality.
 */
public class UnscheduledEvent implements IUnscheduledEvent {

  private String name;
  private IGroup group;
  private ILocation location;
  private double duration;
  private int priority;

  //TODO make ScheduleState

  /**
   * Constructs an UnscheduledEvent with the given name, group of people, location, duration and priority level
   * @param name the name of the event
   * @param group the group of people who are to be at the event
   * @param location the location of the event
   * @param duration the duration of the event
   * @param priority the priority level of the event -- higher priority events are scheduled first
   */
  UnscheduledEvent(String name, IGroup group, ILocation location, double duration, int priority) {
    this.name = name;
    this.group = group;
    this.location = location;
    this.duration = duration;
    this.priority = priority;
  }

  /**
   * Constructs a dummy UnscheduledEvent.
   */
  UnscheduledEvent() {
    this.name = "DummyEvent";
    this.group = new Group();
    this.location = new Location("DummyLocation");
    this.duration = 0;
    this.priority = 0;
  }

  @Override
  public ITimeSet getAvailability() {
    return this.group.getAvailability().subtract(this.location.getUnavailability());
  }

  @Override
  public ITimeSet getOptionSpace(double chunkSize) {
    ITimeSet ret = new TimeSet();
    List<ITimeSet> options = this.getOptions(chunkSize);
    for (ITimeSet option : options) {
      ret = ret.union(option);
    }
    return ret;
  }

  @Override
  public List<ITimeSet> getOptions(double chunkSize) {
    return this.getAvailability().allTimesOfDuration(this.getDuration(), chunkSize);
  }

  @Override
  public boolean independentFrom(IUnscheduledEvent that) {
    return this.location != that.getLocation()
        && this.group.getCommonMembersWith(that.getGroup()).size() == 0;
  }

  //runs in O(n) time where n is number of events to schedule
  @Override
  public IOptionMetric consider(ITimeSet considered, Map<IUnscheduledEvent, ITimeSet> toBeScheduled, double chunkSize) {
    return new OptionMetric(this, considered, toBeScheduled, chunkSize);
  }

  @Override
  public double getDuration() {
    return this.duration;
  }

  @Override
  public int getPriority() {
    return this.priority;
  }

  @Override
  public IGroup getGroup() {
    return this.group;
  }

  @Override
  public ILocation getLocation() {
    return this.location;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public IUnscheduledEvent copy() {
    return new UnscheduledEvent(
        this.name,
        this.group.copy(),
        this.location.copy(),
        this.duration,
        this.priority);
  }

  @Override
  public String toString() {
    return this.getName() + " @ " + this.getAvailability().toString();
  }

}
