package com.igrium.scaffold.level.attribute;

public interface AttributeChangeListener<T> {
    public void onChanged(T oldValue, T newValue);
}
