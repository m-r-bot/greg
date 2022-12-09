package org.example;


import org.example.tags.Rect;
import org.example.tags.Style;
import org.example.tags.Text;
import org.example.tags.TextRectGroup;
import org.example.types.FederalState;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

public class GregService {

    //set geometrical values from initial calendar
    private static final double WIDTH = 1006;
    private static final double HEIGHT = 706;
    private static final double FRAME = 15;
    private static final double HEADER_HEIGHT = 98;
    private static final double FOOTER_HEIGHT = 62;
    private static final double UPPER_SPACE_HEIGHT = 45;
    private static final double LOWER_SPACE_HEIGHT = 20;
    private static final double RECT_HEIGHT = (HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT - UPPER_SPACE_HEIGHT - LOWER_SPACE_HEIGHT - 2 * FRAME) / 31;
    private static final double RECT_WIDTH = (WIDTH - 2 * FRAME) / 13;

    //css classes for svg
    private static final String NORMAL_RECT_STYLE = ".nRect{fill:white; stroke-width:0.3; stroke:black}";
    private static final String WEEKEND_RECT_STYLE = ".hRect{fill:#009ff3; stroke-width:0.3; stroke:black}";
    private static final String HOLIDAY_RECT_STYLE = ".fRect{fill:#b6e6ff; stroke-width:0.3; stroke:black}";
    private static final String HOLIDAY_IN_OTHER_STATE_RECT_STYLE = ".oRect{fill:#DAE9F2; stroke-width:0.3; stroke:black}";
    private static final String HEADER_RECT_STYLE = ".headerRect{fill:#00457c; stroke-width:0.3; stroke:#34568B}";
    private static final String SECOND_YEAR_RECT_STYLE = ".sRect{fill:#b6e6ff; stroke-width:0.3; stroke:black}";

    private static final String NORMAL_TEXT_STYLE = ".nText{font-size:7pt; fill:black}";
    private static final String DATE_TEXT_STYLE = ".dateText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:bold}";
    private static final String DAY_TEXT_STYLE = ".dayText{font-size:7pt; fill:#00457c; font-family:Verdana; font-weight:normal}";
    private static final String MONTH_HEADER_TEXT_STYLE = ".monthText{font-size:11pt; fill:#00457c; font-family:sans-serif; font-weight:bold}";
    private static final String HEADER_TEXT_STYLE = ".headerText{font-size:75pt; fill:white; font-family:sans-serif; font-weight:bold}";
    private static final String FOOTER_TEXT_STYLE = ".footerText{font-size:12pt; fill:white; font-family:Verdana}";
    private static final String HOLIDAY_TEXT_STYLE = ".holidayText{font-size:3pt; fill:#00457c; font-family:Verdana; font-weight:normal}";
    private static final String CALENDAR_WEEK_TEXT_STYLE = ".calendarWeekText{font-size:15pt; fill:#cdd1d3; font-family:Verdana; font-weight:bold}";
    private static final String SECOND_YEAR_DATE_TEXT_STYLE = ".secondDateText{font-size:7pt; fill:#77c3ff; font-family:Verdana; font-weight:bold}";
    private static final String SECOND_YEAR_DAY_TEXT_STYLE = ".secondDayText{font-size:7pt; fill:#77c3ff; font-family:Verdana; font-weight:normal}";
    private static final String SECOND_YEAR_HOLIDAY_TEXT_STYLE = ".secondHolidayText{font-size:3pt; fill:#77c3ff; font-family:Verdana; font-weight:normal}";


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
            HOLIDAY_IN_OTHER_STATE_RECT_STYLE + //
            CALENDAR_WEEK_TEXT_STYLE + //
            SECOND_YEAR_HOLIDAY_TEXT_STYLE + //
            SECOND_YEAR_DATE_TEXT_STYLE + //
            SECOND_YEAR_DAY_TEXT_STYLE + //
            SECOND_YEAR_RECT_STYLE;

    private int counter = 0;
    private Set<Integer> kws;

    private FederalState federalState;
    HolidayService holidayService;
    List<Holiday> holidays;

    public SvgCalendar getGreg(int year, FederalState state) throws IOException {
        int currentYear = year;

        // initialize
        this.kws = new HashSet<>();
        this.federalState = state;
        SvgCalendar svg = new SvgCalendar();
        Style style = new Style();
        holidayService = new HolidayService();
        holidays = holidayService.determineGermanHolidays(year);
        holidays.addAll(holidayService.determineGermanHolidays(year + 1));

        ArrayList<TextRectGroup> textRectGroups = new ArrayList<>();
        List<Text> monthHeaderText = new ArrayList<>();
        List<Text> calendarWeekText = new ArrayList<>();

        // set styles
        style.setStyles(STYLES_STRING);

        boolean isFollowingYear = false;

        //loop over months
        for (int cmonth = 0; cmonth < 13; cmonth++) {
            // initialize x coordinate
            double xCoordinateOfCurrentMonth = (cmonth * RECT_WIDTH) + FRAME;

            //if clause solves enum issue to draw 13th month issue
            if (cmonth == 12) {
                isFollowingYear = true;
                year = year + 1;
            }
            Month month = getMonth(cmonth);

            //add month header text elements to array list
            monthHeaderText.add(getMonthHeader(month, isFollowingYear));


            // loop over days for 1 month
            for (int day = 0; day < month.length(isLeapYear(year)); day++) {

                //initialize y coordinate
                double y = getYForOldLogic(day);

                //define time variables
                LocalDate date = LocalDate.of(year, month, day + 1);
                DayOfWeek curDayOfWeek = date.getDayOfWeek();
                boolean isWeekend = isWeekend(curDayOfWeek);

                //checks for a holiday on the current date
                Optional<Holiday> holidayForCurrentDate = getHolidayForCurrentDate(year, state, holidayService, date);
                Optional<Holiday> holidayInOtherStateForCurrentDate = getHolidayInOtherStateForCurrentDate(year, state, holidayService, date);

                boolean isHolidayInState = isHolidayInState(holidayForCurrentDate);
                boolean isHolidayInOtherSates = isHolidayInOtherSates(holidayInOtherStateForCurrentDate);


                //check for colour of day rectangle
                String stylesClass = getStylesClass(isHolidayInState, isHolidayInOtherSates, isWeekend, cmonth);

                // define labels of day
                String dayName = getDayName(date);
                String dateString = getDateString(date);

                //create svg elements
                Rect rect = getRect(xCoordinateOfCurrentMonth, y, stylesClass);
                Text dateText = getDateText(xCoordinateOfCurrentMonth, y, dateString, cmonth);
                Text dayText = getDayText(xCoordinateOfCurrentMonth, y, dayName, cmonth);

                //bind svg rectangle and text in <g> textrectgroup together
                TextRectGroup group = new TextRectGroup();
                group.setRect(rect);
                group.setText(dayText);
                group.setSecondText(dateText);

                if (isHolidayInState) {
                    Text holidayText = getHolidayTextForHolidayInState(xCoordinateOfCurrentMonth, y, holidayForCurrentDate, cmonth);
                    group.setThirdText(holidayText);
                }

                if (isHolidayInOtherSates) {
                    Text holidayText = getHolidayTextForHolidayInOtherState(xCoordinateOfCurrentMonth, y, holidayInOtherStateForCurrentDate, cmonth);
                    group.setThirdText(holidayText);
                }


                //add to Array List
                textRectGroups.add(group);
                if (date.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                    Optional<Text> label = getOptionalCWLabel(date, currentYear);
                    if (label.isPresent()) {
                        calendarWeekText.add(label.get());
                    }
                }


            }
        }

        // set everything
        svg.setStyle(style);
        svg.setHeader(getHeader(year - 1)); //TODO make this pretty
        svg.setItemisIcon(getItemisIcon());
//        Logo logo = new Logo();
//        logo.setX("25");
//        logo.setY("25");
//        logo.setHeight("88");
//        logo.setWidth("335");
//        logo.setValue("<g><g clip-path=url(#clip1) clip-rule=nonzero id=g12>\n" +
//                "  <path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 40.929688 4.257812 C 20.710938 4.257812 4.261719 20.710938 4.261719 40.929688 C 4.261719 61.148438 20.710938 77.597656 40.929688 77.597656 C 61.152344 77.597656 77.601562 61.148438 77.601562 40.929688 C 77.601562 20.710938 61.152344 4.257812 40.929688 4.257812 Z M 40.929688 81.859375 C 18.359375 81.859375 0 63.5 0 40.929688 C 0 18.359375 18.359375 0 40.929688 0 C 63.5 0 81.859375 18.359375 81.859375 40.929688 C 81.859375 63.5 63.5 81.859375 40.929688 81.859375  id=path10 />\n" +
//                "</g>\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 37.355469 68.210938 L 37.355469 30.167969 L 20.835938 30.167969 L 20.835938 36.921875 L 28.375 36.921875 L 28.375 68.210938 L 37.355469 68.210938  id=path14 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 32.65625 11.679688 C 29.371094 11.679688 26.699219 14.316406 26.699219 17.554688 C 26.699219 20.839844 29.332031 23.511719 32.570312 23.511719 C 35.910156 23.511719 38.527344 20.894531 38.527344 17.554688 C 38.527344 14.316406 35.894531 11.679688 32.65625 11.679688  id=path16 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 44.503906 68.210938 L 44.503906 30.167969 L 61.027344 30.167969 L 61.027344 36.921875 L 53.488281 36.921875 L 53.488281 68.210938 L 44.503906 68.210938  id=path18 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 49.207031 11.679688 C 52.488281 11.679688 55.164062 14.316406 55.164062 17.554688 C 55.164062 20.839844 52.527344 23.511719 49.289062 23.511719 C 45.949219 23.511719 43.332031 20.894531 43.332031 17.554688 C 43.332031 14.316406 45.96875 11.679688 49.207031 11.679688  id=path20 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 134.644531 61.996094 C 130.609375 61.996094 129.304688 59.390625 129.304688 56.023438 L 129.304688 36.921875 L 139.878906 36.921875 L 139.878906 30.175781 L 129.304688 30.175781 L 129.304688 23.511719 L 120.316406 23.511719 C 120.292969 30.507812 120.316406 52.648438 120.316406 56.246094 C 120.316406 60.472656 121.867188 63.726562 124.15625 65.921875 C 126.449219 68.117188 129.757812 69.230469 133.988281 69.230469 C 139.082031 69.230469 142.378906 67.429688 144.570312 65.0625 L 139.917969 59.109375 C 138.738281 60.582031 137.109375 61.996094 134.644531 61.996094  id=path22 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 216.273438 29.125 C 211.363281 29.125 208.078125 30.746094 205.878906 32.914062 C 203.667969 30.746094 200.382812 29.125 195.46875 29.125 C 180.496094 29.125 180.597656 41.25 180.597656 44.128906 C 180.597656 47.003906 180.597656 68.214844 180.597656 68.214844 L 189.574219 68.214844 L 189.574219 44.132812 C 189.574219 35.609375 194.449219 35.875 195.46875 35.875 C 196.492188 35.875 201.398438 35.609375 201.398438 44.132812 L 201.398438 68.214844 L 210.378906 68.214844 L 210.378906 44.132812 C 210.378906 35.609375 215.253906 35.875 216.273438 35.875 C 217.296875 35.875 222.203125 35.609375 222.203125 44.132812 L 222.203125 68.214844 L 231.179688 68.214844 C 231.179688 68.214844 231.179688 46.976562 231.179688 44.128906 C 231.179688 41.277344 231.246094 29.125 216.273438 29.125  id=path24 />\n" +
//                "<g clip-path=url(#clip2) clip-rule=nonzero id=g28>\n" +
//                "  <path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 271.75 44.839844 C 267.46875 43.410156 265.757812 42.355469 265.757812 39.691406 C 265.757812 37.707031 266.871094 35.875 270.042969 35.875 C 273.246094 35.875 274.492188 39.179688 275.179688 40.664062 C 277.875 39.160156 280.496094 37.78125 281.828125 37.019531 C 279.300781 30.984375 275.539062 29.125 269.789062 29.125 C 261.769531 29.125 258.121094 34.609375 258.121094 39.765625 C 258.121094 46.242188 261.066406 49.570312 269.3125 52 C 275.273438 53.753906 275.789062 56.742188 275.519531 58.195312 C 275.203125 59.917969 273.90625 61.746094 270.175781 61.746094 C 266.597656 61.746094 264.363281 57.914062 263.425781 56.332031 C 261.597656 57.472656 259.996094 58.421875 257.050781 60.09375 C 259.128906 65.117188 263.878906 69.175781 270.597656 69.175781 C 278.511719 69.175781 283.464844 64.109375 283.464844 57.074219 C 283.464844 49.441406 278.136719 46.976562 271.75 44.839844  id=path26 />\n" +
//                "</g>\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 238.304688 68.210938 L 238.304688 30.164062 L 254.828125 30.164062 L 254.828125 36.921875 L 247.289062 36.921875 L 247.289062 68.210938 L 238.304688 68.210938  id=path30 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 243.007812 11.679688 C 246.289062 11.679688 248.964844 14.316406 248.964844 17.554688 C 248.964844 20.839844 246.328125 23.511719 243.089844 23.511719 C 239.75 23.511719 237.132812 20.894531 237.132812 17.554688 C 237.132812 14.316406 239.769531 11.679688 243.007812 11.679688  id=path32 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 113.234375 68.210938 L 113.234375 30.164062 L 96.714844 30.164062 L 96.714844 36.921875 L 104.25 36.921875 L 104.25 68.210938 L 113.234375 68.210938  id=path34 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 108.53125 11.679688 C 105.25 11.679688 102.574219 14.316406 102.574219 17.554688 C 102.574219 20.839844 105.210938 23.511719 108.449219 23.511719 C 111.789062 23.511719 114.40625 20.894531 114.40625 17.554688 C 114.40625 14.316406 111.773438 11.679688 108.53125 11.679688  id=path36 />\n" +
//                "<path style= stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1; d=M 154.257812 44.121094 C 154.421875 37.03125 157.597656 35.875 160.214844 35.875 C 162.832031 35.875 166.113281 36.921875 166.257812 44.121094 Z M 174.976562 50.878906 L 174.976562 47.140625 C 174.976562 34.859375 170.285156 29.125 160.214844 29.125 C 150.886719 29.125 145.539062 35.699219 145.539062 47.140625 L 145.539062 51.097656 C 145.539062 62.636719 151.292969 69.253906 161.332031 69.253906 C 167.976562 69.253906 172.128906 65.519531 174.054688 61.394531 C 171.824219 60.035156 169.472656 58.558594 167.960938 57.625 C 166.980469 59.316406 165.066406 62.035156 161.246094 62.035156 C 155.460938 62.035156 154.246094 56.085938 154.246094 51.097656 L 154.246094 50.878906 L 174.976562 50.878906 \" id=\"path38\" />\n" +
//                "</g>");
//        svg.setLogo(logo);
        svg.setMonthHeader(monthHeaderText);
        svg.setGroups(textRectGroups);
        svg.setCalendarWeek(calendarWeekText);
        svg.setFooter(getFooter());
        svg.setViewbox(String.format("0 0 %d %d", (int) WIDTH, (int) HEIGHT));
        return svg;
    }

    private boolean isHolidayInOtherSates(Optional<Holiday> holidayInOtherStateForCurrentDate) {
        boolean isHolidayInOtherSates = holidayInOtherStateForCurrentDate.isPresent();
        return isHolidayInOtherSates;
    }

    private boolean isHolidayInState(Optional<Holiday> holidayForCurrentDate) {
        boolean isHolidayInState = holidayForCurrentDate.isPresent();
        return isHolidayInState;
    }

    private boolean isWeekend(DayOfWeek curDayOfWeek) {
        boolean isWeekend = curDayOfWeek == DayOfWeek.SUNDAY || curDayOfWeek == DayOfWeek.SATURDAY;
        return isWeekend;
    }

    private Month getMonth(int cmonth) {
        Month month = cmonth == 12 ? Month.of(1) : Month.of(cmonth + 1);
        return month;
    }

    public void writeCalendarAsSvg(SvgCalendar calendar, String name) {
        try {
            // Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(calendar.getClass());

            // Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Store XML to File
            File file = new File(name + ".svg");

            // file.toString().replace("&lt;" , "<").replace("&rt", ">");

//            Transformer nullTransformer = TransformerFactory.newInstance().newTransformer();
//            nullTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            nullTransformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "myElement {myNamespace}myOtherElement");
//            nullTransformer.transform(new DOMSource((Node) calendar), new StreamResult(writer/stream));

            // Writes XML file to file-system
            jaxbMarshaller.marshal(calendar, file);


        } catch (JAXBException e) {
            e.printStackTrace();
//        } catch (TransformerConfigurationException e) {
//            throw new RuntimeException(e);
        }
    }

    private boolean isSecondYear(int cmonth) {
        return cmonth == 12;
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


    private Text getHolidayTextForHolidayInOtherState(double xCoordinateOfCurrentMonth, double y, Optional<Holiday> holidayInOtherStateForCurrentDate, int cmonth) {
        Text holidayText = Text.builder()
                .text(holidayInOtherStateForCurrentDate.get().getName())
                .x(xCoordinateOfCurrentMonth + RECT_WIDTH)
                .y(y + RECT_HEIGHT - 1)
                .styleClass("holidayText")
                .textAnchor("end")
                .dominantBaseline("top-bottom")
                .build();

        if (cmonth == 12) holidayText.setStyleClass("secondHolidayText");
        return holidayText;
    }
//TODO works but check if theres is a more elegant solution than checking for cmonth every single time

    private Text getHolidayTextForHolidayInState(double xCoordinateOfCurrentMonth, double y, Optional<Holiday> holidayForCurrentDate, int cmonth) {
        Text holidayText = Text.builder()
                .text(holidayForCurrentDate.get().getName())
                .x(xCoordinateOfCurrentMonth + RECT_WIDTH)
                .y(y + RECT_HEIGHT - 1)
                .styleClass("holidayText")
                .textAnchor("end")
                .dominantBaseline("top-bottom")
                .build();

        if (cmonth == 12) holidayText.setStyleClass("secondHolidayText");
        return holidayText;
    }


    private Optional<Holiday> getHolidayInOtherStateForCurrentDate(int year, FederalState state, HolidayService holidayService, LocalDate date) {
        Optional<Holiday> holidayInOtherStateForCurrentDate = holidayService.getHolidaysByYearAndOtherFederalStates(year, state)
                .stream()
                .filter(holiday -> holiday.isHoliday(date))
                .findFirst();
        return holidayInOtherStateForCurrentDate;
    }


    private Optional<Holiday> getHolidayForCurrentDate(int year, FederalState state, HolidayService holidayService, LocalDate date) {
        Optional<Holiday> holidayForCurrentDate = holidayService.getHolidaysByYearAndState(year, state)
                .stream()
                .filter(holiday -> holiday.isHoliday(date))
                .findFirst();
        return holidayForCurrentDate;
    }


    private String getDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd"));
    }


    private String getDayName(LocalDate date) {
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)//
                .substring(0, 2)//
                .toUpperCase();
        return dayName;
    }


    private Rect getRect(double xCoordinateOfCurrentMonth, double y, String stylesClass) {
        return new Rect(xCoordinateOfCurrentMonth, y, RECT_WIDTH, RECT_HEIGHT, stylesClass);
    }


    private Text getDayText(double xCoordinateOfCurrentMonth, double y, String dayName, int cmonth) {
        Text dayText = Text.builder()
                .text(dayName)
                .x(xCoordinateOfCurrentMonth + 17)
                .y(y)
                .styleClass("dayName")
                .textAnchor("start")
                .dominantBaseline("hanging")
                .build();
        if (cmonth == 12) dayText.setStyleClass("secondDayText");
        return dayText;
    }


    private Text getDateText(double xCoordinateOfCurrentMonth, double y, String dateString, int cmonth) {
        Text dateText = Text.builder()
                .text(dateString)
                .x(xCoordinateOfCurrentMonth)
                .y(y)
                .styleClass("dateText")
                .textAnchor("start")
                .dominantBaseline("hanging")
                .build();
        if (cmonth == 12) dateText.setStyleClass("secondDateText");
        return dateText;
    }

    private String getStylesClass(boolean isHolidayInState, boolean isHolidayInOtherSates, boolean isWeekend, int cmonth) {
        String stylesClass;
//TODO change to defensive programming so break up the nested if function
        if (cmonth == 12 && (isWeekend || isHolidayInState || isHolidayInOtherSates)) {
            stylesClass = "sRect";
        } else if (isWeekend) {
            stylesClass = "hRect";
        } else if (isHolidayInState) {
            stylesClass = "fRect";
        } else if (isHolidayInOtherSates) {
            stylesClass = "oRect";
        } else {
            stylesClass = "nRect";
        }
        return stylesClass;
    }

    private double getYForOldLogic(int day) {
        return FRAME + HEADER_HEIGHT + UPPER_SPACE_HEIGHT + (day * RECT_HEIGHT);
    }

    private boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    private TextRectGroup getHeader(int year) {
        TextRectGroup headerGroup = new TextRectGroup();
        headerGroup.setRect(new Rect(FRAME, FRAME, WIDTH - 2 * FRAME, HEADER_HEIGHT, "headerRect"));

        Text headerText = Text.builder()
                .text(String.format("%d", year))
                .x(WIDTH - 1.5 * FRAME)
                .y(HEADER_HEIGHT + 5)
                .styleClass("headerText")
                .textAnchor("end")
                .dominantBaseline("text-top")
                .build();
        headerGroup.setText(headerText);
        return headerGroup;
    }

    private TextRectGroup getFooter() {
        // create and fill footer
        TextRectGroup footerGroup = new TextRectGroup();
        footerGroup.setRect(new Rect(FRAME, HEIGHT - FOOTER_HEIGHT - FRAME, WIDTH - 2 * FRAME, FOOTER_HEIGHT, "headerRect"));
        Text footerText = Text.builder()
                .text("ITEMIS LEIPZIG 2022 ".repeat(5)).
                x(WIDTH / 2).
                y((HEIGHT - FOOTER_HEIGHT) + (FOOTER_HEIGHT / 2))
                .textAnchor("middle")
                .dominantBaseline("middle")
                .build();
        footerGroup.setText(footerText);
        return footerGroup;
    }

    private Text getMonthHeader(Month month, Boolean isFollowingYear) {
        //checks if it is the second January and assigns xCoordinate accordingly
        double xCoordinateOfCurrentMonth = isFollowingYear ? ((month.getValue() - 1 + 12) * RECT_WIDTH) + FRAME : ((month.getValue() - 1) * RECT_WIDTH) + FRAME;

        //define displayed string
        String monthHeader = month.toString().substring(0, 3);

        Text monthHeaderTextInMethod = Text.builder()
                .text(String.format(monthHeader))
                .x(xCoordinateOfCurrentMonth + RECT_WIDTH / 2)
                .y(HEADER_HEIGHT + UPPER_SPACE_HEIGHT + 5)
                .textAnchor("middle")
                .styleClass("monthText")
                .dominantBaseline("middle")
                .build();
        return monthHeaderTextInMethod;
    }

    private Text getCalendarWeek(LocalDate date, TextRectGroup group) {
        WeekFields weekField = WeekFields.of(Locale.GERMANY);
        int valueOfCalendarWeek = date.get(weekField.weekOfWeekBasedYear());
        double xCoordinateOfCalenderWeek = Double.parseDouble(group.getRect().getX()) + RECT_WIDTH / 2;
        ;
        double yCoordinateOfCalenderWeek = Double.parseDouble(group.getRect().getY());
        Text calendarWeekText = Text.builder()
                .text(String.valueOf(valueOfCalendarWeek))
                .x(xCoordinateOfCalenderWeek).y(yCoordinateOfCalenderWeek)
                .styleClass("calendarWeekText")
                .dominantBaseline("text-top")
                .build();
        return calendarWeekText;
    }

    private Text getCWLabelEl(LocalDate date, boolean isPrimaryYear) {
        WeekFields weekField = WeekFields.of(Locale.GERMANY);
        int valueOfCalendarWeek = date.get(weekField.weekOfWeekBasedYear());
        int cmonth = isPrimaryYear ? date.getMonthValue() - 1 : date.getMonthValue() + 12 - 1;
        double xCoordinateOfCurrentMonth = (cmonth * RECT_WIDTH) + FRAME + 55;
        double yCoordinateOfCurrentMonth = getYForOldLogic(date.getDayOfMonth() - 1) + 5;
        Text calendarWeekText = Text.builder()
                .text(String.valueOf(valueOfCalendarWeek)).x(xCoordinateOfCurrentMonth)
                .y(yCoordinateOfCurrentMonth)
                .styleClass("calendarWeekText")
                .dominantBaseline("hanging")
                .build();
        return calendarWeekText;
    }

    // unique handle for kw year
    private int getKwYear(LocalDate date) {
        WeekFields weekField = WeekFields.of(Locale.GERMANY);
        return date.get(weekField.weekOfWeekBasedYear());
    }

    LocalDate getFirstMondayInYear(int year) {
        return LocalDate.of(year, 1, 1).with(firstInMonth(DayOfWeek.MONDAY));
    }

    public Optional<Text> getOptionalCWLabel(LocalDate date, int year) {
        boolean isPrimaryYear = year == date.getYear();
        for (int i = date.getDayOfWeek().getValue(); i < DayOfWeek.SATURDAY.getValue(); i++) {
            if (isWeekend(date.getDayOfWeek()) || isWeekend(date.plusDays(1).getDayOfWeek()) || date.lengthOfMonth() - date.getDayOfMonth() < 1) {
                date = date.plusDays(1);
                continue;
            } else if (holidayService.isHoliday(date, holidays) || holidayService.isHoliday(date.plusDays(1), holidays)) {
                date = date.plusDays(1);
                continue;
            } else {
                Text el = getCWLabelEl(date, isPrimaryYear);
                return Optional.ofNullable(el);
            }
        }
        return Optional.empty();
    }

    private ItemisIcon getItemisIcon() throws IOException {
        String icon = """
                <g clip-path="url(#clip1)" clip-rule="nonzero" id="g12">
                  <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 40.929688 4.257812 C 20.710938 4.257812 4.261719 20.710938 4.261719 40.929688 C 4.261719 61.148438 20.710938 77.597656 40.929688 77.597656 C 61.152344 77.597656 77.601562 61.148438 77.601562 40.929688 C 77.601562 20.710938 61.152344 4.257812 40.929688 4.257812 Z M 40.929688 81.859375 C 18.359375 81.859375 0 63.5 0 40.929688 C 0 18.359375 18.359375 0 40.929688 0 C 63.5 0 81.859375 18.359375 81.859375 40.929688 C 81.859375 63.5 63.5 81.859375 40.929688 81.859375 " id="path10" />
                </g>
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 37.355469 68.210938 L 37.355469 30.167969 L 20.835938 30.167969 L 20.835938 36.921875 L 28.375 36.921875 L 28.375 68.210938 L 37.355469 68.210938 " id="path14" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 32.65625 11.679688 C 29.371094 11.679688 26.699219 14.316406 26.699219 17.554688 C 26.699219 20.839844 29.332031 23.511719 32.570312 23.511719 C 35.910156 23.511719 38.527344 20.894531 38.527344 17.554688 C 38.527344 14.316406 35.894531 11.679688 32.65625 11.679688 " id="path16" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 44.503906 68.210938 L 44.503906 30.167969 L 61.027344 30.167969 L 61.027344 36.921875 L 53.488281 36.921875 L 53.488281 68.210938 L 44.503906 68.210938 " id="path18" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 49.207031 11.679688 C 52.488281 11.679688 55.164062 14.316406 55.164062 17.554688 C 55.164062 20.839844 52.527344 23.511719 49.289062 23.511719 C 45.949219 23.511719 43.332031 20.894531 43.332031 17.554688 C 43.332031 14.316406 45.96875 11.679688 49.207031 11.679688 " id="path20" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 134.644531 61.996094 C 130.609375 61.996094 129.304688 59.390625 129.304688 56.023438 L 129.304688 36.921875 L 139.878906 36.921875 L 139.878906 30.175781 L 129.304688 30.175781 L 129.304688 23.511719 L 120.316406 23.511719 C 120.292969 30.507812 120.316406 52.648438 120.316406 56.246094 C 120.316406 60.472656 121.867188 63.726562 124.15625 65.921875 C 126.449219 68.117188 129.757812 69.230469 133.988281 69.230469 C 139.082031 69.230469 142.378906 67.429688 144.570312 65.0625 L 139.917969 59.109375 C 138.738281 60.582031 137.109375 61.996094 134.644531 61.996094 " id="path22" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 216.273438 29.125 C 211.363281 29.125 208.078125 30.746094 205.878906 32.914062 C 203.667969 30.746094 200.382812 29.125 195.46875 29.125 C 180.496094 29.125 180.597656 41.25 180.597656 44.128906 C 180.597656 47.003906 180.597656 68.214844 180.597656 68.214844 L 189.574219 68.214844 L 189.574219 44.132812 C 189.574219 35.609375 194.449219 35.875 195.46875 35.875 C 196.492188 35.875 201.398438 35.609375 201.398438 44.132812 L 201.398438 68.214844 L 210.378906 68.214844 L 210.378906 44.132812 C 210.378906 35.609375 215.253906 35.875 216.273438 35.875 C 217.296875 35.875 222.203125 35.609375 222.203125 44.132812 L 222.203125 68.214844 L 231.179688 68.214844 C 231.179688 68.214844 231.179688 46.976562 231.179688 44.128906 C 231.179688 41.277344 231.246094 29.125 216.273438 29.125 " id="path24" />
                <g clip-path="url(#clip2)" clip-rule="nonzero" id="g28">
                  <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 271.75 44.839844 C 267.46875 43.410156 265.757812 42.355469 265.757812 39.691406 C 265.757812 37.707031 266.871094 35.875 270.042969 35.875 C 273.246094 35.875 274.492188 39.179688 275.179688 40.664062 C 277.875 39.160156 280.496094 37.78125 281.828125 37.019531 C 279.300781 30.984375 275.539062 29.125 269.789062 29.125 C 261.769531 29.125 258.121094 34.609375 258.121094 39.765625 C 258.121094 46.242188 261.066406 49.570312 269.3125 52 C 275.273438 53.753906 275.789062 56.742188 275.519531 58.195312 C 275.203125 59.917969 273.90625 61.746094 270.175781 61.746094 C 266.597656 61.746094 264.363281 57.914062 263.425781 56.332031 C 261.597656 57.472656 259.996094 58.421875 257.050781 60.09375 C 259.128906 65.117188 263.878906 69.175781 270.597656 69.175781 C 278.511719 69.175781 283.464844 64.109375 283.464844 57.074219 C 283.464844 49.441406 278.136719 46.976562 271.75 44.839844 " id="path26" />
                </g>
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 238.304688 68.210938 L 238.304688 30.164062 L 254.828125 30.164062 L 254.828125 36.921875 L 247.289062 36.921875 L 247.289062 68.210938 L 238.304688 68.210938 " id="path30" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 243.007812 11.679688 C 246.289062 11.679688 248.964844 14.316406 248.964844 17.554688 C 248.964844 20.839844 246.328125 23.511719 243.089844 23.511719 C 239.75 23.511719 237.132812 20.894531 237.132812 17.554688 C 237.132812 14.316406 239.769531 11.679688 243.007812 11.679688 " id="path32" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 113.234375 68.210938 L 113.234375 30.164062 L 96.714844 30.164062 L 96.714844 36.921875 L 104.25 36.921875 L 104.25 68.210938 L 113.234375 68.210938 " id="path34" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 108.53125 11.679688 C 105.25 11.679688 102.574219 14.316406 102.574219 17.554688 C 102.574219 20.839844 105.210938 23.511719 108.449219 23.511719 C 111.789062 23.511719 114.40625 20.894531 114.40625 17.554688 C 114.40625 14.316406 111.773438 11.679688 108.53125 11.679688 " id="path36" />
                <path style=" stroke:none;fill-rule:nonzero;fill:white;fill-opacity:1;" d="M 154.257812 44.121094 C 154.421875 37.03125 157.597656 35.875 160.214844 35.875 C 162.832031 35.875 166.113281 36.921875 166.257812 44.121094 Z M 174.976562 50.878906 L 174.976562 47.140625 C 174.976562 34.859375 170.285156 29.125 160.214844 29.125 C 150.886719 29.125 145.539062 35.699219 145.539062 47.140625 L 145.539062 51.097656 C 145.539062 62.636719 151.292969 69.253906 161.332031 69.253906 C 167.976562 69.253906 172.128906 65.519531 174.054688 61.394531 C 171.824219 60.035156 169.472656 58.558594 167.960938 57.625 C 166.980469 59.316406 165.066406 62.035156 161.246094 62.035156 C 155.460938 62.035156 154.246094 56.085938 154.246094 51.097656 L 154.246094 50.878906 L 174.976562 50.878906 " id="path38" />
                              """;

        ItemisIcon currentIcon = new ItemisIcon(icon, FRAME + 10, FRAME + 10, HEADER_HEIGHT - 10, WIDTH / 3);
        return currentIcon;
    }


}
