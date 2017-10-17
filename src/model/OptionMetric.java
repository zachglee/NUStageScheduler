package model;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Carries information about a scheduling option, stored with efficient access in mind.
 * Mainly includes information that will be useful in determining this option's
 * optimality in a given schedule.
 */
public class OptionMetric implements IOptionMetric, Comparable<IOptionMetric>{

  private ITimeSet option;
  private int totalOptions;
  private Map<IUnscheduledEvent, EventInfo> eventInfoMap;
  private double chunkSize;

  public OptionMetric(
      IUnscheduledEvent event,
      ITimeSet considered,
      Map<IUnscheduledEvent, ITimeSet> toBeScheduled,
      double chunkSize) {
    eventInfoMap = new HashMap<IUnscheduledEvent, EventInfo>(); //initialize map
    this.option = considered;
    this.chunkSize = chunkSize;
    if (!considered.intersect(event.getAvailability()).equals(considered)) {
      throw new IllegalArgumentException(
          "You cannot consider scheduling an event during a TimeSet that is not within its availability.");
    } //TODO don't really know if i want this ^^^ but im keeping it here for now for safety
    int totalOptions = 0;
    for (Map.Entry<IUnscheduledEvent, ITimeSet> e : toBeScheduled.entrySet()) {
      IUnscheduledEvent curEvent = e.getKey();
      ITimeSet curTimeSet = e.getValue();
      int curEventOptions;
      if (!event.equals(curEvent)) { //make sure we don't count ourselves
        if (event.getLocation().equals(curEvent.getLocation())) { //if current event and this have same location
          curEventOptions
              = curTimeSet.subtract(considered).allTimesOfDuration(curEvent.getDuration(), chunkSize).size();
              //System.out.println("        (Location-Shared-Case)");
        } else { //if current event and this DO NOT have the same location
          if (event.getGroup().getCommonMembersWith(curEvent.getGroup()).size() > 0) { //if current event and this share members
            //if they have common members, we can't count the time we're considering as part of their options
            //System.out.println("        (Members-Shared-Case)");
            curEventOptions
                = curTimeSet.subtract(considered).allTimesOfDuration(curEvent.getDuration(), chunkSize).size();
          } else { //if current even and this DO NOT share members
            //we can just count their options as is, since they're unaffected by us scheduling this event
            //System.out.println("        (None-Shared-Case)");
            curEventOptions = curTimeSet.allTimesOfDuration(curEvent.getDuration(), chunkSize).size();
          }
        }
        totalOptions += curEventOptions;
        eventInfoMap.put(curEvent, new EventInfo(curTimeSet, curEventOptions));
        System.out.println("      Just counted " + curEventOptions + " options for " + curEvent.getName()
            + ", total of: " + totalOptions);
      }
      //System.out.println("Total options is now: " + totalOptions + ", after scheduling " + curEvent.getName());
    }
    //System.out.println("");
    this.totalOptions = totalOptions;
  }

  @Override
  public ITimeSet getOption() {
    return this.option;
  }

  @Override
  public int totalOptions() {
    return this.totalOptions;
  }

  @Override
  public int optionsOf(IUnscheduledEvent event) {
    return this.eventInfoMap.get(event).options();
  }

  @Override
  public boolean hasZero() {
    for (EventInfo e : this.eventInfoMap.values()) {
      if (e.options() == 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public double chunkSize() {
    return this.chunkSize;
  }

  //if this returns a positive value, it means that this is a BETTER OptionMetric than that
  //negative means this is WORSE than that
  @Override
  public int compareTo(IOptionMetric that) {
    if (that == null) {
      return 1;
    }
    boolean thisHasZero = this.hasZero();
    boolean thatHasZero = that.hasZero();
    if (thisHasZero && thatHasZero) {
      return 0;
    } else if (thisHasZero && !thatHasZero) {
      return -1;
    } else if (!thisHasZero && thatHasZero) {
      return 1;
    } else {
      return this.totalOptions() - that.totalOptions();
      //TODO add a fragility clause
    }
  }

  private final class EventInfo {

    private ITimeSet availability;
    private int options;

    EventInfo(ITimeSet availability, int options) {
      this.availability = availability;
      this.options = options;
    }

    public ITimeSet availability() {
      return this.availability;
    }

    public int options() {
      return this.options;
    }
  }
}
