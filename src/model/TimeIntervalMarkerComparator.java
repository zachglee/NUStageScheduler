package model;

import java.util.Comparator;

/**
 * Comparator for TimeIntervalMarkers that compares based on position of the marker in time.
 */
public class TimeIntervalMarkerComparator implements Comparator<ITimeIntervalMarker> {

  @Override
  public int compare(ITimeIntervalMarker o1, ITimeIntervalMarker o2) {
    if (o1.getTime() < o2.getTime()) {
      return -1;
    } else if (o1.getTime() == o2.getTime()) {
      if (!o1.isStart() && o2.isStart()) {
        return -1;
      } else if (o1.isStart() && !o2.isStart()) {
        return 1;
      } else {
        return 0;
      }
    } else {
      return 1;
    }
  }
}
