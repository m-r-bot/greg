package org.example;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "svg")
public class SvgCalendar {

    @XmlAttribute(name = "xmlns")
    private String xmlns;

    @XmlAttribute(name = "version")
    private String version;
    @XmlAttribute(name = "viewBox")
    private String viewbox;
    @XmlElement(name = "style")
    private Style style;

    @XmlElement(name = "g")
    TextRectGroup header;

    @XmlElement(name = "g")
    ArrayList<TextRectGroup> groups;

    @XmlElement(name = "g")
    TextRectGroup footer;

    @XmlElement(name = "g")
    ArrayList<Text> monthHeader;
    @XmlElement(name = "g")
    Text calendarWeek;


    public void setVersion(String version) {
        this.version = version;
    }
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }
    public void setStyle(Style style) {
        this.style = style;
    }
    public void setViewbox(String viewbox) {
        this.viewbox = viewbox;
    }


    public void setHeader(TextRectGroup header) {
        this.header = header;
    }
    public void setGroups(ArrayList<TextRectGroup> groups) {
        this.groups = groups;
    }
    public void setFooter(TextRectGroup footer) {
        this.footer = footer;
    }
    public void setMonthHeader (ArrayList <Text> monthHeaderText) {this.monthHeader = monthHeader;}
    public void setCalendarWeek(Text calendarWeek) {
        this.calendarWeek = calendarWeek;
    }
    public SvgCalendar() {
        setXmlns("http://www.w3.org/2000/svg");
        setVersion("1.1");
        setViewbox("0 0 1000 600");
    }

}

