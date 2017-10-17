package model;

import com.sun.corba.se.spi.ior.IORTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes functionality for a set of time intervals, namely
 * set arithmetic functionality. Note that none of an ITimeSet's
 * methods mutate it.
 */
public interface ITimeSet {

  /**
   * Returns a copy of the set of time intervals that make up this
   * TimeSet.
   * @return the set of time intervals making up this TimeSet
   */
  List<ITimeIntervalMarker> getIntervalMarkers();

  /**
   * Returns a number 0 through 6 representing what day of the week this
   * TimeSet's earliest time begins during. (0=monday, 6=sunday)
   * @return the index of the day of the week during which this TimeSet begins
   */
  int getWeekdayIndex();

  /**
   * Returns the TimeSet that is the union of this TimeSet and
   * the given TimeSet.
   * @param that the TimeSet to union to this one
   * @return the union of this TimeSet and the given TimeSet
   */
  ITimeSet union(ITimeSet that);

  /**
   * Returns the TimeSet that is the intersection of this TimeSet and
   * the given TimeSet.
   * @param that the TimeSet to intersect with this one
   * @return the intersection of this TimeSet and the given TimeSet
   */
  ITimeSet intersect(ITimeSet that);

  /**
   * Returns the TimeSet that is the intersection of this TimeSet and
   * the all the TimeSets in the given list.
   * @param timeSets The TimeSets to intersect
   * @return the intersection of this TimeSet and the ones in the given list
   */
  ITimeSet intersect(List<ITimeSet> timeSets);

  /**
   * Returns the TimeSet that is the complement of this TimeSet.
   * For our purposes the universe is the set of all times on the interval [0, 168].
   * @return the TimeSet that is the complement of this TimeSet
   */
  ITimeSet complement();

  /**
   * Returns the TimeSet produced by subtracting the given TimeSet from
   * this TimeSet.
   * @param that the TimeSet to subtract from this one
   * @return this TimeSet with the given TimeSet subtracted from it.
   */
  ITimeSet subtract(ITimeSet that);

  /**
   * Returns the list of all continuous TimeSets of the given duration contained
   * within this TimeSet, with the stipulation thtat the TimeSets must be able to be
   * broken down into an integer number of chunks of the given chunk size. (For this
   * method to work best, you should give durations that are divisible by the chunksize,
   * and all the intervals in the TimeSet you call this method on should be divisible by
   * the chunksize as well.)
   * @param duration the duration of the TimeSets to return
   * @param chunkSize the size of the chunks TimeSets must be made up of
   * @return the list of all continuous TimeSets of the given duration in this TimeSet
   */
  List<ITimeSet> allTimesOfDuration(double duration, double chunkSize);

  /**
   * Returns the TimeSet that contains all the same times as this one, but shifted forward
   * by the given number of days. (or backward if the number of days is negative)
   * @param days the number of days to shift by
   * @return the TimeSet that is the same as this one, but shifted by the given number of days
   */
  ITimeSet addDays(int days);

  /**
   * Returns this TimeSet as a readable string meant to be seen by a user
   * @return this TimeSet as a readable string mean tto be seen by a user
   */
  String toTextOutput();
}
