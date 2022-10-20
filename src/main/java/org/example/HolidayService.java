package org.example;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class HolidayService {
    public LocalDate calculateEasterDate(int year) {
        // Gaußsche Osterformel
        final int a = year % 19;
        final int b = year % 4;
        final int c = year % 7;
        final int k = year / 100;
        final int p = k / 3;
        final int q = k / 4;
        final int m = (15 + k - p - q) % 30;
        final int d = (19 * a + m) % 30;
        final int n = (4 + k - q) % 7;
        final int e = (2 * b + 4 * c + 6 * d + n) % 7;
        final int easter = (22 + d + e);
        Month month = easter > 31 ? Month.APRIL : Month.MARCH;
        int day = easter > 31 ? easter - 31 : easter;
        return LocalDate.of(year, month, day);
    }

    public List<Holiday> determineGermanHolidays(int year) {
        final String DE = "Deutschland";
        final String BW = "Baden-Württemberg";
        final String BY = "Bayern";
        final String ST = "Sachsen-Anhalt";
        final String BB = "Brandenburg";
        final String HE = "Hessen";
        final String NW = "Nordrhein-Westfalen";
        final String RP = "Rheinland-Pfalz";
        final String SL = "Saarland";
        final String BE = "Berlin";
        final String HB = "Bremen";
        final String HH = "Hamburg";
        final String SN = "Sachsen";
        final String TH = "Thüringen";
        final String SH = "Schleswig-Holstein";
        final String MV = "Mecklenburg-Vorpommern";
        final String NV = "Nordrhein-Westfalen";
        final String NI = "Niedersachsen";

        // Fixed Holidays
        final LocalDate NEUJAHR = LocalDate.of(year, Month.JANUARY, 1);
        final LocalDate HEILIGE_DER_DREI_KOENIGE = LocalDate.of(year, Month.JANUARY, 6);
        final LocalDate FRAUEN_TAG = LocalDate.of(year, Month.MARCH, 8);
        final LocalDate TAG_DER_ARBEIT = LocalDate.of(year, Month.MAY, 1);
        final LocalDate MARIA_HIMMELFAHRT = LocalDate.of(year, Month.AUGUST, 15);
        final LocalDate WELT_KINDER_TAG = LocalDate.of(year, Month.SEPTEMBER, 20);
        final LocalDate TAG_DER_DEUTSCHEN_EINHEIT = LocalDate.of(year, Month.OCTOBER, 3);
        final LocalDate REFORMATIONSTAG = LocalDate.of(year, Month.OCTOBER, 31);
        final LocalDate ALLERHEILIGEN = LocalDate.of(year, Month.NOVEMBER, 1);
        final LocalDate ERSTER_WEIHNACHTSTAG = LocalDate.of(year, Month.DECEMBER, 25);
        final LocalDate ZWEITER_WEIHNACHTSTAG = ERSTER_WEIHNACHTSTAG.plusDays(1);

        // Easter-based Holidays
        final LocalDate OSTER_SONNTAG = calculateEasterDate(year);
        final LocalDate OSTER_MONTAG = OSTER_SONNTAG.plusDays(1);
        final LocalDate KAR_FREITAG = OSTER_SONNTAG.minusDays(2);
        final LocalDate CHRISTI_HIMMEL_FAHRT = OSTER_SONNTAG.plusDays(39);
        final LocalDate PFINGST_SONNTAG = OSTER_SONNTAG.plusDays(49);
        final LocalDate PFINGST_MONTAG = PFINGST_SONNTAG.plusDays(1);
        final LocalDate FRONLEICHNAM = OSTER_SONNTAG.plusDays(60);

        final LocalDate BUSS_UND_BETTAG = LocalDate.of(year, Month.NOVEMBER, 23)
                .with(TemporalAdjusters.previous(DayOfWeek.WEDNESDAY));

        Map<String, LocalDate> nationalHolidays = new HashMap<>();
        nationalHolidays.put("Neujahr", NEUJAHR);
        nationalHolidays.put("Karfreitag", KAR_FREITAG);
        nationalHolidays.put("Ostermontag", OSTER_MONTAG);
        nationalHolidays.put("Christi Himmelfahrt", CHRISTI_HIMMEL_FAHRT);
        nationalHolidays.put("Pfingstmontag", PFINGST_MONTAG);
        nationalHolidays.put("Tag der Arbeit", TAG_DER_ARBEIT);
        nationalHolidays.put("Tag der Deutschen Einheit", TAG_DER_DEUTSCHEN_EINHEIT);
        nationalHolidays.put("Erster Weihnachtstag", ERSTER_WEIHNACHTSTAG);
        nationalHolidays.put("Zweiter Weihnachtstag", ZWEITER_WEIHNACHTSTAG);

        // Date of local holidays.
        Map<String, LocalDate> regionalHolidays = new HashMap<>();
        regionalHolidays.put("Heilige Drei Könige", HEILIGE_DER_DREI_KOENIGE);
        regionalHolidays.put("Frauen Tag", FRAUEN_TAG);
        regionalHolidays.put("Buß- und Bettag", BUSS_UND_BETTAG);
        regionalHolidays.put("Weltkindertag", WELT_KINDER_TAG);
        regionalHolidays.put("Ostersonntag", OSTER_SONNTAG);
        regionalHolidays.put("Pfingstsonntag", PFINGST_SONNTAG);
        regionalHolidays.put("Fronleichnam", FRONLEICHNAM);
        regionalHolidays.put("Mariä Himmelfahrt", MARIA_HIMMELFAHRT);
        regionalHolidays.put("Reformationstag", REFORMATIONSTAG);
        regionalHolidays.put("Allerheiligen", ALLERHEILIGEN);

        Map<String, List<String>> regionalHolidaysRegions = new HashMap<>();
        regionalHolidaysRegions.put("Heilige Drei Könige", Arrays.asList(BW, BY, ST));
        regionalHolidaysRegions.put("Frauen Tag", Arrays.asList(BE));
        regionalHolidaysRegions.put("Buß- und Bettag", Arrays.asList(SN));
        regionalHolidaysRegions.put("Weltkindertag", Arrays.asList(TH));
        regionalHolidaysRegions.put("Ostersonntag", Arrays.asList(BB));
        regionalHolidaysRegions.put("Pfingstsonntag", Arrays.asList(BB));
        regionalHolidaysRegions.put("Fronleichnam", Arrays.asList(BW, BY, HE, ST, NW, RP, SL));
        regionalHolidaysRegions.put("Mariä Himmelfahrt", Arrays.asList(SL));
        regionalHolidaysRegions.put("Reformationstag",
                Arrays.asList(BB, HE, HB, HH, MV, NI, SN, ST, SH, TH));
        regionalHolidaysRegions.put("Allerheiligen", Arrays.asList(BW, BY, NV, RP, SL));

        List<Holiday> holidays = new ArrayList<>();

        nationalHolidays.forEach((holiday, date) ->holidays.add(new Holiday(date,holiday,true)) );
        regionalHolidays.forEach((holiday, date) -> holidays.add(new Holiday(date,holiday,regionalHolidaysRegions.get(holiday),false)));

        return holidays;
    }

    public List<Holiday> getGermanHolidaysByYearAndState(int year, String state){
        List<Holiday> holidays = this.determineGermanHolidays(year);
        List<Holiday> regionHolidays = holidays.stream().filter(holiday -> holiday.isInRegion(state)).toList();
        return regionHolidays;
    }


    }
