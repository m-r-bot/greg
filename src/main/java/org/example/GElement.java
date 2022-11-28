package org.example;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
//@XmlRootElement(name = "g")
@XmlType(propOrder = { "g" })
public class GElement
{
    private String g;
    public String getG() { return g; }
    @XmlAnyElement(BodyDomHandler.class)
    public void setG (String g) {this.g = g;}
}

//lüge, das müsste eigentlich so funktionieren