package model;

import java.math.BigDecimal;
import java.util.*;

/**
 * Represents a set of time intervals represented as doubles
 * denoting the beginnings and ends of different intervals.
 * Times are represented as doubles, and are measured in hours.
 * Times can range on the interval [0, 168], representing all 168
 * hours in a 7-day week.
 */
public class TimeSet implements ITimeSet {

  List<ITimeIntervalMarker> intervalMarkers;

  /**
   * Constructs a TimeSet containing a single time interval
   * with the given start and end time.
   * @param start the start time of the time interval
   * @param end the end time of the time interval
   */
  public TimeSet(double start, double end) {
    if (start > end) {
      throw new IllegalArgumentException(
          "The beginning of a time interval cannot be greater than its end.");
    }
    if (start < 0.0) {
      throw new IllegalArgumentException("Times cannot be negative.");
    }
    if (end > 168.0) {
      throw new IllegalArgumentException(
          "Times cannot be greater than 168, the number of hours in a 7-day week.");
    }
    intervalMarkers = new ArrayList<ITimeIntervalMarker>();
    intervalMarkers.add(new StartMarker(start, end));
    intervalMarkers.add(new EndMarker(start, end));
  }

  //TODO perhaps a convenience human friendly constructor in hours and minutes etc?

  /**
   * Constructs an empty TimeSet containing no times.
   */
  public TimeSet() {
    intervalMarkers = new ArrayList<ITimeIntervalMarker>();
  }

  /**
   * Constructs a TimeSet with the given interval markers.
   * @param intervalMarkers the interval markers to construct this TimeSet with
   */
  private TimeSet(List<ITimeIntervalMarker> intervalMarkers) {
    this.intervalMarkers = new ArrayList<ITimeIntervalMarker>();
    for (ITimeIntervalMarker intervalMarker : intervalMarkers) {
      this.intervalMarkers.add(intervalMarker);
    }
  }

  @Override
  public List<ITimeIntervalMarker> getIntervalMarkers() {
    ArrayList<ITimeIntervalMarker> ret = new ArrayList<ITimeIntervalMarker>();
    for (ITimeIntervalMarker intervalMarker : this.intervalMarkers) {
      ret.add(intervalMarker);
    }
    return ret;
  }

  @Override
  public int getWeekdayIndex() {
    double beginTime = this.intervalMarkers.get(0).getTime();
    int weekdayIndex = (int) Math.floor(beginTime / 24.0);
    if (weekdayIndex >= 0 && weekdayIndex <= 6) {
      return weekdayIndex;
    } else {
      throw new IllegalStateException(
          "Something went wrong and the calculated weekday index was not in range [0, 6]");
    }
  }

  @Override
  public ITimeSet union(ITimeSet that) {
    //make the worklist and populate it with all interval markers in this and the given TimeSet
    List<ITimeIntervalMarker> worklist = this.getIntervalMarkers();
    worklist.addAll(that.getIntervalMarkers());
    //sort the worklist
    Collections.sort(worklist, new TimeIntervalMarkerComparator());
    //setup
    List<ITimeIntervalMarker> ret = new ArrayList<ITimeIntervalMarker>();
    double opener = -1.0; //used to track starts of intervals to be added to the product TimeSet
    double targetEnd = -1.0;
    ITimeIntervalMarker ongoing = null;
    //traverse
    for (int i = 0; i < worklist.size(); i++) {
      ITimeIntervalMarker currentMarker = worklist.get(i);
      if (currentMarker.isStart()) {
        //if the marker is a start
        if (ongoing == null) {
          //if there is no ongoing, make this the ongoing
          ongoing = currentMarker;
          //and set the targetEnd to the start's end time
          targetEnd = currentMarker.getCounterpartTime();
          //and set the current marker's time as the opener
          opener = currentMarker.getTime();
        } else { //if there is ALREADY an ongoing
          //set the targetEnd to this marker's end time,
          //IF that end time is greater than the current targetEnd
          if (currentMarker.getCounterpartTime() > targetEnd) {
            targetEnd = currentMarker.getCounterpartTime();
          }
        }
      } else { //if the marker is an end
        if (ongoing == null) { //this shouldn't ever happen
          if (currentMarker.getTime() != targetEnd) {
            throw new IllegalStateException(
                "Something went wrong. We should never process an EndMarker before " +
                    "before we have processed a StartMarker and made it the ongoing.");
          }
        } else if (currentMarker.getTime() == targetEnd) { //if this marker is the targetEnd
          //add the proper interval to the product
          ret.add(new StartMarker(opener, targetEnd));
          ret.add(new EndMarker(opener, targetEnd));
          //reset the ongoing to be null
          ongoing = null;
        }
        //if the marker is an EndMarker and is NOT the targetEnd, do nothing and move on
      }
    }
    return new TimeSet(ret);
  }

  @Override
  public ITimeSet intersect(ITimeSet that) {
    List<ITimeSet> timeSetSingleton = new ArrayList<ITimeSet>();
    timeSetSingleton.add(that);
    return this.intersect(timeSetSingleton);
  }

  @Override
  public ITimeSet intersect(List<ITimeSet> timeSets) {
    //make the worklist and populate it with all interval markers in this and the given TimeSet
    List<ITimeIntervalMarker> worklist = this.getIntervalMarkers();
    for (ITimeSet t : timeSets) {
      worklist.addAll(t.getIntervalMarkers());
    }
    //sort the worklist
    Collections.sort(worklist, new TimeIntervalMarkerComparator());
    //setup
    List<ITimeIntervalMarker> ret = new ArrayList<ITimeIntervalMarker>();
    double opener = -1.0; //used to track starts of intervals to be added to the product TimeSet
    //tracks which intervals are ongoing:
    ITimeIntervalMarker[] ongoing = new ITimeIntervalMarker[timeSets.size() + 1];
    for (int i = 0; i < ongoing.length; i++) {
      ongoing[i] = null;
    }
    //traverse
    for (int i = 0; i < worklist.size(); i++) {
      ITimeIntervalMarker currentMarker = worklist.get(i);
      if (currentMarker.isStart()) { //if current marker is a start
        for (int o = 0; o < ongoing.length; o++) {
          if (ongoing[o] == null) {
            ongoing[o] = currentMarker;
            if (o == ongoing.length - 1) {
              //if this is the nth ongoing set where n is the number of sets in the intersection,
              //open the interval to add to the product, at this marker's position
              opener = currentMarker.getTime();
            }
            o = ongoing.length;
          }
        }

      } else { //if the current marker is an end
        if (ongoing[0] == null) { //this should never happen
          throw new IllegalStateException(
              "Something went wrong. We should never process an EndMarker before " +
                  "before we have processed a StartMarker and made it the ongoing1.");
        }
        if (ongoing[ongoing.length - 1] != null) {
          //if there are enough intervals ongoing for an intersection to be open,
          //close that open intersection at this mark and add it to the product
          ret.add(new StartMarker(opener, currentMarker.getTime()));
          ret.add(new EndMarker(opener, currentMarker.getTime()));
          opener = -1.0;
        }
        //check for which interval is now not ongoing because of this EndMarker
        for (int o = 0; o < ongoing.length; o++) {
          ITimeIntervalMarker intervalMarker = ongoing[o];
          //if the current EndMarker is the end of the o'th ongoing interval:
          if (intervalMarker.getCounterpartTime() == currentMarker.getTime()) {
            //shift everything past it in the list down one, overwriting it
            for (int k = o; k < ongoing.length; k++) {
              if (k == ongoing.length - 1) {
                ongoing[k] = null;
              } else {
                ongoing[k] = ongoing[k + 1];
              }
            }
            o = ongoing.length; //break out of this loop
          }
        }
      }
    }
    return new TimeSet(ret);
  }

  @Override
  public ITimeSet complement() {
    //make the worklist, which in this case is just a copy of this TimeSet's intervalMarkers
    List<ITimeIntervalMarker> worklist = this.getIntervalMarkers();
    //sort the worklist
    Collections.sort(worklist, new TimeIntervalMarkerComparator());
    //the list of interval markers to make the final product TimeSet out of:
    List<ITimeIntervalMarker> ret = new ArrayList<ITimeIntervalMarker>();
    double opener = 0.0; //used to track starts of intervals to be added to the product TimeSet
    //for every interval marker in this TimeSet's list of intervalMarkers
    for (int i = 0; i < worklist.size(); i++) {
      ITimeIntervalMarker currentMarker = worklist.get(i);
      if (currentMarker.isStart()) { //if currentMarker is a StartMarker
        if (currentMarker.getTime() > 0.0) {
          //add the proper intervals, starting from the last opener value we recorded,
          //and spanning to this StartMarker we just encountered
          if (opener != currentMarker.getTime()) { //but only if they represent a non-empty interval (start and end being the same)
            ret.add(new StartMarker(opener, currentMarker.getTime()));
            ret.add(new EndMarker(opener, currentMarker.getTime()));
          }
          opener = -1.0; //ensure that if for some reason we try to add another interval before a new
                         //opener value has been recorded, the negative time exception will be thrown
        } else { //if this StartMarker is at the beginning of the universe, 0.0:
          //just reset opener and don't add anything to the product
          opener = -1.0;
        }
      } else { //currentMarker must be an EndMarker
        if (currentMarker.getTime() < 168.0) {//if it's not at the very end of the universe
          opener = currentMarker.getTime(); //we record a new opener
        }
      }
    } //when we reach the end of the list, we add one last interval going from opener to 168 (the end of the universe)
    if (opener >= 0.0) { //BUT only if opener has actually been assigned a value at this point
      ret.add(new StartMarker(opener, 168.0));
      ret.add(new EndMarker(opener, 168.0));
    }
    return new TimeSet(ret);
  }

  @Override
  public ITimeSet subtract(ITimeSet that) {
    return this.intersect(that.complement());
  }

  @Override
  public List<ITimeSet> allTimesOfDuration(double duration, double chunkSize) {
    if (Math.abs(duration % chunkSize) > .01) {
      throw new IllegalArgumentException(
          "The given duration must be evenly divisible by the given chunkSize.");
    }
    if (chunkSize <= 0 || duration <= 0) {
      throw new IllegalArgumentException("The given duration and chunkSize must be positive.");
    }
    //make the worklist, which in this case is just a copy of this TimeSet's intervalMarkers
    List<ITimeIntervalMarker> worklist = this.getIntervalMarkers();
    //sort the worklist
    Collections.sort(worklist, new TimeIntervalMarkerComparator());
    //the list of TimeSets to return:
    List<ITimeSet> ret = new ArrayList<ITimeSet>();
    //I had to use BigDecimals here because of precision issues
    for (int i = 0; i < worklist.size(); i++) {
      ITimeIntervalMarker currentMarker = worklist.get(i);
      if (currentMarker.isStart()) {
        for (BigDecimal t = new BigDecimal(currentMarker.getTime());
             t.compareTo(new BigDecimal(currentMarker.getCounterpartTime())) < 0;
             t = t.add(new BigDecimal(chunkSize))) {
          if (t.add(new BigDecimal(duration)).compareTo(new BigDecimal(currentMarker.getCounterpartTime())) <= 0) {
            ret.add(new TimeSet(t.doubleValue(), t.add(new BigDecimal(duration)).doubleValue()));
          } else {
            t = new BigDecimal(currentMarker.getCounterpartTime()); //end for loop prematurely
          }
        }
      }
    }
    return ret;
  }

  @Override
  public ITimeSet addDays(int days) {
    List<ITimeIntervalMarker> newIntervalMarkers = new ArrayList<ITimeIntervalMarker>();
    for (ITimeIntervalMarker intervalMarker : intervalMarkers) {
      if (intervalMarker.isStart()) {
        double start = intervalMarker.getTime() + (24.0 * days);
        double end = intervalMarker.getCounterpartTime() + (24.0 * days);
        try {
          newIntervalMarkers.add(new StartMarker(start, end));
          newIntervalMarkers.add(new EndMarker(start, end));
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException(
              "You are trying to shift by a number of days that results in illegal TimeSets.");
        }
      }
    }
    return new TimeSet(newIntervalMarkers);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ITimeSet) {
      ITimeSet that = (ITimeSet) obj;
      List<ITimeIntervalMarker> thatIntervalMarkers = that.getIntervalMarkers();
      if (this.intervalMarkers.size() == thatIntervalMarkers.size()) {
        for (ITimeIntervalMarker intervalMarker : thatIntervalMarkers) {
          if (!this.intervalMarkers.contains(intervalMarker)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.intervalMarkers);
  }

  @Override
  public String toTextOutput() {
    String ret = "";
    for (ITimeIntervalMarker intervalMarker : intervalMarkers) {
      if (intervalMarker.isStart()) {
        ret += "[" + intervalMarker.getTimeTextOutput() + ", "
            + intervalMarker.getCounterpartTimeTextOutput() + "]";
        ret += ", ";
      }
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = "";
    for (ITimeIntervalMarker intervalMarker : intervalMarkers) {
      if (intervalMarker.isStart()) {
        ret += "[" + intervalMarker.getTime() + ", " + intervalMarker.getCounterpartTime() + "]";
        ret += ", ";
      }
    }
    return ret;
  }
}
