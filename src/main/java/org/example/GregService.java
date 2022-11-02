package org.example;


import org.example.types.FederalState;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class GregService {

    private static final double WIDTH = 1006;
    private static final double HEIGHT = 706;
    private static final double FRAME = 15;
    private static final double HEADER_HEIGHT = 98;
    private static final double FOOTER_HEIGHT = 62;
    private static final double UPPER_SPACE_HEIGHT = 35;
    private static final double LOWER_SPACE_HEIGHT = 30;
    private static final double RECT_HEIGHT = (HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT - UPPER_SPACE_HEIGHT - LOWER_SPACE_HEIGHT - 2*FRAME) / 31;
    private static final double RECT_WIDTH = (WIDTH-2*FRAME) / 13;


    private static final String NORMAL_RECT_STYLE = ".nRect{fill:white; stroke-width:0.3; stroke:black}";
    private static final String WEEKEND_RECT_STYLE = ".hRect{fill:#009ff3; stroke-width:0.3; stroke:black}";
    private static final String HOLIDAY_RECT_STYLE = ".fRect{fill:#b6e6ff; stroke-width:0.3; stroke:black}";
    private static final String HEADER_RECT_STYLE = ".headerRect{fill:#00457c; stroke-width:0.3; stroke:#34568B}";
    private static final String NORMAL_TEXT_STYLE = ".nText{font-size:7pt; fill:black}";
    private static final String DATE_TEXT_STYLE = ".dateText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:bold}";
    private static final String DAY_TEXT_STYLE = ".dayText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:normal}";
    private static final String MONTH_HEADER_TEXT_STYLE = ".monthText{font-size:15pt; fill:#00457c; font-family:sans-serif; font-weight:bold}";
    private static final String HEADER_TEXT_STYLE = ".headerText{font-size:75pt; fill:white; font-family:Verdana}";
    private static final String FOOTER_TEXT_STYLE = ".footerText{font-size:12pt; fill:white; font-family:Verdana}";
    private static final String HOLIDAY_TEXT_STYLE = ".holidayText{font-size:4pt; fill:#00457c; font-family:Verdana; font-weight:normal}";


    private final FederalState federalState;

    private int currentMonth = 0;
    private boolean currentDayIsHoliday = false;
    private String holidayName = "";

    private HolidayService hs = ();

    public GregService(FederalState federalState) {
        this.federalState = federalState;
    }

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
            HOLIDAY_TEXT_STYLE //
            ;

    public SvgCalendar getGroggy(int year) {

        for (LocalDate currentDay : getRangeOfDays(year)) {

            if(currentDayIsStartOfNewMonth(currentDay)) {
                initNewMonth();
            }

            if(isWeekend(currentDay)) {
                initWeekend();

            } else if (isHolidayInCurrentFederalState(currentDay)) {
                initHolidayInCurrentSate()

            } else if(isHolidayInOtherFederalState()) {
                initHolidayInOtherState()
            }

            printDay()
        }


    }

    private boolean isHolidayInCurrentFederalState(LocalDate currentDay) {

        HolidayService holidayService = new HolidayService();
        Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndState(currentDay.getYear(), this.federalState)
                .stream()
                .filter(holiday -> holiday.isHoliday(currentDay))
                .findFirst();
        return holidayForCurrentDate.isPresent();
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

        // TODO change state to enum (16 BL) and think about state having different connotations

        // initialize
        SvgCalendar svg = new SvgCalendar();

        Style style = new Style();
        // set styles
        style.setStyles(STYLES_STRING);

        HolidayService holidayService = new HolidayService();

        ArrayList<TextRectGroup> textRectGroups = new ArrayList<>();
        //TODO deal with month 13 issue
        //loop over months
        for (int cmonth = 0; cmonth < 12; cmonth++) {
            Month month = Month.of(cmonth + 1);
            Text monthHeader = new Text();
            //monthHeader = getMonthHeader(month);

            // initialize x coordinate
            double xCoordinateOfCurrentMonth = (cmonth * RECT_WIDTH) + FRAME;

            // loop over days for 1 month
            for (int day = 0; day < month.length(isLeapYear(year)); day++) {

                //initialize y coordinate
                double y = getY(day);

                LocalDate date = LocalDate.of(year, month, day + 1);
                DayOfWeek curDayOfWeek = date.getDayOfWeek();

                boolean isWeekend =  curDayOfWeek == DayOfWeek.SUNDAY || curDayOfWeek == DayOfWeek.SATURDAY ;

                Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndState(year, FederalState.valueOf(state))
                        .stream()
                        .filter(holiday -> holiday.isHoliday(date))
                        .findFirst();

                boolean isHolidayInState = holidayForCurrentDate.isPresent();

                //check for colour of day rectangle
                String sclass = isHolidayInState ? "fRect" : isWeekend ? "hRect" : "nRect";

                // day text
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)//
                        .substring(0, 2)//
                        .toUpperCase();
                String dateString = date.format(DateTimeFormatter.ofPattern("dd"));

                //create svg rectangle
                Rect rect = new Rect(xCoordinateOfCurrentMonth, y, RECT_WIDTH, RECT_HEIGHT, sclass);

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

                //add to Array List
                textRectGroups.add(group);
            }
        }


        // set everything
        svg.setStyle(style);
        svg.setHeader(getHeader(year, state));
        //svg.setMonthHeader(getMonthHeader(month));
        svg.setGroups(textRectGroups);
        svg.setFooter(getFooter(year, state));
        svg.setViewbox(String.format("0 0 %d %d", (int) WIDTH, (int) HEIGHT));
        return svg;
    }

    private static double getY(int day) {
        double y = FRAME + HEADER_HEIGHT + UPPER_SPACE_HEIGHT + (day * RECT_HEIGHT);
        return y;
    }

    private static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    private static boolean compareDates(LocalDate dateHoliday, LocalDate curDate) {
        return dateHoliday.equals(curDate);
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

    public static TextRectGroup getHeader(int year, String icon) {
        TextRectGroup headerGroup = new TextRectGroup();
        headerGroup.setRect(new Rect(FRAME, FRAME, WIDTH - 2*FRAME, HEADER_HEIGHT, "headerRect"));
        Text headerText = new Text(String.format("%d", year), WIDTH - 1.5*FRAME, HEADER_HEIGHT + 5, "headerText");
        headerText.setTextAnchor("end");
        headerText.setDominantBaseline("text-top");

        Text iconText = new Text();
        headerGroup.setText(headerText);

        return headerGroup;
    }

    public static TextRectGroup getFooter (int year, String state) {
        // create and fill footer
        TextRectGroup footerGroup = new TextRectGroup();
        footerGroup.setRect(new Rect(FRAME, HEIGHT - FOOTER_HEIGHT - FRAME, WIDTH - 2*FRAME, FOOTER_HEIGHT, "headerRect"));
        Text footerText = new Text("ITEMIS LEIPZIG 2022 ".repeat(5), WIDTH / 2, (HEIGHT - FOOTER_HEIGHT) + (FOOTER_HEIGHT / 2), "footerText");
        footerText.setTextAnchor("middle");
        footerText.setDominantBaseline("middle");
        footerGroup.setText(footerText);

        return footerGroup;
    }

/*    public static Text getMonthHeader(Month month){
        String monthHeader = String.valueOf(month.getValue()).substring(0,3);
        Text text = new Text(String.format("%d", monthHeader), FRAME, HEADER_HEIGHT - UPPER_SPACE_HEIGHT - FRAME + 1, "monthText");
        return text;
    }*/







}
