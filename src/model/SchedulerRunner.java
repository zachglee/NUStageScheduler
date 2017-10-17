package model;

import java.io.*;

/**
 * Runs the Scheduler with the necessary files.
 */
public class SchedulerRunner {
  public static void main(String[] args) {
    File personData = new File(
        ".\\InputFiles\\personData.txt");
    File groupData = new File(
        ".\\InputFiles\\groupData.txt");
    File locationData = new File(
        ".\\InputFiles\\locationData.txt");
    File eventData = new File(
        ".\\InputFiles\\eventData.txt");
    File conflictData = new File(
        ".\\InputFiles\\conflictData.txt");
    File windowData = new File(
        ".\\InputFiles\\windowData.txt");
    IScheduleDataReader reader = new ScheduleDataReader(
        personData, groupData , locationData, eventData, conflictData, windowData,/*new TimeSet(0.0, 6.0),*/ 0, 6);
    IScheduler scheduler = new Scheduler(0.25); //15 minute blocks
    ISchedule produced = scheduler.schedule(reader.getEventsToSchedule());

    //TODO make TextOutput not be military time.
    //TODO make time windows customizable
    //TODO make chunkSize customizable
    //TODO make conflicts work
    //TODO test decimal hour durations for events
    //TODO automate location finding as well

    //print output
    System.out.println("\n\n-----------------FINAL SCHEDULE-----------------");
    System.out.println(produced.toTextOutput()
        + "-------------------------END-----------------------\n\n");

    //write output to file
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("TextSchedule.txt"), "utf-8"))) {
      writer.write(produced.toTextOutput());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
