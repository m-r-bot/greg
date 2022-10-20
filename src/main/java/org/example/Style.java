package org.example;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "style")
@XmlAccessorType(XmlAccessType.NONE)
public class Style {
    @XmlValue
    private String styles;

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getStyles() {
        return styles;
    }

    public Style(String styles) {
        this.styles = styles;
    }
    public Style(){

    }
}
