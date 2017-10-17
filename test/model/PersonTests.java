package model;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 *  Tests for the Person class.
 */
public class PersonTests {

  @Test
  public void testPersonConstructor() {
    IPerson p1 = new Person("Zach", new TimeSet(0.0, 3.0));
    assertEquals("Zach", p1.getName());
    assertEquals(new TimeSet(0.0, 3.0), p1.getCurrentAvailability());
    assertEquals(p1.getCurrentAvailability(), p1.getDefaultAvailability());
    p1.setCurrentAvailability(new TimeSet(3.3, 4.2));
    assertEquals(new TimeSet(3.3, 4.2), p1.getCurrentAvailability());
    assertEquals(new TimeSet(0.0, 3.0), p1.getDefaultAvailability());
  }

  @Test
  public void testSplit() {
    String test = "9:00pm - 10:00pm";
    String[] components = test.split("-| - | -|- ");
    for (int i = 0; i < components.length; i++) {
      System.out.println("index " + i + " - {" + components[i] + "}");
    }
    //System.out.println(components[0]);
  }
}
