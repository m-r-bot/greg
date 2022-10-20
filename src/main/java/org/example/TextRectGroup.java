package org.example;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = "g")
@XmlAccessorType(XmlAccessType.NONE)
public class TextRectGroup {
    @XmlElement
    Rect rect;

    @XmlElement
    Text text;

    @XmlElement (name = "text")
    Text secondText;

    @XmlElement (name = "text")
    Text thirdText;

    public void setRect(Rect rect) {
        this.rect = rect;
    }
    public void setText(Text text) {
        this.text = text;
    }

    public void setSecondText(Text secondText) {
        this.secondText = secondText;
    }

    public void setThirdText(Text thirdText) {
        this.thirdText = thirdText;
    }


}