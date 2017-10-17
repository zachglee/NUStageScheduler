package model;

import java.util.*;

/**
 * Carries information used to inform the construction of a schedule
 * containing given events, (people, groups, locations) as well as
 * various bookkeeping objects to facilitate scheduling efficiently.
 * Has the functionality necessary to construct such a schedule.
 */
public class Scheduler implements IScheduler {

  //maybe I don't even need normal fields, cuz all the info is within the events passed in?
  private List<IUnscheduledEvent> toSchedule;
  private List<IScheduledEvent> scheduled;
  private double chunkSize;

  //maybe some bookkeeping fields here
  private Map<IUnscheduledEvent, ITimeSet> eventAvailabilities;
  private IOptionMetric currentOptionMetric;
  private List<IUnscheduledEvent> freeEvents;
  //tracks which events have at least 1 option at a given time,
  //private Map<Double, List<IUnscheduledEvent>> optionDensities;
  //more bookkeeping

  public Scheduler(double chunkSize) {
    this.toSchedule = new ArrayList<IUnscheduledEvent>();
    this.freeEvents = new ArrayList<IUnscheduledEvent>();
    this.chunkSize = chunkSize;
    //optionDensities = new HashMap<Double, List<IUnscheduledEvent>>();
    //setup optionDensities with empty lists at the start times of all chunks
    /*for (double k = 0.0; k < 168.0; k += this.chunkSize) {
      optionDensities.put(k, new ArrayList<IUnscheduledEvent>());
    }*/
  }

  @Override
  public ISchedule schedule(List<IUnscheduledEvent> toSchedule) {
    if (toSchedule.size() <= 0) {
      throw new IllegalArgumentException("You cannot construct a schedule with no events.");
    }
    this.toSchedule = toSchedule;
    //setup
    System.out.println("//-----[Setup");
    this.scheduled = new ArrayList<IScheduledEvent>();
    updateEventAvailabilities();
    //updateOptionDensities();
    //updateFreeEvents();
    //setup for optionDensities: //TODO confirm this works, test
    /*for (IUnscheduledEvent e : this.toSchedule) {
      ITimeSet curOptionSpace = this.eventAvailabilities.get(e);
      List<ITimeSet> chunks = curOptionSpace.allTimesOfDuration(chunkSize, chunkSize);
      for (ITimeSet chunk : chunks) {
        double chunkStart = chunk.getIntervalMarkers().get(0).getTime();
        optionDensities.get(chunkStart).add(e);
      }
    }*/
    //we need a value for optionMetric, so set it as if we had just
    //scheduled an event for an empty TimeSet. This way it will count
    //every event's initial options without actually subtracting from them
    this.currentOptionMetric = new UnscheduledEvent().consider(
        new TimeSet(),
        eventAvailabilities,
        this.chunkSize);
    System.out.println("//-----Setup]");
    //loop through n times, scheduling n events, handling bookkeeping
    while (this.toSchedule.size() > 0) {
      scheduleNext();
    }
    return new Schedule(scheduled);
  }

  /**
   * Schedules the next event, at the optimal time, adding it
   * to the list of scheduled events.
   */
  private void scheduleNext() {
    if (this.toSchedule.size() > 0) { //make sure there are events to schedule
      IUnscheduledEvent next = getNext();
      List<ITimeSet> allOptions = next.getOptions(this.chunkSize);
      IOptionMetric bestSoFar = null;
      //find the best option
      System.out.println("allOptions for " + next.getName() + ": " + allOptions.toString());
      for (ITimeSet possible : allOptions) {
        System.out.println("Now counting options for the possible " + possible.toString()
            + " for event: " + next.getName());
        IOptionMetric possibleMetric = next.consider(possible, eventAvailabilities, this.chunkSize);
        //if possibleMetric is better than bestSoFar, its the new best
        if (((Comparable<IOptionMetric>)possibleMetric).compareTo(bestSoFar) > 0) {
          //debugging ---------------
          if (bestSoFar == null) {
            System.out.println("We're replacing null (bestSoFar) with a "
                + possibleMetric.totalOptions() + "-option.");
          } else {
            System.out.println("We're replacing a " + bestSoFar.totalOptions()
                + "-option with a " + possibleMetric.totalOptions() + "-option.");
          }
          //debugging ----------------
          bestSoFar = possibleMetric;
        }
      }
      if (bestSoFar != null) {
        scheduleEvent(next, bestSoFar);
      } else {
        //TODO eventually handle this instead of just throwing exception
        throw new IllegalStateException("Could not find any places to schedule this: " + next.toString());
      }
    } else {
      throw new IllegalStateException(
          "You cannot schedule next if there is no next event to schedule.");
    }
  }

  /**
   * Helper that updates the map providing constant time access to the availabilities
   * of events. Gets called whenever those availabilities change, aka, whenever we
   * schedule a new event. (runs in O(n) time)
   */
  private void updateEventAvailabilities() {
    //TODO could do optimization, since i can identify some events whose availabilities def wont change
    this.eventAvailabilities = new HashMap<IUnscheduledEvent, ITimeSet>();
    for (IUnscheduledEvent e : this.toSchedule) {
      this.eventAvailabilities.put(e, e.getOptionSpace(this.chunkSize));
    }
  }

  //TODO TEST ALL OF THIS ALSO WRITE JAVADOC
  /*private void updateOptionDensities(/*IUnscheduledEvent event, IOptionMetric option) {
    //reset optionDensities
    this.optionDensities = new HashMap<Double, List<IUnscheduledEvent>>();
    //repopulate optionDensities with the current values
    for (IUnscheduledEvent e : this.toSchedule) {
      ITimeSet curOptionSpace = this.eventAvailabilities.get(e);
      List<ITimeSet> chunks = curOptionSpace.allTimesOfDuration(chunkSize, chunkSize);
      for (ITimeSet chunk : chunks) {
        double chunkStart = chunk.getIntervalMarkers().get(0).getTime();
        //if there's no entry there in the map, make an empty list there so we can add one
        if (optionDensities.get(chunkStart) == null) {
          optionDensities.put(chunkStart, new ArrayList<IUnscheduledEvent>());
        }
        optionDensities.get(chunkStart).add(e);
      }
    }*/
    //Runs in roughly O(n) time
    //remove this event from the entries for its option space in optionDensities
    /*ITimeSet optionSpace = event.getOptionSpace(this.chunkSize);
    removeOptions(event, optionSpace);
    List<ITimeSet> optionChunks = option.getOption().allTimesOfDuration(chunkSize, chunkSize);
    //iterate through all events, finding events that share a location or member with the given event
    //remove those events from the time of the given option, in optionDensities
    for (IUnscheduledEvent e : this.toSchedule) {
      if (!e.independentFrom(event)) {
        for (ITimeSet chunk : optionChunks) {
          double chunkStart = chunk.getIntervalMarkers().get(0).getTime();
          //TODO make a less naive method than just removing, using removeOptions() then add back in
          optionDensities.get(chunkStart).remove(e);
          //if there's exactly 1 element, add that element to freeEvents
          if (optionDensities.get(chunkStart).size() == 1) {
            this.freeEvents.add(optionDensities.get(chunkStart).get(0));
            System.out.println("ADDED A FREE EVENT: " + optionDensities.get(chunkStart).get(0));
          }
        }
      }
    }
  }*/


  /*private void updateFreeEvents() {
    for (Map.Entry<Double, List<IUnscheduledEvent>> entry : optionDensities.entrySet()) {
      double time = entry.getKey();
      List<IUnscheduledEvent> curEvents = optionDensities.get(time);
      //if there is only a single event with an option at time:
      if (curEvents.size() == 1) {
        //add that one event to freeEvents
        freeEvents.add(curEvents.get(0));
        System.out.println("Added FREE EVENT: " + curEvents.get(0).toString());
        //remove it from optionDensities so we don't think its a free event again later
        optionDensities.put(time, new ArrayList<IUnscheduledEvent>());
      }
    }
  }*/

  /**
   * Returns the next event that should be scheduled. Does this in O(n) time
   * by looping through toSchedule and finding the min with an EventPriorityComparator
   * @return the next event that should be scheduled
   */
  private IUnscheduledEvent getNext() { //TODO try and use a priorityQueue
    //if there are 'free' events, use them
    if (!freeEvents.isEmpty()) {
      return freeEvents.remove(0);
    }
    EventPriorityComparator comp = new EventPriorityComparator();
    IUnscheduledEvent highestPriority = null;
    for (IUnscheduledEvent e : toSchedule) {
      if (highestPriority == null) {
        highestPriority = e;
      } else {
        if (comp.compare(e, highestPriority) < 0) {
          //this e should be scheduled before highestPriority,
          //so make it the new highestPriority
          highestPriority = e;
        }
      }
    }
    toSchedule.remove(highestPriority);
    return highestPriority;
  }

  /**
   * Schedules the given event at the time provided by the given optionMetric.
   * Updates all bookkeeping data appropriately.
   * @param event the event to schedule
   * @param optionMetric the optionMetric representing the choice we made for scheduling the event
   */
  private void scheduleEvent(IUnscheduledEvent event, IOptionMetric optionMetric) {
    this.scheduled.add(new ScheduledEvent(event, optionMetric.getOption()));
    System.out.println("SCHEDULED ----- " + event.getName() + ", at " + optionMetric.getOption());
    this.currentOptionMetric = optionMetric;
    updateEventAvailabilities(); //O(n) time
    //update the optionDensities array
    //updateOptionDensities(/*event, optionMetric*/); //O(n) time
    //updateFreeEvents();
  }

  /**
   * Comparator class for determining which UnscheduledEvents should be scheduled first.
   * Takes multiple factors into account, including:
   * - whether an event is 'free' or not
   * - user provided priority levels
   * - number of options
   * - duration
   * - group size
   */
  private final class EventPriorityComparator implements Comparator<IUnscheduledEvent> {

    @Override
    public int compare(IUnscheduledEvent o1, IUnscheduledEvent o2) {
      //TODO implement freeness timetable
      //check freeness -- needs to be within Scheduler
      if (o1.getPriority() != o2.getPriority()) {
        return o2.getPriority() - o1.getPriority();
      }
      //check number of options -- events with less options should be scheduled first
      if (currentOptionMetric.optionsOf(o1) != currentOptionMetric.optionsOf(o2)) {
        return currentOptionMetric.optionsOf(o1) - currentOptionMetric.optionsOf(o2);
      }
      if (o1.getDuration() != o2.getDuration()) {
        double difference = o2.getDuration() - o1.getDuration();
        if (difference < 0) {
          return -1;
        } else if (difference > 0) {
          return 1;
        } else {
          return 0;
        }
      }
      if (o1.getGroup().size() != o2.getGroup().size()) {
        return o2.getGroup().size() - o1.getGroup().size();
      }
      return 0;
    }
  }
}
