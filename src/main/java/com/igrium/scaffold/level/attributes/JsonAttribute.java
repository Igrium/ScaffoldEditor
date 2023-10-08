package com.igrium.scaffold.level.attributes;

import java.util.function.Supplier;

import org.dom4j.Element;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igrium.scaffold.level.attribute.BaseAttribute;
import com.igrium.scaffold.util.InvalidXMLException;

/**
 * An attribute that can store arbitrary json (gson) data.
 */
public class JsonAttribute<T> extends BaseAttribute<T> {

    private final Gson gson;
    private final Class<T> type;
    private final Supplier<T> constructor;

    public JsonAttribute(Gson gson, Class<T> type, Supplier<T> constructor) {
        this.gson = gson;
        this.type = type;
        this.constructor = constructor;
    }

    public JsonAttribute(Class<T> type, Supplier<T> constructor) {
        this.gson = new Gson();
        this.type = type;
        this.constructor = constructor;
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void readXML(Element element) throws InvalidXMLException {
        try {
            T value = gson.fromJson(element.getText(), type);
            setValue(value);

        } catch (JsonSyntaxException e) {
            throw new InvalidXMLException(element, "Unable to parse json.", e);
        }
    }

    @Override
    public void writeXML(Element element) {
        element.setText(gson.toJson(element));
    }

    @Override
    protected T defaultValue() {
        return constructor.get();
    }
    
}
