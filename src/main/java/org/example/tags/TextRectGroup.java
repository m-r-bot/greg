package org.example.tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tags.Rect;
import org.example.tags.Text;

import javax.xml.bind.annotation.*;


@XmlRootElement(name = "g")
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class TextRectGroup {
    @XmlElement
    Rect rect;

    @XmlElement
    Text text;

    @XmlElement (name = "text")
    Text secondText;

    @XmlElement (name = "text")
    Text thirdText;

}