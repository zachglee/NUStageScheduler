package model;

/**
 * Represents an already scheduled event in a schedule.
 */
public class ScheduledEvent implements IScheduledEvent {

  private String name;
  private IGroup group;
  private ITimeSet time;
  private ILocation location;

  public ScheduledEvent(IUnscheduledEvent event, ITimeSet time) {
    this.name = event.getName();
    this.group = event.getGroup(); //TODO what if getGroup returns a copy?
    this.time = time;
    this.location = event.getLocation();
    //now update the availabilities of all people belonging to this event,
    //they are no longer available at this event's time
    for (IPerson p : this.group.getMembers()) {
      p.setCurrentAvailability(p.getCurrentAvailability().subtract(time));
    }
    //update this location's unavailability -- it is no longer available at this event's time
    this.location.addEvent(this);
  }

  //private constructor for use by copy()
  private ScheduledEvent(String name, IGroup group, ITimeSet time, ILocation location) {
    this.name = name;
    this.group = group;
    this.time = time;
    this.location = location;
  }

  @Override
  public ITimeSet getTime() {
    return this.time;
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
  public String toTextOutput() {
    String ret = this.name + " - " + this.time.toTextOutput() + "@" + this.location.toString();
    ret += "\n  " + this.group.toString();
    return ret;
  }

  @Override
  public String toString() {
    String ret = this.name + " - " + this.time.toString() + "@" + this.location.toString();
    ret += "\n  " + this.group.toString();
    return ret;
  }
}
