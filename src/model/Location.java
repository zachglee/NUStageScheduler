package model;

import javax.swing.plaf.synth.SynthCheckBoxUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a location at which events are to be scheduled.
 */
public class Location implements ILocation {

  private String name;
  private ITimeSet unavailability;
  private List<IScheduledEvent> events;

  public Location(String name) {
    this.unavailability = new TimeSet();
    this.events = new ArrayList<IScheduledEvent>();
    this.name = name;
  }

  /**
   * Constructs a Location with the given name, unavailability and events.
   * (private, for use by copy())s
   * @param name the name of the Location
   * @param unavailability the unavailability of the Location
   * @param events the events scheduled at the Location
   */
  private Location(String name, ITimeSet unavailability, List<IScheduledEvent> events) {
    this.name = name;
    this.unavailability = unavailability;
    this.events = events;
  }

  @Override
  public ITimeSet getUnavailability() {
    return this.unavailability;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public ILocation copy() {
    return new Location(this.name, this.unavailability, this.events);
  }

  @Override
  public void reset() {
    unavailability = new TimeSet();
    events = new ArrayList<IScheduledEvent>();
  }

  @Override
  public void addEvent(IScheduledEvent event) {
    this.unavailability = this.unavailability.union(event.getTime());
    this.events.add(event);
  }

  @Override
  public String toString() {
    return name;
  }

  //TODO do i need an equals and hashcode?

  /*@Override
  public boolean equals(Object obj) {
    if (obj instanceof ILocation) {
      ILocation loc = (ILocation) obj;
      return loc.getName().equals(this.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }*/
}
