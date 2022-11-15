package org.example;


import org.example.types.FederalState;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class GregService {

    //set geometrical values from initial calendar
    private static final double WIDTH = 1006;
    private static final double HEIGHT = 706;
    private static final double FRAME = 15;
    private static final double HEADER_HEIGHT = 98;
    private static final double FOOTER_HEIGHT = 62;
    private static final double UPPER_SPACE_HEIGHT = 35;
    private static final double LOWER_SPACE_HEIGHT = 30;
    private static final double RECT_HEIGHT = (HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT - UPPER_SPACE_HEIGHT - LOWER_SPACE_HEIGHT - 2*FRAME) / 31;
    private static final double RECT_WIDTH = (WIDTH-2*FRAME) / 13;

    //css classes for svg
    private static final String NORMAL_RECT_STYLE = ".nRect{fill:white; stroke-width:0.3; stroke:black}";
    private static final String WEEKEND_RECT_STYLE = ".hRect{fill:#009ff3; stroke-width:0.3; stroke:black}";
    private static final String HOLIDAY_RECT_STYLE = ".fRect{fill:#b6e6ff; stroke-width:0.3; stroke:black}";
    private static final String HOLIDAY_IN_OTHER_STATE_RECT_STYLE = ".oRect{fill:#DAE9F2; stroke-width:0.3; stroke:black}";
    private static final String HEADER_RECT_STYLE = ".headerRect{fill:#00457c; stroke-width:0.3; stroke:#34568B}";
    private static final String NORMAL_TEXT_STYLE = ".nText{font-size:7pt; fill:black}";
    private static final String DATE_TEXT_STYLE = ".dateText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:bold}";
    private static final String DAY_TEXT_STYLE = ".dayText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:normal}";
    private static final String MONTH_HEADER_TEXT_STYLE = ".monthText{font-size:9pt; fill:#00457c; font-family:sans-serif; font-weight:bold}";
    private static final String HEADER_TEXT_STYLE = ".headerText{font-size:75pt; fill:white; font-family:Verdana}";
    private static final String FOOTER_TEXT_STYLE = ".footerText{font-size:12pt; fill:white; font-family:Verdana}";
    private static final String HOLIDAY_TEXT_STYLE = ".holidayText{font-size:3pt; fill:#00457c; font-family:Verdana; font-weight:normal}";

    private static final String STYLES_STRING = NORMAL_RECT_STYLE + //
            WEEKEND_RECT_STYLE + //
            HOLIDAY_RECT_STYLE + //
            HEADER_RECT_STYLE + //
            NORMAL_TEXT_STYLE + //
            DATE_TEXT_STYLE + //
            DAY_TEXT_STYLE + //
            MONTH_HEADER_TEXT_STYLE + //
            HEADER_TEXT_STYLE + //
            FOOTER_TEXT_STYLE + //
            HOLIDAY_TEXT_STYLE + //
            HOLIDAY_IN_OTHER_STATE_RECT_STYLE
            ;

    private FederalState federalState;
    //needs to be zero for January to start
    private int currentMonth = 0;
    private boolean currentDayIsHoliday = false;
    private String holidayName = "";
    public GregService(FederalState federalState) {
        this.federalState = federalState;
    }


    public SvgCalendar getGroggy(int year) {

        // initialize
        SvgCalendar svg = new SvgCalendar();
        ArrayList<TextRectGroup> textRectGroups = new ArrayList<>();

        Style style = new Style();
        style.setStyles(STYLES_STRING);

        //goes through all dates of the set period of 13 months
        for (LocalDate currentDay : getRangeOfDays(year)) {

            int xCoordinateOfCurrentMonth = 0;

            if(currentDayIsStartOfNewMonth(currentDay)) {
                //double xCoordinateOfCurrentMonth = (currentMonth * RECT_WIDTH) + FRAME;
            }

            if(isWeekend(currentDay)) {


            } else if (isHolidayInCurrentFederalState(currentDay)) {


            } else if(isHolidayInOtherFederalState(currentDay)) {

            }

            // day text
            String dayName = currentDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)//
                    .substring(0, 2)//
                    .toUpperCase();
            String dateString = currentDay.format(DateTimeFormatter.ofPattern("dd"));

            //create svg rectangle
            //Rect rect = new Rect(xCoordinateOfCurrentMonth, getY(currentDay), RECT_WIDTH, RECT_HEIGHT, getStylesClass());

            //create svg text
            Text dateText = new Text(dateString, xCoordinateOfCurrentMonth, getY(currentDay), "dateText");
            dateText.setTextAnchor("start");
            dateText.setDominantBaseline("hanging");

            Text dayText = new Text(dayName, xCoordinateOfCurrentMonth + 17, getY(currentDay), "dayText");
            dayText.setTextAnchor("start");
            dayText.setDominantBaseline("hanging");
        }

        // set everything
        svg.setStyle(style);
        svg.setHeader(getHeader(year));
        //svg.setMonthHeader(getMonthHeader(month));
        svg.setGroups(textRectGroups);
        svg.setFooter(getFooter(year));
        svg.setViewbox(String.format("0 0 %d %d", (int) WIDTH, (int) HEIGHT));
        return svg;

    }

    private double getY(LocalDate currentDay) {
        return FRAME + HEADER_HEIGHT + UPPER_SPACE_HEIGHT + (currentDay.getDayOfMonth() * RECT_HEIGHT);
    }

    public boolean isHolidayInCurrentFederalState(LocalDate currentDay) {

        HolidayService holidayService = new HolidayService();
        Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndState(currentDay.getYear(), this.federalState)
                .stream()
                .filter(holiday -> holiday.isHoliday(currentDay))
                .findFirst();
        return holidayForCurrentDate.isPresent();
    }

    public boolean isHolidayInOtherFederalState(LocalDate currentDay) {

        HolidayService holidayService = new HolidayService();
        Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndOtherFederalStates(currentDay.getYear(), this.federalState)
                .stream()
                .filter(holiday -> holiday.isHoliday(currentDay))
                .findFirst();
        return holidayForCurrentDate.isPresent();
    }

    public boolean isWeekend(LocalDate currentDay) {
        DayOfWeek curDayOfWeek = currentDay.getDayOfWeek();

        return curDayOfWeek == DayOfWeek.SUNDAY || curDayOfWeek == DayOfWeek.SATURDAY ;

    }

    private boolean currentDayIsStartOfNewMonth(LocalDate currentDay) {
        return currentMonth != currentDay.getMonth().getValue();
    }

    private static List<LocalDate> getRangeOfDays(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year +1, 1,1);

        return startDate.datesUntil(endDate, Period.ofDays(1)).toList();
    }


    public static SvgCalendar getGreg(int year, String state) {

        // initialize
        SvgCalendar svg = new SvgCalendar();

        Style style = new Style();
        // set styles
        style.setStyles(STYLES_STRING);

        HolidayService holidayService = new HolidayService();

        ArrayList<TextRectGroup> textRectGroups = new ArrayList<>();
        ArrayList<Text> monthHeaderText = new ArrayList<>();
        //TODO deal with month 13 issue
        //loop over months
        for (int cmonth = 0; cmonth < 13; cmonth++) {
            Month month = Month.of(cmonth + 1);

            // initialize x coordinate
            double xCoordinateOfCurrentMonth = (cmonth * RECT_WIDTH) + FRAME;

            // loop over days for 1 month
            for (int day = 0; day < month.length(isLeapYear(year)); day++) {

                //initialize y coordinate
                double y = getYForOldLogic(day);

                LocalDate date = LocalDate.of(year, month, day + 1);
                DayOfWeek curDayOfWeek = date.getDayOfWeek();

                boolean isWeekend = curDayOfWeek == DayOfWeek.SUNDAY || curDayOfWeek == DayOfWeek.SATURDAY;

                Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndState(year, FederalState.valueOf(state))
                        .stream()
                        .filter(holiday -> holiday.isHoliday(date))
                        .findFirst();

                Optional<Holiday> holidayInOtherStateForCurrentDate = holidayService.getHolidaysByYearAndOtherFederalStates(year, FederalState.valueOf(state))
                        .stream()
                        .filter(holiday -> holiday.isHoliday(date))
                        .findFirst();

                boolean isHolidayInState = holidayForCurrentDate.isPresent();
                boolean isHolidayInOtherSates = holidayInOtherStateForCurrentDate.isPresent();

                //check for colour of day rectangle
                String stylesClass = getStylesClass(isHolidayInState, isHolidayInOtherSates, isWeekend);

                // day text
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)//
                        .substring(0, 2)//
                        .toUpperCase();
                String dateString = date.format(DateTimeFormatter.ofPattern("dd"));

                //create svg rectangle
                Rect rect = new Rect(xCoordinateOfCurrentMonth, y, RECT_WIDTH, RECT_HEIGHT, stylesClass);

                //create svg text
                Text dateText = new Text(dateString, xCoordinateOfCurrentMonth, y, "dateText");
                dateText.setTextAnchor("start");
                dateText.setDominantBaseline("hanging");

                Text dayText = new Text(dayName, xCoordinateOfCurrentMonth + 17, y, "dayText");
                dayText.setTextAnchor("start");
                dayText.setDominantBaseline("hanging");


                //bind svg rectangle and text in <g> textrectgroup together
                TextRectGroup group = new TextRectGroup();
                group.setRect(rect);
                group.setText(dayText);
                group.setSecondText(dateText);

                if (isHolidayInState) {

                    Text holidayText = new Text(holidayForCurrentDate.get().getName(), xCoordinateOfCurrentMonth + RECT_WIDTH, y + RECT_HEIGHT, "holidayText");
                    holidayText.setTextAnchor("end");
                    holidayText.setDominantBaseline("top-bottom");

                    group.setThirdText(holidayText);
                }

                if (isHolidayInOtherSates) {

                    Text holidayText = new Text(holidayInOtherStateForCurrentDate.get().getName(), xCoordinateOfCurrentMonth + RECT_WIDTH, y + RECT_HEIGHT, "holidayText");
                    holidayText.setTextAnchor("end");
                    holidayText.setDominantBaseline("top-bottom");

                    group.setThirdText(holidayText);
                }

                //add to Array List
                textRectGroups.add(group);
            }
            monthHeaderText.add(getMonthHeader(month));
            //svg.setMonthHeader(monthHeaderText);
        }


        // set everything
        svg.setStyle(style);
        svg.setHeader(getHeader(year));
        svg.setMonthHeader(monthHeaderText);
        svg.setGroups(textRectGroups);
        svg.setFooter(getFooter(year));
        svg.setViewbox(String.format("0 0 %d %d", (int) WIDTH, (int) HEIGHT));
        return svg;
    }

    private static String getStylesClass(boolean isHolidayInState, boolean isHolidayInOtherSates, boolean isWeekend) {
        String stylesClass;
        // = isHolidayInState ? "fRect" : isHolidayInOtherSates ? "hRect" : "nRect";

        if (isWeekend) {
            stylesClass  = "hRect";
        } else if (isHolidayInState) {
            stylesClass = "fRect";
        } else if (isHolidayInOtherSates) {
            stylesClass = "oRect";
        } else {
            stylesClass = "nRect";
        }

        return stylesClass;
    }

    private static double getYForOldLogic(int day) {
        return FRAME + HEADER_HEIGHT + UPPER_SPACE_HEIGHT + (day * RECT_HEIGHT);
    }

    private static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    public static void writeCalendarAsSvg(SvgCalendar calendar, String name) {
        try {
            // Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(calendar.getClass());

            // Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Store XML to File
            File file = new File(name + ".svg");

            // Writes XML file to file-system
            jaxbMarshaller.marshal(calendar, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static TextRectGroup getHeader(int year) {
        TextRectGroup headerGroup = new TextRectGroup();
        headerGroup.setRect(new Rect(FRAME, FRAME, WIDTH - 2*FRAME, HEADER_HEIGHT, "headerRect"));
        Text headerText = new Text(String.format("%d", year), WIDTH - 1.5*FRAME, HEADER_HEIGHT + 5, "headerText");
        headerText.setTextAnchor("end");
        headerText.setDominantBaseline("text-top");

        //Text iconText = new Text();
        headerGroup.setText(headerText);

        return headerGroup;
    }

    public static TextRectGroup getFooter (int year) {
        // create and fill footer
        TextRectGroup footerGroup = new TextRectGroup();
        footerGroup.setRect(new Rect(FRAME, HEIGHT - FOOTER_HEIGHT - FRAME, WIDTH - 2*FRAME, FOOTER_HEIGHT, "headerRect"));
        Text footerText = new Text("ITEMIS LEIPZIG 2022 ".repeat(5), WIDTH / 2, (HEIGHT - FOOTER_HEIGHT) + (FOOTER_HEIGHT / 2), "footerText");
        footerText.setTextAnchor("middle");
        footerText.setDominantBaseline("middle");
        footerGroup.setText(footerText);

        return footerGroup;
    }

    public static Text getMonthHeader(Month month){
        String monthHeader = month.toString().substring(0,3);
        Text monthHeaderTextInMethod = new Text(String.format(monthHeader), FRAME, HEIGHT - HEADER_HEIGHT - UPPER_SPACE_HEIGHT - FRAME + 1, "monthText");
        monthHeaderTextInMethod.setTextAnchor("middle");
        monthHeaderTextInMethod.setDominantBaseline("middle");
        return monthHeaderTextInMethod;
    }






}
