package com.igrium.scaffold.level.attributes;

import java.text.NumberFormat;
import java.text.ParseException;

import org.dom4j.Element;

import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

/**
 * An attribute that contains a number of some kind.
 */
public abstract class NumberAttribute<T extends Number> extends BaseAttribute<T> {

    public NumberAttribute(T initialValue) {
        super(initialValue);
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        String value = element.attributeValue("value");
        if (value == null)
            throw new InvalidXMLException(element, "No value attribute found.");

        try {
            setValue(parseString(value));

        } catch (NumberFormatException e) {
            throw new InvalidXMLException(element, "Unable to parse string as number.", e);
        }
    }

    @Override
    public void writeXML(Element element) {
        element.addAttribute("value", getValue().toString());
    }

    protected abstract T parseString(String input) throws NumberFormatException;

    /**
     * A generic number attribute that may end up holding any kind of number. It's
     * better to use type-specific attributes.
     */
    public static class GenericNumberAttribute extends NumberAttribute<Number> {

        public GenericNumberAttribute(Number initialValue) {
            super(initialValue);
        }

        @Override
        protected Number parseString(String input) throws NumberFormatException {
            try {
                return NumberFormat.getInstance().parse(input);
            } catch (ParseException e) {
                throw new NumberFormatException(input);
            }
        }

        @Override
        public Class<Number> getType() {
            return Number.class;
        }

    }

    public static class IntegerAttribute extends NumberAttribute<Integer> {

        public IntegerAttribute(Integer initialValue) {
            super(initialValue);
        }

        @Override
        protected Integer parseString(String input) throws NumberFormatException {
            return Integer.valueOf(input);
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

    }

    public static class LongAttribute extends NumberAttribute<Long> {

        public LongAttribute(Long initialValue) {
            super(initialValue);
        }

        @Override
        protected Long parseString(String input) throws NumberFormatException {
            return Long.valueOf(input);
        }

        @Override
        public Class<Long> getType() {
            return Long.class;
        }

    }

    public static class FloatAttribute extends NumberAttribute<Float> {

        public FloatAttribute(Float initialValue) {
            super(initialValue);
        }

        @Override
        protected Float parseString(String input) throws NumberFormatException {
            return Float.valueOf(input);
        }

        @Override
        public Class<Float> getType() {
            return Float.class;
        }

    }

    public static class DoubleAttribute extends NumberAttribute<Double> {

        public DoubleAttribute(Double initialValue) {
            super(initialValue);
        }

        @Override
        protected Double parseString(String input) throws NumberFormatException {
            return Double.valueOf(input);
        }

        @Override
        public Class<Double> getType() {
            return Double.class;
        }

    }

}
