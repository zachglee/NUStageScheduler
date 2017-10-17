package model;

import java.util.*;

/**
 * Represents a group of people, designed for scheduling
 * purposes. Assigns a weight to each person, which can be
 * used to make scheduling decisions.
 */
public class Group implements IGroup {

  private String name;
  private Map<IPerson, Integer> personWeightMap;

  /**
   * Constructs a group with the given name, containing the given people as members, with each
   * person having a default weight of 12.
   * (Note: 12 is used as a default because it has many factors and thus
   * many common ratios of weights can be easily achieved, should one wish
   * to have people with different weights.)
   * @param members the people to be members of the constructed group
   */
  public Group(String name, Set<IPerson> members) {
    this.name = name;
    this.personWeightMap = new HashMap<IPerson, Integer>();
    for (IPerson p : members) {
      personWeightMap.put(p, 12);
    }
  }

  /**
   * Constructs a group with the given name, containing the given person as its only member.
   * @param name the name of the group to be constructed
   * @param singleMember the single member of the group
   */
  public Group(String name, IPerson singleMember) {
    this.name = name;
    this.personWeightMap = new HashMap<IPerson, Integer>();
    personWeightMap.put(singleMember, 12);
  }

  /**
   * Constructs an empty group.
   */
  public Group() {
    this.name = "Empty";
    this.personWeightMap = new HashMap<IPerson, Integer>();
  }

  /**
   * Constructs a Group with the given name containing the people in the given map, assigned the
   * weights the given map maps them to.
   * @param personWeightMap the map describing the people and their weights
   */
  public Group(String name, Map<IPerson, Integer> personWeightMap) {
    this.name = name;
    this.personWeightMap = personWeightMap;
  }

  @Override
  public ITimeSet getAvailability() {
    Set<IPerson> members = this.getMembers();
    List<ITimeSet> allAvailabilities = new ArrayList<ITimeSet>();
    for (IPerson p : members) {
      allAvailabilities.add(p.getCurrentAvailability());
    }
    if (allAvailabilities.size() > 0) {
      return allAvailabilities.get(0).intersect(allAvailabilities.subList(1, allAvailabilities.size()));
    } else {
      return new TimeSet();
    }
  }

  @Override
  public Set<IPerson> getMembers() {
    return personWeightMap.keySet();
  }

  @Override
  public Set<IPerson> getCommonMembersWith(IGroup g) {
    Set<IPerson> myMembers = this.getMembers();
    Set<IPerson> theirMembers = g.getMembers();
    Set<IPerson> ret = new HashSet<IPerson>();
    for (IPerson member : myMembers) {
      if (theirMembers.contains(member)) {
        ret.add(member);
      }
    }
    return ret;
  }

  @Override
  public int size() {
    return this.personWeightMap.entrySet().size();
  }

  @Override
  public String toString() {
    Set<IPerson> myMembers = this.getMembers();
    String ret = this.name + ": (";
    int i = 1;
    for (IPerson member : myMembers) {
      ret += member.toString();
      if (i != myMembers.size()) {
        ret += ", ";
      }
      i++;
    }
    return ret + ")";
  }

  @Override
  public IGroup copy() {
    Map<IPerson, Integer> copyWeightMap = new HashMap<IPerson, Integer>();
    for (IPerson p : this.personWeightMap.keySet()) {
      copyWeightMap.put(p.copy(), this.personWeightMap.get(p));
    }
    return new Group(this.name, copyWeightMap);
  }
}
