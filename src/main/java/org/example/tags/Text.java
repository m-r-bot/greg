package org.example.tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Text {

    @XmlValue
    private String text;

    @XmlAttribute(name = "x")
    private double x;
    @XmlAttribute(name = "y")
    private double y;

    @XmlAttribute(name = "class")
    private String styleClass;

    @XmlAttribute(name = "dominant-baseline")
    private String dominantBaseline;

    @XmlAttribute(name = "text-anchor")
    private String textAnchor;

    public void setDominantBaseline(String dominantBaseline) {
        this.dominantBaseline = dominantBaseline;
    }

    public void setTextAnchor(String textAnchor) {
        this.textAnchor = textAnchor;
    }
}