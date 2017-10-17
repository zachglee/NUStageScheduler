package model;

import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the class TimeSet
 */
public class TimeSetTests {

  @Test
  public void testTimeSetConstructor() {
    //basic
    ITimeSet t1 = new TimeSet(12.0, 24.0);
    List<ITimeIntervalMarker> t1ims = t1.getIntervalMarkers();
    assertEquals(true, t1ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(true, t1ims.contains(new EndMarker(12.0, 24.0)));
    //edge of range
    ITimeSet t2 = new TimeSet(0.0, 168.0);
    List<ITimeIntervalMarker> t2ims = t2.getIntervalMarkers();
    assertEquals(true, t2ims.contains(new StartMarker(0.0, 168.0)));
    assertEquals(true, t2ims.contains(new EndMarker(0.0, 168.0)));
    //decimal
    ITimeSet t3 = new TimeSet(3.1415, 11.176);
    List<ITimeIntervalMarker> t3ims = t3.getIntervalMarkers();
    assertEquals(true, t3ims.contains(new StartMarker(3.1415, 11.176)));
    assertEquals(true, t3ims.contains(new EndMarker(3.1415, 11.176)));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testTimeSetConstructorRangeException1() {
    ITimeSet t1 = new TimeSet(-.01, 10.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testTimeSetConstructorRangeException2() {
    ITimeSet t1 = new TimeSet(42.0, 168.01);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testTimeSetConstructorStartEndRelationException() {
    ITimeSet t1 = new TimeSet(3.01, 3.0);
  }

  @Test
  public void testUnion() {
    ITimeSet t1 = new TimeSet(12.0, 24.0);
    ITimeSet t2 = new TimeSet(36.0, 48.0);
    ITimeSet t3 = new TimeSet(20.0, 40.0);
    ITimeSet t4 = new TimeSet(25.0, 33.0);
    ITimeSet t5 = new TimeSet(7.5, 69.0);
    //union two disjoint sets
    ITimeSet union1 = t1.union(t2);
    List<ITimeIntervalMarker> union1ims = union1.getIntervalMarkers();
    assertEquals(true, union1ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(true, union1ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(true, union1ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(true, union1ims.contains(new EndMarker(36.0, 48.0)));
    //union two overlapping sets case 1
    ITimeSet union2 = t1.union(t3);
    List<ITimeIntervalMarker> union2ims = union2.getIntervalMarkers();
    assertEquals(false, union2ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, union2ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, union2ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, union2ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(true, union2ims.contains(new StartMarker(12.0, 40.0)));
    assertEquals(true, union2ims.contains(new EndMarker(12.0, 40.0)));
    //union two overlapping sets case 2
    ITimeSet union3 = t2.union(t3);
    List<ITimeIntervalMarker> union3ims = union3.getIntervalMarkers();
    assertEquals(false, union3ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, union3ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(false, union3ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, union3ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(true, union3ims.contains(new StartMarker(20.0, 48.0)));
    assertEquals(true, union3ims.contains(new EndMarker(20.0, 48.0)));
    //union two disjoint sets to a set that contains both of them
    ITimeSet union4 = t5.union(union1);
    List<ITimeIntervalMarker> union4ims = union4.getIntervalMarkers();
    assertEquals(false, union4ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, union4ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(false, union4ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, union4ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(true, union4ims.contains(new StartMarker(7.5, 69.0)));
    assertEquals(true, union4ims.contains(new EndMarker(7.5, 69.0)));
    //complex example
    ITimeSet union5 = union1.union(t4).union(t3);
    List<ITimeIntervalMarker> union5ims = union5.getIntervalMarkers();
    assertEquals(false, union5ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, union5ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(false, union5ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, union5ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, union5ims.contains(new StartMarker(25.0, 33.0)));
    assertEquals(false, union5ims.contains(new EndMarker(25.0, 33.0)));
    assertEquals(false, union5ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, union5ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(true, union5ims.contains(new StartMarker(12.0, 48.0)));
    assertEquals(true, union5ims.contains(new EndMarker(12.0, 48.0)));
  }

  @Test
  public void testIntersect() {
    ITimeSet t1 = new TimeSet(12.0, 24.0);
    ITimeSet t2 = new TimeSet(36.0, 48.0);
    ITimeSet t3 = new TimeSet(20.0, 40.0);
    ITimeSet t4 = new TimeSet(25.0, 33.0);
    ITimeSet t5 = new TimeSet(7.5, 69.0);
    //intersect two disjoint sets
    ITimeSet intersect1 = t1.intersect(t2);
    List<ITimeIntervalMarker> intersect1ims = intersect1.getIntervalMarkers();
    assertEquals(0, intersect1ims.size());
    //intersect two overlapping sets case 1
    ITimeSet intersect2 = t1.intersect(t3);
    List<ITimeIntervalMarker> intersect2ims = intersect2.getIntervalMarkers();
    assertEquals(false, intersect2ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, intersect2ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, intersect2ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, intersect2ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(true, intersect2ims.contains(new StartMarker(20.0, 24.0)));
    assertEquals(true, intersect2ims.contains(new EndMarker(20.0, 24.0)));
    //intersect two overlapping sets case 2
    ITimeSet intersect3 = t2.intersect(t3);
    List<ITimeIntervalMarker> intersect3ims = intersect3.getIntervalMarkers();
    assertEquals(false, intersect3ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, intersect3ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(false, intersect3ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, intersect3ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(true, intersect3ims.contains(new StartMarker(36.0, 40.0)));
    assertEquals(true, intersect3ims.contains(new EndMarker(36.0, 40.0)));
    //intersect of an empty set with anything is still an empty set
    ITimeSet intersect4 = intersect1.intersect(t5);
    List<ITimeIntervalMarker> intersect4ims = intersect4.getIntervalMarkers();
    assertEquals(0, intersect4ims.size());
    //intersect of a TimeSet and another TimeSet that contains it entirely should just
    //be the first TimeSet
    ITimeSet intersect5 = t2.union(t1).intersect(t5);
    List<ITimeIntervalMarker> intersect5ims = intersect5.getIntervalMarkers();
    assertEquals(false, intersect5ims.contains(new StartMarker(7.5, 69.0)));
    assertEquals(false, intersect5ims.contains(new EndMarker(7.5, 69.0)));
    assertEquals(true, intersect5ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(true, intersect5ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(true, intersect5ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(true, intersect5ims.contains(new EndMarker(36.0, 48.0)));
    //complex example
    ITimeSet intersect6 = t2.union(t1).union(t4).intersect(t3);
    List<ITimeIntervalMarker> intersect6ims = intersect6.getIntervalMarkers();
    assertEquals(false, intersect6ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, intersect6ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(false, intersect6ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, intersect6ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, intersect6ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, intersect6ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(true, intersect6ims.contains(new StartMarker(20.0, 24.0)));
    assertEquals(true, intersect6ims.contains(new EndMarker(20.0, 24.0)));
    assertEquals(true, intersect6ims.contains(new StartMarker(36.0, 40.0)));
    assertEquals(true, intersect6ims.contains(new EndMarker(36.0, 40.0)));
    assertEquals(true, intersect6ims.contains(new StartMarker(25.0, 33.0)));
    assertEquals(true, intersect6ims.contains(new EndMarker(25.0, 33.0)));
    //tests intersect that takes a list of sets, in this specific case, a 3-way intersection
    List<ITimeSet> testTimeSets = new ArrayList<ITimeSet>();
    testTimeSets.add(t4);
    testTimeSets.add(t5);
    ITimeSet intersect7 = t3.intersect(testTimeSets);
    List<ITimeIntervalMarker> intersect7ims = intersect7.getIntervalMarkers();
    assertEquals(false, intersect7ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, intersect7ims.contains(new EndMarker(20.0, 40.0)));
    assertEquals(false, intersect7ims.contains(new StartMarker(7.5, 69.0)));
    assertEquals(false, intersect7ims.contains(new EndMarker(7.5, 69.0)));
    assertEquals(true, intersect7ims.contains(new StartMarker(25.0, 33.0)));
    assertEquals(true, intersect7ims.contains(new EndMarker(25.0, 33.0)));
  }

  @Test
  public void testComplement() {
    ITimeSet t1 = new TimeSet(12.0, 24.0);
    ITimeSet t2 = new TimeSet(36.0, 48.0);
    ITimeSet t3 = new TimeSet(20.0, 40.0);
    ITimeSet t4 = new TimeSet(25.0, 33.0);
    ITimeSet t5 = new TimeSet(7.5, 69.0);
    ITimeSet u = new TimeSet(0.0, 168.0);
    //basic complement
    ITimeSet complement1 = t5.complement();
    List<ITimeIntervalMarker> complement1ims = complement1.getIntervalMarkers();
    assertEquals(true, complement1ims.contains(new StartMarker(0.0, 7.5)));
    assertEquals(true, complement1ims.contains(new EndMarker(0.0, 7.5)));
    assertEquals(true, complement1ims.contains(new StartMarker(69.0, 168.0)));
    assertEquals(true, complement1ims.contains(new EndMarker(69.0, 168.0)));
    assertEquals(false, complement1ims.contains(new StartMarker(7.5, 69.0)));
    assertEquals(false, complement1ims.contains(new EndMarker(7.5, 69.0)));
    //complement of universe should be empty
    ITimeSet complement2 = u.complement();
    List<ITimeIntervalMarker> complement2ims = complement2.getIntervalMarkers();
    assertEquals(0, complement2ims.size());
    //complement of the union of multiple disjoint sets
    ITimeSet complement3 = t1.union(t2).union(t4).complement();
    List<ITimeIntervalMarker> complement3ims = complement3.getIntervalMarkers();
    assertEquals(true, complement3ims.contains(new StartMarker(0.0, 12.0)));
    assertEquals(true, complement3ims.contains(new EndMarker(0.0, 12.0)));
    assertEquals(true, complement3ims.contains(new StartMarker(48.0, 168.0)));
    assertEquals(true, complement3ims.contains(new EndMarker(48.0, 168.0)));
    assertEquals(true, complement3ims.contains(new StartMarker(24.0, 25.0)));
    assertEquals(true, complement3ims.contains(new EndMarker(24.0, 25.0)));
    assertEquals(true, complement3ims.contains(new StartMarker(33.0, 36.0)));
    assertEquals(true, complement3ims.contains(new EndMarker(33.0, 36.0)));
    assertEquals(false, complement3ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, complement3ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, complement3ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, complement3ims.contains(new EndMarker(36.0, 48.0)));
    assertEquals(false, complement3ims.contains(new StartMarker(25.0, 33.0)));
    assertEquals(false, complement3ims.contains(new EndMarker(25.0, 33.0)));
  }

  @Test
  public void testSubtract() {
    ITimeSet t1 = new TimeSet(12.0, 24.0);
    ITimeSet t2 = new TimeSet(36.0, 48.0);
    ITimeSet t3 = new TimeSet(20.0, 40.0);
    ITimeSet t4 = new TimeSet(25.0, 33.0);
    ITimeSet t5 = new TimeSet(7.5, 69.0);
    ITimeSet u = new TimeSet(0.0, 168.0);
    //subtracting disjoint sets should just return the first disjoint set
    ITimeSet subtraction1 = t1.subtract(t2);
    List<ITimeIntervalMarker> subtraction1ims = subtraction1.getIntervalMarkers();
    assertEquals(true, subtraction1ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(true, subtraction1ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, subtraction1ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, subtraction1ims.contains(new EndMarker(36.0, 48.0)));
    //subtracting a set containing a smaller set from that smaller set should return the empty set
    ITimeSet subtraction2 = t1.union(t2).subtract(t5);
    List<ITimeIntervalMarker> subtraction2ims = subtraction2.getIntervalMarkers();
    assertEquals(0, subtraction2ims.size());
    //subtracting a set from a union of that set and another should yield just the other set
    ITimeSet subtraction3 = t4.union(t2).subtract(t2);
    List<ITimeIntervalMarker> subtraction3ims = subtraction3.getIntervalMarkers();
    assertEquals(true, subtraction3ims.contains(new StartMarker(25.0, 33.0)));
    assertEquals(true, subtraction3ims.contains(new EndMarker(25.0, 33.0)));
    assertEquals(false, subtraction3ims.contains(new StartMarker(36.0, 48.0)));
    assertEquals(false, subtraction3ims.contains(new EndMarker(36.0, 48.0)));
    //subtract two overlapping sets
    ITimeSet subtraction4 = t3.subtract(t1);
    List<ITimeIntervalMarker> subtraction4ims = subtraction4.getIntervalMarkers();
    assertEquals(true, subtraction4ims.contains(new StartMarker(24.0, 40.0)));
    assertEquals(true, subtraction4ims.contains(new EndMarker(24.0, 40.0)));
    assertEquals(false, subtraction4ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, subtraction4ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, subtraction4ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, subtraction4ims.contains(new EndMarker(20.0, 40.0)));
    //subtract the same two overlapping sets, but in with the opposite set as the invocant,
    //which should yield a different result
    ITimeSet subtraction5 = t1.subtract(t3);
    List<ITimeIntervalMarker> subtraction5ims = subtraction5.getIntervalMarkers();
    assertEquals(true, subtraction5ims.contains(new StartMarker(12.0, 20.0)));
    assertEquals(true, subtraction5ims.contains(new EndMarker(12.0, 20.0)));
    assertEquals(false, subtraction5ims.contains(new StartMarker(12.0, 24.0)));
    assertEquals(false, subtraction5ims.contains(new EndMarker(12.0, 24.0)));
    assertEquals(false, subtraction5ims.contains(new StartMarker(20.0, 40.0)));
    assertEquals(false, subtraction5ims.contains(new EndMarker(20.0, 40.0)));
  }

  @Test
  public void testAllTimesOfDuration() {
    ITimeSet t1 = new TimeSet(12.5, 14.0);
    ITimeSet t2 = new TimeSet(36.0, 38.0);
    ITimeSet t3 = new TimeSet(20.0, 40.0);
    ITimeSet t4 = new TimeSet(25.0, 33.0);
    ITimeSet t5 = new TimeSet(7.5, 69.0);
    ITimeSet u = new TimeSet(0.0, 168.0);
    //basic example
    List<ITimeSet> timeList1 = t1.allTimesOfDuration(0.5, .25);
    assertEquals(5, timeList1.size());
    assertEquals(true, timeList1.contains(new TimeSet(12.5, 13.0)));
    assertEquals(true, timeList1.contains(new TimeSet(12.75, 13.25)));
    assertEquals(true, timeList1.contains(new TimeSet(13.0, 13.5)));
    assertEquals(true, timeList1.contains(new TimeSet(13.25, 13.75)));
    assertEquals(true, timeList1.contains(new TimeSet(13.5, 14.0)));
    //two disjoint sets example
    List<ITimeSet> timeList2 = t1.union(t2).allTimesOfDuration(1.0, .5);
    assertEquals(5, timeList2.size());
    assertEquals(true, timeList2.contains(new TimeSet(12.5, 13.5)));
    assertEquals(true, timeList2.contains(new TimeSet(13.0, 14.0)));
    assertEquals(true, timeList2.contains(new TimeSet(36.0, 37.0)));
    assertEquals(true, timeList2.contains(new TimeSet(36.5, 37.5)));
    assertEquals(true, timeList2.contains(new TimeSet(37.0, 38.0)));
    //no valid times case, because of the duration and or chunk size
    List<ITimeSet> timeList3 = t4.union(t1).allTimesOfDuration(10.0, .01);
    assertEquals(0, timeList3.size());
    //test duration and chunksizes that don't fit nicely with the times
    List<ITimeSet> timeList4 = t1.union(t2).allTimesOfDuration(0.7, 0.7);
    assertEquals(4, timeList4.size());
    assertEquals(true, timeList4.contains(new TimeSet(12.5, 13.2)));
    assertEquals(true, timeList4.contains(new TimeSet(13.2, 13.9)));
    assertEquals(true, timeList4.contains(new TimeSet(36.0, 36.7)));
    assertEquals(true, timeList4.contains(new TimeSet(36.7, 37.4)));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAllTimesOfDurationNegativeException1() {
    ITimeSet t1 = new TimeSet(12.5, 14.0);
    List<ITimeSet> exception = t1.allTimesOfDuration(1.0, 0.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAllTimesOfDurationNegativeException2() {
    ITimeSet t1 = new TimeSet(12.5, 14.0);
    List<ITimeSet> exception = t1.allTimesOfDuration(0.0, 1.0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAllTimesOfDurationDivisibleException() {
    ITimeSet t1 = new TimeSet(12.5, 14.0);
    List<ITimeSet> exception = t1.allTimesOfDuration(1.0, 0.9);
  }

  @Test
  public void testTimeSetEquals() {
    ITimeSet t1 = new TimeSet(0.0, 3.14);
    ITimeSet t2 = new TimeSet(0.0, 3.14);
    ITimeSet t3 = new TimeSet(1.0, 4.14);
    ITimeSet t4 = new TimeSet(5.0, 6.0);
    assertEquals(true, t1.equals(t2));
    assertEquals(true, t1.union(t3).equals(t3.union(t1))); //doesn't matter which is the invocant
    assertEquals(false, t1.equals(t3));
    assertEquals(false, t3.equals(t4));
    assertEquals(true, t1.union(t2).equals(t2));
    assertEquals(true, t2.union(t1).union(t4).equals(t2.union(t4)));
    assertEquals(true, t1.intersect(t4).equals(t2.intersect(t4))); //empty sets are equal
  }

  @Test
  public void testTimeSetHashCode() {
    ITimeSet t1 = new TimeSet(0.0, 3.14);
    ITimeSet t2 = new TimeSet(0.0, 3.14);
    ITimeSet t3 = new TimeSet(1.0, 4.14);
    ITimeSet t4 = new TimeSet(5.0, 6.0);
    assertEquals(true, t1.hashCode() == t2.hashCode());
    assertEquals(true, t1.union(t3).hashCode() == t3.union(t1).hashCode()); //doesn't matter which is the invocant
    assertEquals(false, t1.hashCode() == t3.hashCode());
    assertEquals(false, t3.hashCode() == t4.hashCode());
    assertEquals(true, t1.union(t2).hashCode() == t2.hashCode());
    assertEquals(true, t2.union(t1).union(t4).hashCode() == t2.union(t4).hashCode());
    assertEquals(true, t1.intersect(t4).hashCode() == t2.intersect(t4).hashCode()); //empty sets are equal
  }

  @Test
  public void testAddDays() {
    ITimeSet t1 = new TimeSet(1.0, 7.0);
    assertEquals(new TimeSet(25.0, 31.0),
        t1.addDays(1));
    assertEquals(new TimeSet(1.0, 7.0),
        t1.addDays(0));
    assertEquals(new TimeSet(1.0, 7.0),
        t1.addDays(2).addDays(-1).addDays(-1));
  }
}
