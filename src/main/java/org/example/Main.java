package org.example;

import java.util.Locale;

public class Main {



    public static void main(String[] args) {

        enum state{
            BW, BY, ST, BB, HE, NW, RP, SL, BE, HB, HH, SN, TH, SH, MV, NV, NI
        }

        //add locale default because double vals are using comma instead of dot otherwise?!
        Locale.setDefault(Locale.US);

        //input values for desired year and state
        SvgCalendar calendar = GregService.getGreg(2022,"Sachsen");
        GregService.writeCalendarAsSvg(calendar,"calendario-v01");
    }

}