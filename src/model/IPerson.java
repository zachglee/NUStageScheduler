package model;

/**
 * Describes functionality for a representation of a person,
 * to be used in the construction of a schedule. Namely, tracks their
 * availability for scheduling at different times.
 */
public interface IPerson {

  /**
   * Returns the TimeSet representing this Person's current availability.
   * @return the TimeSet representing this Person's current availability
   */
  ITimeSet getCurrentAvailability();

  ITimeSet getDefaultAvailability(); //<-- may not need this, we'll see

  /**
   * Sets this Person's current availability to the given TimeSet.
   * @param t the TimeSet to set this Person's current availability to
   */
  //TODO consider adding a more controlled, targeted mutator method, this ones a bit too powerful
  void setCurrentAvailability(ITimeSet t);

  /**
   * Returns the name of this Person.
   * @return the name of this Person
   */
  String getName();

  /**
   * Returns a person that is a copy of this person.
   * @return a copy of this person
   */
  IPerson copy();
}
