package org.example;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "svg")
@XmlAccessorType(XmlAccessType.NONE)
public class Logo {
    @XmlAttribute
    private String height;

    @XmlAttribute
    private String width;

    @XmlAttribute
    private String x;

    @XmlAttribute
    private String y;

    @XmlValue
    private String value;


    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
