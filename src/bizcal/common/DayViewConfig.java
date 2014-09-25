package bizcal.common;

import java.util.Calendar;

import bizcal.util.DateUtil;
import bizcal.util.TimeOfDay;

public class DayViewConfig extends CalendarViewConfig {
    //	private int dayCount = 1;
    /**
     * Switched off -> nothing will be printed on top of a column!!!
     */
    private boolean showExtraDateHeaders = true;
    private boolean showDateFooter = false;
    //	private TimeOfDay startView;
    private TimeOfDay endView;
    /**
     * The alpha for the grid lines
     */
    private int gridAlpha = 50;
    /**
     * Timeslots per hour
     */
    private int numberOfTimeSlots = 4;
    /**
     * The default start time of the day view
     */
    public int DAY_START_DEFAULT = 0;
    /**
     * The default end time of the day view
     */
    public int DAY_END_DEFAULT = 24;
    /**
     * The working start time of the day view
     * Can be changed to switch to half day view
     */
    private int dayStartHour = DAY_START_DEFAULT;
    /**
     * The working end time of the day view
     * Can be changed to switch to half day view
     */
    private int dayEndHour = DAY_END_DEFAULT;
    /**
     * the day break time
     */
    private int breakHour = 24;
    // this is only for the initial position of the scrollpane
    private int dayViewStart = 0;
    private int dayViewEnd = 24;
    private int weekStart = Calendar.MONDAY;
    private int weekEnd = Calendar.FRIDAY;

    public DayViewConfig() {

        endView = new TimeOfDay(dayViewEnd, dayViewStart);

        setCaption("Calendar");
    }

    public DayViewConfig(CalendarViewConfig calViewD) {
        copy(calViewD);
    }

    /**
     * @return
     */
    public int getDayCount() {
        return DateUtil.getDiffDay(weekStart, weekEnd);
    }

    public boolean isShowExtraDateHeaders() {
        return showExtraDateHeaders;
    }

    public void setShowExtraDateHeaders(boolean showExtraDateHeaders) {
        this.showExtraDateHeaders = showExtraDateHeaders;
    }

    public void setShowDateFooter(boolean showDateFooter) {
        this.showDateFooter = showDateFooter;
    }

    public boolean isShowDateFooter() {
        return showDateFooter;
    }

    @Override
    public TimeOfDay getEndView() {
        return endView;
    }

    @Override
    public void setEndView(TimeOfDay endView) {
        this.endView = endView;
    }

    /**
     * @return the numberOfTimeSlots
     */
    public int getNumberOfTimeSlots() {
        return numberOfTimeSlots;
    }

    /**
     * @param numberOfTimeSlots the numberOfTimeSlots to set
     */
    public void setNumberOfTimeSlots(int numberOfTimeSlots) {
        this.numberOfTimeSlots = numberOfTimeSlots;
    }

    // =============================================================================
    // Default day start end hour
    // =============================================================================
    /**
     * Get the default day end hour
     *
     * @return
     */
    public int getDefaultDayEndHour() {
        /* ================================================== */
        return this.DAY_END_DEFAULT;
        /* ================================================== */
    }

    /**
     * get the default day start hour
     *
     * @return
     */
    public int getDefaultDayStartHour() {
        /* ================================================== */
        return this.DAY_START_DEFAULT;
        /* ================================================== */
    }

    public void setDefaultDayEndHour(int hour) {
        /* ================================================== */
        this.DAY_END_DEFAULT = hour;
        /* ================================================== */
    }

    public void setDefaultDayStartHour(int hour) {
        /* ================================================== */
        this.DAY_START_DEFAULT = hour;
        /* ================================================== */
    }

    // =============================================================================	
    /**
     * @return the dayEndHour
     */
    public int getDayEndHour() {
        return dayEndHour;
    }

    /**
     * @param dayEndHour the dayEndHour to set
     */
    public void setDayEndHour(int dayEndHour) {
        this.dayEndHour = dayEndHour;
    }

    /**
     * @return the dayStartHour
     */
    public int getDayStartHour() {
        return dayStartHour;
    }

    /**
     * @param dayStartHour the dayStartHour to set
     */
    public void setDayStartHour(int dayStartHour) {
        this.dayStartHour = dayStartHour;
    }

    /**
     * Returns the amount of hours to display
     *
     * @return
     */
    public int getHours() {
        /* ================================================== */
        return this.getDayEndHour() - this.getDayStartHour();
        /* ================================================== */
    }

    @Override
    public int getMinimumTimeSlotHeight() {
        /* ====================================================== */
        // TODO Auto-generated method stub
        return super.getMinimumTimeSlotHeight();
        /* ====================================================== */
    }

    /**
     * @return the dayViewEnd
     */
    public int getDayViewEnd() {
        /* ================================================== */
        return dayViewEnd;
        /* ================================================== */
    }

    /**
     * @param dayViewEnd the dayViewEnd to set
     */
    public void setDayViewEnd(int dayViewEnd) {
        this.dayViewEnd = dayViewEnd;

        endView = new TimeOfDay(dayViewEnd, 0);
    }

    /**
     * @return the dayViewStart
     */
    public int getDayViewStart() {
        return dayViewStart;
    }

    /**
     * @param dayViewStart the dayViewStart to set
     */
    public void setDayViewStart(int dayViewStart) {
        this.dayViewStart = dayViewStart;
//		startView = new TimeOfDay(dayViewStart, 0);
    }

    // ==============================================================
    // week start / week end
    // ==============================================================	
    /**
     * @return the weekEnd
     */
    public int getWeekEnd() {
        return weekEnd;
    }

    /**
     * @param weekEnd the weekEnd to set
     */
    public void setWeekStop(int weekEnd) {
        this.weekEnd = weekEnd;
    }

    /**
     * @return the weekStart
     */
    public int getWeekStart() {
        return weekStart;
    }

    /**
     * @param weekStart the weekStart to set
     */
    public void setWeekStart(int weekStart) {
        this.weekStart = weekStart;
    }

    /**
     * Set the day break hour
     *
     * @param breakHour
     */
    public void setDayBreak(int breakHour) {
        /* ====================================================== */
        this.breakHour = breakHour;
        /* ====================================================== */
    }

    /**
     * Get the day break hour
     *
     * @return
     */
    public int getDayBreak() {
        /* ====================================================== */
        return this.breakHour;
        /* ====================================================== */
    }

    /**
     * @return the gridAlpha
     */
    public int getGridAlpha() {
        return gridAlpha;
    }

    /**
     * @param gridAlpha the gridAlpha to set
     */
    public void setGridAlpha(int gridAlpha) {
        this.gridAlpha = gridAlpha;
    }
}
