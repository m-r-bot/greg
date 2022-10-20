package org.example;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final String HEADER_TEXT_STYLE = ".headerText{font-size:30pt; fill:white; font-family:Verdana}";
    private static final String FOOTER_TEXT_STYLE = ".footerText{font-size:12pt; fill:white; font-family:Verdana}";

    private static final String STYLES_STRING = new StringBuilder()
            .append(NORMAL_RECT_STYLE) //
            .append(WEEKEND_RECT_STYLE) //
            .append(HOLIDAY_RECT_STYLE) //
            .append(HEADER_RECT_STYLE) //
            .append(NORMAL_TEXT_STYLE) //
            .append(DATE_TEXT_STYLE) //
            .append(DAY_TEXT_STYLE) //
            .append(MONTH_HEADER_TEXT_STYLE) //
            .append(HEADER_TEXT_STYLE) //
            .append(FOOTER_TEXT_STYLE) //
            .toString();


    public static SvgCalendar getGreg(int year, String state) {

        // TODO change state to enum (16 BL) and think about state having different connotations

        // initialize
        SvgCalendar svg = new SvgCalendar();

        Style style = new Style();
        // set styles
        style.setStyles(STYLES_STRING);

        HolidayService holidayService = new HolidayService();
        Set<LocalDate> holidayDates = holidayService.getGermanHolidaysByYearAndState(year, state).stream()//
                .map(holiday -> holiday.getDate())//
                .collect(Collectors.toSet());

        // check if year is a leap year
        boolean isLeapYear = (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) ? true : false;


        ArrayList<TextRectGroup> textRectGroups = new ArrayList<>();
        //TODO deal with month 13 issue
        //loop over months
        Month month = null;
        for (int cmonth = 0; cmonth < 12; cmonth++) {
            month = Month.of(cmonth + 1);
            Text monthHeader = new Text();
            //monthHeader = getMonthHeader(month);

            // initialize x coordinate
            double x = (cmonth * RECT_WIDTH) + FRAME;

            // loop over days for 1 month
            for (int day = 0; day < month.length(isLeapYear); day++) {

                //initialize y coordinate
                double y = FRAME + HEADER_HEIGHT + UPPER_SPACE_HEIGHT + (day * RECT_HEIGHT);

                LocalDate date = LocalDate.of(year, month, day + 1);
                int dval = date.getDayOfWeek().getValue();

                boolean isHoliday = holidayDates.contains(date);

                //check for colour of day rectangle
                String sclass = isHoliday ? "fRect" : (dval == 6 || dval == 7) ? "hRect" : "nRect";

                // day text
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)//
                        .substring(0, 2)//
                        .toUpperCase();
                String dateString = date.format(DateTimeFormatter.ofPattern("dd-MM"));
                //String fullDate = String.format("%s - %s", dayName, dateString);

                //create svg rectangle
                Rect rect = new Rect(x, y, RECT_WIDTH, RECT_HEIGHT, sclass);

                //create svg text
                Text dayText = new Text(dayName, x, y + RECT_HEIGHT, "dayText");
                dayText.setTextAnchor("start");
                dayText.setDominantBaseline("text-top");

                Text dateText = new Text(dateString, x + 15, y + RECT_HEIGHT, "dateText");
                //dateText.setTextAnchor("start");
                //dateText.setDominantBaseline("text-top");

                //bind svg rectangle and text in <g> textrectgroup together
                TextRectGroup group = new TextRectGroup();
                group.setRect(rect);
                group.setText(dayText);
                group.setSecondText(dateText);

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

    public static @NotNull TextRectGroup getHeader(int year, String state) {
        TextRectGroup headerGroup = new TextRectGroup();
        headerGroup.setRect(new Rect(FRAME, FRAME, WIDTH - 2*FRAME, HEADER_HEIGHT, "headerRect"));
        Text headerText = new Text(String.format("ITEMIS CALENDAR %d - State: %s", year, state), WIDTH / 2, HEADER_HEIGHT / 2, "headerText");
        headerText.setTextAnchor("middle");
        headerText.setDominantBaseline("middle");
        headerGroup.setText(headerText);

        return headerGroup;
    }

    public static @NotNull TextRectGroup getFooter (int year, String state) {
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
