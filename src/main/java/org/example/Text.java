package org.example;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.NONE)
public class Text {
    public Text(String text, double x, double y, String styleClass) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.styleClass = styleClass;
    }

    public Text() {
    }

    @XmlValue
    private String text;


    private double x;
    private double y;

    @XmlAttribute(name = "class")
    private String styleClass;

    @XmlAttribute(name = "dominant-baseline")
    private String dominantBaseline;

    @XmlAttribute(name = "text-anchor")
    private String textAnchor;

    public void setText(String text) {
        this.text = text;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @XmlAttribute(name = "x")
    public String getX() {
        return String.format("%.2f",this.x);
    }
    @XmlAttribute(name = "y")
    public String getY() {
        return String.format("%.2f",this.y);
    }

    public void setDominantBaseline(String dominantBaseline) {
        this.dominantBaseline = dominantBaseline;
    }

    public void setTextAnchor(String textAnchor) {
        this.textAnchor = textAnchor;
    }
}