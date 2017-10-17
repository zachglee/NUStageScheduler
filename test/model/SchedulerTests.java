package model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Zachary Lee on 7/19/2017.
 */
public class SchedulerTests {

  IPerson allFree1;
  IPerson allFree2;
  IPerson last5;
  IPerson last4;
  IPerson disjoint;
  IPerson first4;
  IPerson oneToFour;
  IPerson threeToFive;

  IGroup a;
  IGroup b;
  IGroup c;
  IGroup d;
  IGroup e;
  IGroup x;
  IGroup y;
  IGroup z;

  ILocation loc1;
  ILocation loc2;

  IUnscheduledEvent a1;
  IUnscheduledEvent a2;
  IUnscheduledEvent b1;
  IUnscheduledEvent c1;
  IUnscheduledEvent d1;
  IUnscheduledEvent e1;
  IUnscheduledEvent x1;
  IUnscheduledEvent y1;
  IUnscheduledEvent z1;

  List<IUnscheduledEvent> toSchedule1;
  List<IUnscheduledEvent> toSchedule2;

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
    first4 = new Person("First4", new TimeSet(0.0, 4.0));
    oneToFour = new Person("OneToFour", new TimeSet(1.0, 4.0));
    threeToFive = new Person("ThreeToFive", new TimeSet(3.0, 5.0));

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
    Set<IPerson> xMembers = new HashSet<IPerson>();
    xMembers.add(allFree2);
    xMembers.add(oneToFour);
    x = new Group("X", xMembers);
    Set<IPerson> yMembers = new HashSet<IPerson>();
    yMembers.add(allFree1);
    yMembers.add(first4);
    y = new Group("Y", yMembers);
    Set<IPerson> zMembers = new HashSet<IPerson>();
    zMembers.add(threeToFive);
    z = new Group("Z", zMembers);

    loc1 = new Location("Room1");
    loc2 = new Location("Room2");

    a1 = new UnscheduledEvent("EventA1", a, loc1, 1.0, 1);
    a2 = new UnscheduledEvent("EventA2", a, loc2, 2.0, 1);
    b1 = new UnscheduledEvent("EventB1", b, loc1, 2.0, 1);
    c1 = new UnscheduledEvent("EventC1", c, loc1, 1.0, 1);
    d1 = new UnscheduledEvent("EventD1", d, loc2, 2.0, 1);
    e1 = new UnscheduledEvent("EventE1", e, loc2, 1.0, 1);
    x1 = new UnscheduledEvent("EventX1", x, loc1, 2.0, 1);
    y1 = new UnscheduledEvent("EventY1", y, loc1, 2.0, 1);
    z1 = new UnscheduledEvent("EventZ1", z, loc1, 1.0, 1);

    toSchedule1 = new ArrayList<IUnscheduledEvent>();
    toSchedule1.add(a1);
    toSchedule1.add(a2);
    toSchedule1.add(b1);
    toSchedule1.add(c1);
    toSchedule1.add(d1);
    toSchedule1.add(e1);
    toSchedule2 = new ArrayList<IUnscheduledEvent>();
    toSchedule2.add(x1);
    toSchedule2.add(y1);
    toSchedule2.add(z1);
  }

  @Test
  public void testScheduleBasic() {
    initData();
    IScheduler scheduler = new Scheduler(1.0);
    ISchedule produced = scheduler.schedule(toSchedule1);
    assertEquals("EventA1", produced.getEventAt(new TimeSet(4.0, 5.0), loc1).getName());
    assertEquals("EventB1", produced.getEventAt(new TimeSet(1.0, 3.0), loc1).getName());
    assertEquals("EventC1", produced.getEventAt(new TimeSet(3.0, 4.0), loc1).getName());
    assertEquals("EventA2", produced.getEventAt(new TimeSet(0.0, 2.0), loc2).getName());
    assertEquals("EventD1", produced.getEventAt(new TimeSet(4.0, 6.0), loc2).getName());
    assertEquals("EventE1", produced.getEventAt(new TimeSet(2.0, 3.0), loc2).getName());
    //TODO test whatever version of reset I choose to make
  }

  @Test
  public void testScheduleZeroCase() {
    initData();
    IScheduler scheduler = new Scheduler(1.0);
    ISchedule produced = scheduler.schedule(toSchedule2);
    assertEquals("EventX1", produced.getEventAt(new TimeSet(2.0, 4.0), loc1).getName());
    assertEquals("EventY1", produced.getEventAt(new TimeSet(0.0, 2.0), loc1).getName());
    assertEquals("EventZ1", produced.getEventAt(new TimeSet(4.0, 5.0), loc1).getName());
  }



}
