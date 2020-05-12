package it.stefanocasagrande.readxml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

public class Classes {

    @Root
    public static class prices
    {
        @ElementList(entry="pitch", inline=true)
        public List<pitch> pitch;
    }

    @Root
    public static class pitch
    {
        @Attribute
        public String securityId;

        @Attribute
        public String considerationCurrency;

        @ElementList(entry="price", inline=true)
        public List<price> price;
    }

    @Root
    public static class price
    {
        @Attribute
        public String actionIndicator;

        @Attribute
        public double limit_kg;

        @Attribute
        public String limit_fmt_kg;

        @Attribute
        public String limit_fmt_oz;
    }

}
