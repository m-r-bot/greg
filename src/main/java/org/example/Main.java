package org.example;

import com.sun.xml.bind.AnyTypeAdapter;
import org.apache.commons.io.FileUtils;
import org.example.types.FederalState;

import java.io.*;
import java.util.Locale;

public class Main {



    public static void main(String[] args) throws IOException {

        //add locale default to set default delimiter to dot instead of German comma
        Locale.setDefault(Locale.US);

        //input values for desired year and federal state
        int year = 2023;
        //to change federal state select fitting
        FederalState federalState = FederalState.SN;
        String versionName = "calendar-v07";
        GregService service = new GregService();
        SvgCalendar calendar = service.getGreg(year, federalState);
        service.writeCalendarAsSvg(calendar, versionName);

        File file = new File(versionName + ".svg");
        String fileContext = FileUtils.readFileToString(file);
        fileContext = fileContext.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        FileUtils.write(file, fileContext);

    }

}