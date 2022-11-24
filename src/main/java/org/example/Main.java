package org.example;

import org.example.types.FederalState;

import java.util.Locale;

public class Main {



    public static void main(String[] args) {

        //add locale default to set default delimiter to dot instead of German comma
        Locale.setDefault(Locale.US);

        //input values for desired year and federal state
        SvgCalendar calendar = GregService.getGreg(2022,FederalState.SN);
        GregService.writeCalendarAsSvg(calendar, "calendar-v06");
    }

}