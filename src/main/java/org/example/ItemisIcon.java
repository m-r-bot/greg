package org.example;

import javax.xml.bind.annotation.*;
import java.io.StringReader;

@XmlRootElement(name = "svg")
//@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "g" })
public class ItemisIcon {

    public ItemisIcon(String g, double x, double y, double height, double width) {
        this.g = g;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public ItemisIcon (){}
    private String g;
    private double x;
    private double y;
    private double height;

    private double width;

    @XmlAttribute(name = "x")
    public String getX() {
        return String.format("%.2f",this.x);
    }
    @XmlAttribute(name = "y")
    public String getY() {
        return String.format("%.2f",this.y);
    }
    @XmlAttribute(name = "height")
    public String getHeight() {
        return String.format("%.2f",this.height);
    }

    @XmlAttribute(name = "width")
    public String getWidth() {
        return String.format("%.2f",this.width);
    }

    @XmlElement(name = "g")
    private String getG() {
        return this.g;
    }

    public void setG (String g) {this.g = g;}

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
    // x, y, width, height, namespce siehe Text elemnt
    // den ganzen Kladderadatsch an eigentliches svg ranhängen


