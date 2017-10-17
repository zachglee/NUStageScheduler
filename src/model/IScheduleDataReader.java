package model;

import java.util.List;

/**
 * Describes functionality for a reader object that reads data necessary
 * for scheduling (people, availabilities, locations, groups, events, etc)
 * from text files, and makes it accessible.
 */
public interface IScheduleDataReader {

  List<IUnscheduledEvent> getEventsToSchedule();

  //TODO a getConflicts() method

  //TODO maybe some other getPeople, getGroups, get whatever methods

}
