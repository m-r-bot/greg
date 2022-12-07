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
        int year = 2022;
        FederalState federalState = FederalState.SN;
        String versionName = "calendar-v07";
        GregService service = new GregService();
        SvgCalendar calendar = service.getGreg(year, federalState);
        service.writeCalendarAsSvg(calendar, versionName);

        File file = new File("C:/Users/mboese/Desktop/Dev/greg/" + versionName + ".svg");
        String fileContext = FileUtils.readFileToString(file);
        fileContext = fileContext.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        FileUtils.write(file, fileContext);

//        File file = new File("C:/Users/mboese/Desktop/Dev/greg/calendar-v06.svg");
//        //calendar.toString().replace("&lt;" , "<").replace("&rt", ">");
//       try {
//           BufferedReader br = new BufferedReader(new FileReader(file));
//
//           String line;
//           while ((line = br.readLine()) != null) {
//               String str = line.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//               System.out.println(str);
//
//               BufferedWriter bw = new BufferedWriter(new FileWriter(file));
//               bw.write(str);
//               bw.wait(3000); // warum owned er das nicht
//               bw.close();
//           }
//           br.close();
//
//       } catch (IOException e){
//           e.printStackTrace();
//       } catch (InterruptedException e) {
//           throw new RuntimeException(e);
//       }




    }

}