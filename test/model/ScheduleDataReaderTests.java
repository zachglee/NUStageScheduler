package model;

import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the ScheduleDataReader class.
 */
public class ScheduleDataReaderTests {

  @Test
  public void testBasicExample() {
    File personData = new File(
        ".\\test\\model\\TestInputFiles\\personData.txt");
    File groupData = new File(
        ".\\test\\model\\TestInputFiles\\groupData.txt");
    File locationData = new File(
        ".\\test\\model\\TestInputFiles\\locationData.txt");
    File eventData = new File(
        ".\\test\\model\\TestInputFiles\\eventData.txt");
    File conflictData = new File(
        ".\\test\\model\\TestInputFiles\\conflictData.txt");
    File windowData = new File(
        ".\\InputFiles\\windowData.txt");
    IScheduleDataReader reader = new ScheduleDataReader(
        personData, groupData , locationData, eventData, conflictData, windowData,/*new TimeSet(0.0, 6.0),*/ 0, 0);
    IScheduler scheduler = new Scheduler(1.0);
    ISchedule produced = scheduler.schedule(reader.getEventsToSchedule());
    assertEquals("EventA1", produced.getEventAt(new TimeSet(4.0, 5.0), "Room1").getName());
    assertEquals("EventB1", produced.getEventAt(new TimeSet(1.0, 3.0), "Room1").getName());
    assertEquals("EventC1", produced.getEventAt(new TimeSet(3.0, 4.0), "Room1").getName());
    assertEquals("EventA2", produced.getEventAt(new TimeSet(0.0, 2.0), "Room2").getName());
    assertEquals("EventD1", produced.getEventAt(new TimeSet(4.0, 6.0), "Room2").getName());
    assertEquals("EventE1", produced.getEventAt(new TimeSet(2.0, 3.0), "Room2").getName());

    //toString
    System.out.println("\n\n-----------------FINAL SCHEDULE-----------------");
    System.out.println(produced.toTextOutput()
        + "-------------------------END-----------------------\n\n");

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("TextSchedule.txt"), "utf-8"))) {
      writer.write(produced.toTextOutput());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

}
