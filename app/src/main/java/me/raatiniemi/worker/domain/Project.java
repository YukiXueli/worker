package me.raatiniemi.worker.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Project extends DomainObject
{
    private String mName;

    private String mDescription;

    private ArrayList<Time> mTime;

    /**
     * Initialize an existing project.
     * @param id Id for the project.
     * @param name Name of the project.
     */
    public Project(Long id, String name)
    {
        super(id);

        setName(name);
        setTime(new ArrayList<Time>());
    }

    /**
     * Initialize a new project without an id.
     * @param name Name of the project.
     */
    public Project(String name)
    {
        this(null, name);
    }

    /**
     * Set the project name.
     * @param name Project name.
     */
    public void setName(String name)
    {
         mName = name;
    }

    /**
     * Retrieve the project name.
     * @return Project name.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Set the project description.
     * @param description Project description.
     */
    public void setDescription(String description)
    {
        mDescription = description;
    }

    /**
     * Retrieve the project description.
     * @return Project description.
     */
    public String getDescription()
    {
        return mDescription;
    }

    private void setTime(ArrayList<Time> time)
    {
        mTime = time;
    }

    public ArrayList<Time> getTime()
    {
        return mTime;
    }

    public void addTime(Time time)
    {
        getTime().add(time);
    }

    public String summarizeTime()
    {
        // TODO: Migrate to date interval handler.

        // Total time in number of seconds.
        long total = 0;

        ArrayList<Time> time = getTime();
        if (null != time && !time.isEmpty()) {
            for (Time item: time) {
                total += item.getTime();
            }
        }

        // Convert milliseconds to seconds.
        total = total / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = (total / (60 * 60) % 24);
        long minutes = (total / 60 % 60);

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long seconds = (total % 60);
        if (seconds >= 30) {
            minutes += 1;
        }

        return String.format("%dh %dm", hours, minutes);
    }

    /**
     * Retrieve the time domain object that might be active.
     * @return Time domain object, or null if no time have been registered.
     */
    private Time getActiveTime()
    {
        ArrayList<Time> time = getTime();

        if (time == null || time.isEmpty()) {
            return null;
        }

        return time.get(time.size() - 1);
    }

    /**
     * Retrieve the time when the project was clocked in.
     * @return Time when project was clocked in, or null if project is not active.
     */
    public String getClockedInSince()
    {
        // TODO: Just return the value for getStart() and parse it outside of the domain object.

        // If the project is not active, there's nothing to do.
        if (!isActive()) {
            return null;
        }

        // TODO: Handle if the time session overlap days.
        // The timestamp should include the date it was
        // checked in, e.g. 21 May 1:06PM.

        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        Date date = new Date(time.getStart());

        // Format the timestamp with hours and minutes.
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    /**
     * Clock out the active project, if the project is not active nothing happens.
     * @return The clocked out time domain object, or null if project is not active.
     */
    public Time clockOut()
    {
        // If the project is not active, there's nothing to do.
        if (!isActive()) {
            return null;
        }

        Time time = getActiveTime();
        time.clockOut();

        return time;
    }

    /**
     * Check if the project is active.
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive()
    {
        boolean active = false;

        // Retrieve the last element of the time array and check if the
        // item is active, hence defines if the project is active.
        Time time = getActiveTime();
        if (time != null) {
            active = time.isActive();
        }

        return active;
    }
}
