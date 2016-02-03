package b5.project.medibro;

/**
 * Created by Abhijith on 1/30/2016.
 */
public class Medication {

    private int id;
    private String name, intervals, reminder_times, start_date, duration, notes;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntervals() {
        return intervals;
    }

    public void setIntervals(String intervals) {
        this.intervals = intervals;
    }

    public String getReminder_times() {
        return reminder_times;
    }

    public void setReminder_times(String reminder_times) {
        this.reminder_times = reminder_times;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
