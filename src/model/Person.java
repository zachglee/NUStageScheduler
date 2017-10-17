package model;

import java.util.Objects;

/**
 * Representation of a person for use in constructing a schedule.
 * Namely tracks availability.
 */
public class Person implements IPerson {

  private String name;
  private ITimeSet defaultAvailability; //TODO almost certainly dont need this
  private ITimeSet currentAvailability;

  /**
   * Constructs a Person with the given name and availability.
   * @param name the name of the person to construct
   * @param defaultAvailability the availability to start them with
   */
  public Person(String name, ITimeSet defaultAvailability) {
    this.name = name;
    this.defaultAvailability = defaultAvailability;
    this.currentAvailability = defaultAvailability;
  }

  /**
   * Constructs a new Person with the given name, default availability and current availability.
   * @param name the new Person's name
   * @param defaultAvailability the new Person's default availability
   * @param currentAvailability the new Person's current availability
   */
  private Person(String name, ITimeSet defaultAvailability, ITimeSet currentAvailability) {
    this.name = name;
    this.defaultAvailability = defaultAvailability;
    this.currentAvailability = currentAvailability;
  }

  @Override
  public ITimeSet getCurrentAvailability() {
    return this.currentAvailability;
  }

  @Override
  public ITimeSet getDefaultAvailability() {
    return this.defaultAvailability;
  }

  @Override
  public void setCurrentAvailability(ITimeSet t) {
    if (t == null) {
      throw new IllegalArgumentException(
          "You cannot set a Person's current availability to null.");
    }
    this.currentAvailability = t;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public IPerson copy() {
    //all of Person's fields are non-mutable, so we can use the same actual objects
    //just make a new person, with fields pointing to the same things
    return new Person(this.name, this.defaultAvailability, this.currentAvailability);
  }

  @Override
  public String toString() {
    return this.name;
  }


  //TODO this is like a rough hacky version of person equality to see if I can fix bugs
  //TODO reconsder and think about the implications of this, is it okay?
  /*@Override
  public boolean equals(Object obj) {
    if (obj instanceof IPerson) {
      IPerson p = (IPerson) obj;
      return p.getName().equals(this.name)
          && p.getCurrentAvailability().equals(this.currentAvailability);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.currentAvailability);
  }*/
}
