package model;

import java.util.Set;

/**
 * Describes functionality for a Group of people,
 * used for scheduling purposes.
 */
public interface IGroup {

  /**
   * Returns the TimeSet of times when every member of this group is available.
   * @return the TimeSet of times when every member of this group is available
   */
  ITimeSet getAvailability();

  /**
   * Returns the set of people that are members of this group.
   * @return the set of people that are members of this group
   */
  Set<IPerson> getMembers();

  /**
   * Returns the set of people who are both members of this group and the given group
   * @param g the group to check for common members with
   * @return the set of people who are both members of this group and the given group
   */
  Set<IPerson> getCommonMembersWith(IGroup g);

  /**
   * Returns the number of people in this group.
   * @return the number of people in this group
   */
  int size();

  /**
   * Returns a copy of this group.
   * @return a copy of this group.
   */
  IGroup copy();
}
