package model;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the UnscheduledEvent class.
 */
public class UnscheduledEventTests {

  IPerson allFree1;
  IPerson allFree2;
  IPerson last5;
  IPerson last4;
  IPerson disjoint;

  IGroup a;
  IGroup b;
  IGroup c;
  IGroup d;
  IGroup e;

  ILocation loc1;
  ILocation loc2;

  IUnscheduledEvent a1;
  IUnscheduledEvent a2;
  IUnscheduledEvent b1;
  IUnscheduledEvent c1;
  IUnscheduledEvent d1;
  IUnscheduledEvent e1;

  /**
   * Initializes the data. Groups b and d have members in common with only each other.
   * Groups a, c, and e have members in common with only each other.
   */
  public void initData() {
    allFree1 = new Person("AllFree1", new TimeSet(0.0, 6.0));
    allFree2 = new Person("AllFree2", new TimeSet(0.0, 6.0));
    last5 = new Person("Last5", new TimeSet(1.0, 6.0));
    last4 = new Person("Last4", new TimeSet(2.0, 6.0));
    disjoint = new Person("Disjoint", new TimeSet(0.0, 3.0).union(new TimeSet(4.0, 6.0)));

    Set<IPerson> bMembers = new HashSet<IPerson>();
    bMembers.add(allFree1);
    bMembers.add(last5);
    b = new Group("B", bMembers);
    d = new Group("D", allFree1);
    c = new Group("C", allFree2);
    Set<IPerson> eMembers = new HashSet<IPerson>();
    eMembers.add(allFree2);
    eMembers.add(last4);
    e = new Group("E", eMembers);
    Set<IPerson> aMembers = new HashSet<IPerson>();
    aMembers.add(allFree2);
    aMembers.add(disjoint);
    a = new Group("A", aMembers);

    loc1 = new Location("Room1");
    loc2 = new Location("Room2");

    a1 = new UnscheduledEvent("EventA1", a, loc1, 1.0, 1);
    a2 = new UnscheduledEvent("EventA2", a, loc2, 2.0, 1);
    b1 = new UnscheduledEvent("EventB1", b, loc1, 2.0, 1);
    c1 = new UnscheduledEvent("EventC1", c, loc1, 1.0, 1);
    d1 = new UnscheduledEvent("EventD1", d, loc2, 2.0, 1);
    e1 = new UnscheduledEvent("EventE1", e, loc2, 1.0, 1);
  }

  @Test
  public void testOptionsRemainingIf() {
    initData(); //TODO more robust test cases, esp to do with location unavailability
    Map<IUnscheduledEvent, ITimeSet> toBeScheduled = new HashMap<IUnscheduledEvent, ITimeSet>();
    toBeScheduled.put(a1, a1.getAvailability());
    toBeScheduled.put(b1, b1.getAvailability());
    toBeScheduled.put(c1, c1.getAvailability());
    toBeScheduled.put(d1, d1.getAvailability());
    toBeScheduled.put(e1, e1.getAvailability());

    assertEquals(18,
        a2.consider(
            new TimeSet(0.0, 2.0),
            toBeScheduled,
            1.0).totalOptions());
    //initData(); //TODO this line should not break it once I implement non-literal location equality
    assertEquals(16,
        a2.consider(
            new TimeSet(4.0, 6.0),
            toBeScheduled,
            1.0).totalOptions());
    assertEquals(16,
        a2.consider(
            new TimeSet(1.0, 3.0),
            toBeScheduled,
            1.0).totalOptions());

    initData();
    IScheduledEvent a2scheduled = new ScheduledEvent(a2, new TimeSet(0.0, 2.0));
    toBeScheduled = new HashMap<IUnscheduledEvent, ITimeSet>();
    toBeScheduled.put(a1, a1.getAvailability());
    toBeScheduled.put(c1, c1.getAvailability());
    toBeScheduled.put(d1, d1.getAvailability());
    toBeScheduled.put(e1, e1.getAvailability());

    assertEquals(8,
        b1.consider(
            new TimeSet(4.0, 6.0),
            toBeScheduled,
            1.0).totalOptions());
    assertEquals(8,
        b1.consider(
            new TimeSet(3.0, 5.0),
            toBeScheduled,
            1.0).totalOptions());
    //HELL YEAH IT WORKS HAHAHAHAHAHHA
    assertEquals(8,
        new OptionMetric(b1,
            new TimeSet(3.0, 5.0),
            toBeScheduled,
            1.0).totalOptions());
  }

}
