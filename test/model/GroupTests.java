package model;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the Group class.
 */
public class GroupTests {

  @Test
  public void testGetCommonMembersWith() {
    IPerson zach = new Person("Zach",
        new TimeSet(0.0, 4.2).union(new TimeSet(5.5, 9.0)));
    IPerson mari = new Person("Mari",
        new TimeSet(6.0, 11.0).union(new TimeSet(2.75, 5.0)));
    IPerson griffin = new Person("Griffin",
        new TimeSet(8.5, 10.5).union(new TimeSet(2.0, 4.0)));
    IPerson julia = new Person("Julia",
        new TimeSet(3.0, 6.25).union(new TimeSet(7.0, 10.25)));
    Set<IPerson> people1 = new HashSet<IPerson>();
    people1.add(mari);
    people1.add(griffin);
    people1.add(julia);
    Set<IPerson> people2 = new HashSet<IPerson>();
    people2.add(zach);
    people2.add(mari);
    people2.add(julia);
    Set<IPerson> people3 = new HashSet<IPerson>();
    people3.add(zach);
    IGroup g1 = new Group("G1", people1);
    IGroup g2 = new Group("G2", people2);
    IGroup g3 = new Group("G3", people3);
    Set<IPerson> commonMembers = g2.getCommonMembersWith(g1);
    assertEquals(true, commonMembers.contains(julia));
    assertEquals(true, commonMembers.contains(mari));
    assertEquals(2, commonMembers.size());
    assertEquals(0, g1.getCommonMembersWith(g3).size());
    assertEquals(3, g2.getCommonMembersWith(g2).size());
  }

  @Test
  public void testGetAvailability() {
    IPerson zach = new Person("Zach",
        new TimeSet(0.0, 4.2).union(new TimeSet(5.5, 9.0)));
    IPerson mari = new Person("Mari",
        new TimeSet(6.0, 11.0).union(new TimeSet(2.75, 5.0)));
    IPerson griffin = new Person("Griffin",
        new TimeSet(8.5, 10.5).union(new TimeSet(2.0, 4.0)));
    IPerson julia = new Person("Julia",
        new TimeSet(3.0, 6.25).union(new TimeSet(7.0, 10.25)));
    Set<IPerson> people = new HashSet<IPerson>();
    people.add(zach);
    people.add(mari);
    people.add(griffin);
    people.add(julia);
    IGroup g1 = new Group("G1", people);
    ITimeSet t1 = g1.getAvailability();
    assertEquals(true, t1.equals(new TimeSet(3.0, 4.0).union(new TimeSet(8.5, 9.0))));
    for (IPerson p : g1.getMembers()) {
      p.setCurrentAvailability(p.getCurrentAvailability().subtract(new TimeSet(2.0, 3.5)));
    }
    t1 = g1.getAvailability();
    assertEquals(true, t1.equals(new TimeSet(3.5, 4.0).union(new TimeSet(8.5, 9.0))));
    for (IPerson p : g1.getMembers()) {
      p.setCurrentAvailability(p.getCurrentAvailability().subtract(new TimeSet(3.5, 9.0)));
    }
    t1 = g1.getAvailability();
    assertEquals(true, t1.equals(new TimeSet()));
  }

  @Test
  public void testGetMembers() {
    IPerson zach = new Person("Zach",
        new TimeSet(0.0, 4.2).union(new TimeSet(5.5, 9.0)));
    IPerson mari = new Person("Mari",
        new TimeSet(6.0, 11.0).union(new TimeSet(2.75, 5.0)));
    IPerson griffin = new Person("Griffin",
        new TimeSet(8.5, 10.5).union(new TimeSet(2.0, 4.0)));
    IPerson julia = new Person("Julia",
        new TimeSet(3.0, 6.25).union(new TimeSet(7.0, 10.25)));
    Set<IPerson> people = new HashSet<IPerson>();
    people.add(zach);
    people.add(mari);
    people.add(griffin);
    people.add(julia);
    IGroup g1 = new Group("G1", people);
    Set<IPerson> members = g1.getMembers();
    assertEquals(true, members.contains(zach));
    assertEquals(true, members.contains(mari));
    assertEquals(true, members.contains(griffin));
    assertEquals(true, members.contains(julia));
    assertEquals(4, members.size());
  }

  @Test
  public void testGroupToString() {
    IPerson zach = new Person("Zach",
        new TimeSet(0.0, 4.2).union(new TimeSet(5.5, 9.0)));
    IPerson mari = new Person("Mari",
        new TimeSet(6.0, 11.0).union(new TimeSet(2.75, 5.0)));
    IPerson griffin = new Person("Griffin",
        new TimeSet(8.5, 10.5).union(new TimeSet(2.0, 4.0)));
    IPerson julia = new Person("Julia",
        new TimeSet(3.0, 6.25).union(new TimeSet(7.0, 10.25)));
    Set<IPerson> people = new HashSet<IPerson>();
    people.add(zach);
    people.add(mari);
    people.add(griffin);
    people.add(julia);
    IGroup g1 = new Group("G1", people);
    //assertEquals("", g1.toString());
    /*assertEquals(true,
        g1.toString().equals("G1: (Zach, Julia, Mari, Griffin)")
            || g1.toString().equals("G1: (Griffin, Julia, Zach, Mari)")
            || g1.toString().equals("G1: (Mari, Griffin, Julia, Zach)"));*/
  }
}
