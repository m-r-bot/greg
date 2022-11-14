package org.example;

import org.example.types.FederalState;

import java.util.Locale;

public class Main {



    public static void main(String[] args) {

        //add locale default because double values are using comma instead of dot otherwise?!
        Locale.setDefault(Locale.US);

        //input values for desired year and state
        SvgCalendar calendar = GregService.getGreg(2022,"SN");
//        GregService service = new GregService(FederalState.BY);
//        service.getGroggy(2023);
        GregService.writeCalendarAsSvg(calendar, "calendar-v06");
    }

}