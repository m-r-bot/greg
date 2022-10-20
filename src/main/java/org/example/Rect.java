package org.example;


import javax.xml.bind.annotation.*;

/**
 *
 */
@XmlRootElement(name = "rect")
@XmlAccessorType(XmlAccessType.NONE)
public class Rect {

    //Data containers could be records Java 17 - do xml annotations work?

    public Rect() {}

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param styleClass
     */
    public Rect(double x, double y, double width, double height, String styleClass) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.styleClass = styleClass;
    }

    private double x;

    private double y;

    private double width;

    private double height;

    @XmlAttribute(name = "class")
    private String styleClass;


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @XmlAttribute(name = "x")
    public String getX() {
        return String.format("%.2f",x);
    }
    @XmlAttribute(name = "y")
    public String getY() {
        return String.format("%.2f",y);
    }
    @XmlAttribute(name = "width")
    public String getWidth() {
        return String.format("%.2f",width);
    }
    @XmlAttribute(name = "height")
    public String getHeight() {
        return String.format("%.2f",height);
    }
}