package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a schedule that is meant to be
 * a final product, mainly for the purpose of carrying and
 * displaying information.
 */
public class Schedule implements ISchedule {

  private String name;
  private List<IScheduledEvent> events;
  private Map<ITimeSet, List<IScheduledEvent>> timeEventMap; //for constant time access to events by time
  private String[] weekdays =
      {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

  public Schedule(String name, List<IScheduledEvent> events) {
    this.name = name;
    this.events = events;
    this.timeEventMap = new HashMap<ITimeSet, List<IScheduledEvent>>();
    for (IScheduledEvent e : events) {
      ITimeSet time = e.getTime();
      if (timeEventMap.get(time) == null) {
        timeEventMap.put(time, new ArrayList<IScheduledEvent>());
      }
      timeEventMap.get(time).add(e);
    }
  }

  public Schedule(List<IScheduledEvent> events) {
    this.name = "[PlaceholderName]";
    this.events = events;
    this.timeEventMap = new HashMap<ITimeSet, List<IScheduledEvent>>();
    for (IScheduledEvent e : events) {
      ITimeSet time = e.getTime();
      if (timeEventMap.get(time) == null) {
        timeEventMap.put(time, new ArrayList<IScheduledEvent>());
      }
      timeEventMap.get(time).add(e);
    }
  }

  @Override
  //TODO make nicely formatted and cleaned up, this is just rough
  public String toString() {
    String ret = this.name + ":\n\n";
    for (IScheduledEvent e : this.events) {
      ret += e.toString() + "\n";
    }
    return ret;
  }

  @Override
  public String toTextOutput() {
    String newline = System.lineSeparator();
    String ret = this.name + ":" + newline + newline;
    //first make lists of what events are on what days
    List<List<IScheduledEvent>> theWeek = new ArrayList<List<IScheduledEvent>>();
    //populate theWeek with 7 empty arraylists of events
    for (int i = 1; i <= 7; i++) {
      theWeek.add(new ArrayList<IScheduledEvent>());
    }
    //iterate through all events we have and put them into the correct weekdays
    for (IScheduledEvent e : this.events) {
      //determine weekday index [0-6]
      int weekdayIndex = e.getTime().getWeekdayIndex(); //needs testing
      theWeek.get(weekdayIndex).add(e); //add the event to the proper weekday list
      System.out.println("Added to weekday " + weekdayIndex + ": " + e.toString());
    }
    //now print all the events on each weekday
    for (int i = 0; i < theWeek.size(); i++) {
      ret += this.weekdays[i] + ":" + newline + "---" + newline; //print the day name
      List<IScheduledEvent> weekdayEvents = theWeek.get(i);
      for (IScheduledEvent e : weekdayEvents) {
        ret += e.toTextOutput() + newline;
      }
    }
    return ret;
  }

  @Override
  public IScheduledEvent getEventAt(ITimeSet time, ILocation location) {
    List<IScheduledEvent> eventsAtTime = this.timeEventMap.get(time);
    for (IScheduledEvent e : eventsAtTime) {
      if (e.getLocation().equals(location)) {
        return e;
      }
    }
    return null;
  }

  @Override
  public IScheduledEvent getEventAt(ITimeSet time, String locationName) {
    List<IScheduledEvent> eventsAtTime = this.timeEventMap.get(time);
    for (IScheduledEvent e : eventsAtTime) {
      if (e.getLocation().getName().equals(locationName)) {
        return e;
      }
    }
    return null;
  }

}
