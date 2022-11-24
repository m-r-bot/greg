package org.example;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "svg")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemisIcon {

    public ItemisIcon(String content, double x, double y, double height, double width) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public ItemisIcon (){}
    @XmlValue
    private String content;
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

//    @XmlAttribute(name = "g")
//    public String getContent() {
//        return this.content;
//    }

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
    // x, y, width, height, namespce siehe TExt elemnt
    // den ganzen Kladderadatsch an eigentliches svg ranhängen


