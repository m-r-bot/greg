package org.example;

import com.sun.xml.bind.AnyTypeAdapter;
import org.example.types.FederalState;

import java.io.*;
import java.util.Locale;

public class Main {



    public static void main(String[] args) throws IOException {

        //add locale default to set default delimiter to dot instead of German comma
        Locale.setDefault(Locale.US);

        //input values for desired year and federal state
        SvgCalendar calendar = GregService.getGreg(2022,FederalState.SN);
        GregService.writeCalendarAsSvg(calendar, "calendar-v06");

        File file = new File("C:/Users/mboese/Desktop/Dev/greg/calendar-v06.svg");
        //calendar.toString().replace("&lt;" , "<").replace("&rt", ">");
       try {
           BufferedReader br = new BufferedReader(new FileReader(file));

           String line;
           while ((line = br.readLine()) != null) {
               String str = line.replace("&lt;", "<").replace("&rt", ">");
               System.out.println(str);

               BufferedWriter bw = new BufferedWriter(new FileWriter(file));
               bw.write(str);
               bw.close();
           }
           br.close();

       } catch (IOException e){
           e.printStackTrace();
       }

    }

}