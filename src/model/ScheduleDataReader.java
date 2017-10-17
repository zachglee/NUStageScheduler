package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents a reader object that reads data necessary
 * for scheduling (people, availabilities, locations, groups, events, etc)
 * from text files, and makes it accessible.
 */
public class ScheduleDataReader implements IScheduleDataReader {

  //these maps map names of things to the objects themselves
  private Map<String, IPerson> personMap;
  private Map<String, IGroup> groupMap;
  private Map<String, ILocation> locationMap;
  private List<IUnscheduledEvent> eventList;
  private ITimeSet schedulingWindows;

  private Map<String, Integer> weekdayShiftMap = new HashMap<String, Integer>();

  /**
   * Constructs a ScheduleDataReader that reads input from the given files and parses it
   * into data representable by the model. The last three parameters give what general
   * areas of time we are looking to schedule within.
   * @param people the file describing the people and their unavailabilities
   * @param groups the file describing groups and their members
   * @param locations the file describing locations
   * @param events the file describing unscheduled events to be scheduled
   * @param conflicts the file describing individuals' one-time conflicts for this week
   * @param window the file describing the window of time during a given day during which we will schedule
   * @param fromDay the earliest day of the week we are willing to schedule on (0 is monday, 6 is sunday)
   * @param toDay the latest day of the week we are willing to schedule on
   */
  public ScheduleDataReader(
      File people,
      File groups,
      File locations,
      File events,
      File conflicts,
      File window,
      /*ITimeSet schedulingWindow,*/
      int fromDay,
      int toDay) {
    //TODO add a conflict.txt parameter and processing <-- did i already do this?
    personMap = new HashMap<String, IPerson>();
    groupMap = new HashMap<String, IGroup>();
    locationMap = new HashMap<String, ILocation>();
    eventList = new ArrayList<IUnscheduledEvent>();
    Scanner windowReader;
    Scanner personReader;
    Scanner conflictReader;
    Scanner groupReader;
    Scanner locationReader;
    Scanner eventReader;
    try {
      windowReader = new Scanner(window);
      personReader = new Scanner(people);
      conflictReader = new Scanner(conflicts);
      groupReader = new Scanner(groups);
      locationReader = new Scanner(locations);
      eventReader = new Scanner(events);
    } catch (FileNotFoundException e ) {
      //TODO this is crude, fix later
      throw new IllegalArgumentException(e.getMessage());
    }
    //get the scheduling window as a TimeSet
    //TODO MAKE SURE THIS WORKS!!!
    ITimeSet schedulingWindow;
    if (windowReader.hasNextLine()) {
      String curLine = windowReader.nextLine();
      schedulingWindow = rangeToTimeSet(curLine);
    } else {
      throw new IllegalArgumentException(
          "ERROR: Couldn't read from window.txt file to get the scheduling window.");
    }
    //make sure that schedulingWindow is within the first day
    if (!schedulingWindow.intersect(new TimeSet(24.0, 168.0)).equals(new TimeSet())) {
      throw new IllegalArgumentException(
          "The given scheduling window must be within the first day of the week " +
              "for it to be properly applied to the rest of the weekdays.");
    }
    if (fromDay > toDay || fromDay < 0 || toDay > 6) {
      throw new IllegalArgumentException("Given fromDay and toDay must be in the range [0, 6]," +
          " and fromDay must be less than or equal to toDay.");
    }
    initShiftMap();
    this.schedulingWindows = new TimeSet();
    for (int i = fromDay; i <= toDay; i++) {
      schedulingWindows = schedulingWindows.union(schedulingWindow.addDays(i));
    }
    //read in person data (unavailabilities)
    while(personReader.hasNextLine()) {
      String curLine = personReader.nextLine();
      String[] components = curLine.split("; |;");
      String name = components[0];
      ITimeSet totalUnavailability = new TimeSet();
      for (int i = 1; i < components.length; i++) {
        //process the textual unavailabilities into TimeSets
        String curComponent = components[i];
        totalUnavailability = totalUnavailability.union(weekdayToTimeSet(curComponent));
      }
      personMap.put(name, new Person(name, this.schedulingWindows.subtract(totalUnavailability)));
      System.out.println("Recorded: " + name + " : " + personMap.get(name).getCurrentAvailability().toString());
    }
    //read in conflict data (one-time unavailabilities) (Processed exactly the same as person data)
    while(conflictReader.hasNextLine()) {
      String curLine = conflictReader.nextLine();
      String[] components = curLine.split("; |;");
      String name = components[0];
      ITimeSet totalConflict = new TimeSet();
      for (int i = 1; i < components.length; i++) {
        //process the textual unavailabilities into TimeSets
        String curComponent = components[i];
        totalConflict = totalConflict.union(weekdayToTimeSet(curComponent));
      }
      IPerson conflictedPerson = personMap.get(name);
      conflictedPerson.setCurrentAvailability(
          conflictedPerson.getCurrentAvailability().subtract(totalConflict));
    }
    //read in group data TODO test
    while (groupReader.hasNextLine()) {
      String curLine = groupReader.nextLine();
      String[] components = curLine.split(" - | -|- |-");
      String name = components[0];
      String[] memberNames = components[1].split(", |,");
      Set<IPerson> members = new HashSet<IPerson>();
      for (int i = 0; i < memberNames.length; i++) {
        members.add(personMap.get(memberNames[i]));
      }
      this.groupMap.put(name, new Group(name, members));
      System.out.println("Recorded: " + groupMap.get(name).toString());
    }
    //read in location data
    while (locationReader.hasNextLine()) {
      String curLine = locationReader.nextLine();
      this.locationMap.put(curLine, new Location(curLine));
    }
    //read in events
    while (eventReader.hasNextLine()) {
      String curLine = eventReader.nextLine();
      String[] components = curLine.split(" @ | @|@ |@| - | -|- |-");
      String name = components[0];
      ILocation location = this.locationMap.get(components[1]);
      double duration = Double.parseDouble(components[2]);
      IGroup group = this.groupMap.get(components[3]);
      int priority = 1;
      if (components.length == 5) {
        priority = Integer.parseInt(components[4]);
      }
      this.eventList.add(new UnscheduledEvent(name, group, location, duration, priority));
    }
  }

  /**
   * Returns the TimeSet described by the given String.
   * Given string should be of the format: (braces are not part of format)
   * [Weekday] [StartTime][am/pm]-[EndTime][am/pm], [StartTime][am/pm]-[EndTime][am/pm] ...;
   * [StartTime] and [EndTime] components are given in [hours]:[minutes] format.
   * [am/pm] components are optional. If not given, pm will be the assumed default.
   * The ... means you can continue to add as many times as you'd like, of the same format,
   * separated by commmas, and ending on a semicolon.
   * @param times the String to convert to a TimeSet
   * @return the TimeSet described by the given String
   */
  private ITimeSet weekdayToTimeSet(String times) {
    String[] components = times.split(", ", 0);
    int weekdayShift = 0;
    ITimeSet ret = new TimeSet();
    for (int i = 0; i < components.length; i++) {
      String curComponent = components[i];
      if (i == 0) { //if we're on the first element
        //get the weekday
        String weekday = curComponent.substring(0, curComponent.indexOf(" "));
        weekdayShift = weekdayShiftMap.get(weekday);
        curComponent = curComponent.substring(curComponent.indexOf(" ") + 1, curComponent.length());
      }
      ret = ret.union(rangeToTimeSet(curComponent));
    }
    return ret.addDays(weekdayShift);
  }

  /**
   * Returns the TimeSet described by the given String.
   * The TimeSet is returned as if it was during the first day of the week,
   * but other methods that have info about the day may shift it properly.
   * Given String should be of the format: (braces not part of the formatting)
   * [hours]:[minutes][am/pm] - [hours]:[minutes][am/pm]
   * where the time preceding the hyphen is the start time of the range, and
   * the time following it is the end time. Spaces around the hyphen are optional.
   * The time may also be followed by a comma and will still be processed correctly.
   * @param timeRange the String describing the time to convert to a TimeSet
   * @return the TimeSet described by the given String
   */
  private ITimeSet rangeToTimeSet(String timeRange) {
    //TODO implement this
    String[] components = timeRange.split("-| - | -|- |, |,", 0);
    return new TimeSet(hmToTime(components[0]), hmToTime(components[1]));
  }

  /**
   * Returns the time, as a double in hours, described by the given String in hours:minutes format.
   * The time is returned as if it was during the first day of the week, but other methods that have
   * info about the day of the week may shift it properly.
   * Note: 12:00am will be treated as the very end of the day, so use 0:00am to refer to the beginning.
   * Given string should be of the format: (braces are not part of format)
   * [hours]:[minutes][am/pm]
   * [minutes] will be two digits. [hours] will be 1 or two digits.
   * [am/pm] component is optional. If not given, pm will be the assumed default.
   * @return the time in hours described by the given String in hours:minutes format
   */
  private double hmToTime(String hmTime) {
    String[] components = hmTime.split(":");
    double hours = Double.parseDouble(components[0]);
    double minutes = Double.parseDouble(components[1].substring(0, 2));
    double timeSetTime = hours + (minutes / 60.0);
    String ampm = "pm";
    if (components[1].length() > 2) { //if there's an am/pm component
      ampm = components[1].substring(2, components[1].length());
    }
    if (ampm.equals("pm") || !ampm.equals("am")) {
      timeSetTime += 12.0;
    }
    return timeSetTime;
  }

  private void initShiftMap() {
    weekdayShiftMap.put("mon", 0);
    weekdayShiftMap.put("Mon", 0);
    weekdayShiftMap.put("Monday", 0);
    weekdayShiftMap.put("tue", 1);
    weekdayShiftMap.put("Tue", 1);
    weekdayShiftMap.put("Tuesday", 1);
    weekdayShiftMap.put("wed", 2);
    weekdayShiftMap.put("Wed", 2);
    weekdayShiftMap.put("Wednesday", 2);
    weekdayShiftMap.put("thu", 3);
    weekdayShiftMap.put("Thu", 3);
    weekdayShiftMap.put("Thursday", 3);
    weekdayShiftMap.put("fri", 4);
    weekdayShiftMap.put("Fri", 4);
    weekdayShiftMap.put("Friday", 4);
    weekdayShiftMap.put("sat", 5);
    weekdayShiftMap.put("Sat", 5);
    weekdayShiftMap.put("Saturday", 5);
    weekdayShiftMap.put("sun", 6);
    weekdayShiftMap.put("Sun", 6);
    weekdayShiftMap.put("Sunday", 6);
  }

  @Override
  public List<IUnscheduledEvent> getEventsToSchedule() {
    return this.eventList;
  }
}
