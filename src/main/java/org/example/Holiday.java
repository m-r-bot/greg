package org.example;

import java.time.LocalDate;
import java.util.List;

public class Holiday {
    LocalDate date;
    String name;
    List<String> regions;
    boolean national;

    public Holiday(LocalDate date, String name, List<String> regions, boolean national) {
        this.date = date;
        this.name = name;
        this.regions = regions;
        this.national = national;
    }

    public Holiday(LocalDate date, String name, boolean national) {
        this.date = date;
        this.name = name;
        this.national = national;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public boolean isNational() {
        return national;
    }

    public void setNational(boolean national) {
        this.national = national;
    }

    public boolean isHoliday(LocalDate date){
        return date.isEqual(this.date);
    }
    public boolean isInRegion(String region){
        if (this.national) {
            return true;
        }
        if(this.regions.stream().anyMatch(region::equals)){
            return true;
        }else {
            return false;
        }
    }
}

