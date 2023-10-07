package com.igrium.scaffold.level.element;

import org.apache.commons.lang3.RandomStringUtils;
import org.dom4j.Element;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.igrium.scaffold.level.Level;
import com.igrium.scaffold.level.attribute.Attrib;
import com.igrium.scaffold.level.attribute.Attribute;
import com.igrium.scaffold.level.attribute.AttributeHolder;
import com.igrium.scaffold.level.attributes.Vector3fAttribute;
import com.igrium.scaffold.level.stack.StackElement;
import com.igrium.scaffold.util.InvalidXMLException;
import com.igrium.scaffold.util.XMLSerializable;
import com.mojang.logging.LogUtils;

public abstract class ScaffoldElement extends AttributeHolder implements XMLSerializable {


    /**
     * A unique ID that's used to identify this element during serialization.
     */
    private String id = RandomStringUtils.randomAlphanumeric(8);

    private final ElementType<?> type;

    public ElementType<?> getType() {
        return type;
    }

    /**
     * Get this element's ID.
     * @return A unique ID that's used to identify this element during serialization.
     */
    public final String getId() {
        return id;
    }

    @Deprecated
    public final void setId(String id) {
        this.id = id;
    }

    private Level level;

    public Level getLevel() {
        return level;
    }

    public ScaffoldElement(ElementType<?> type, Level level) {
        this.level = level;
        position.addChangeListener(this::onSetExactPosition);
        this.type = type;
    }

    private StackElement stackItem = new StackElement(this);
    
    /**
     * This element's "handle" on the level stack.
     * @return Element handle.
     */
    public StackElement getStackItem() {
        return stackItem;
    }

    @Attrib(displayName = "Position")
    private final Vector3fAttribute position = new Vector3fAttribute();
    private Vector3i gridPosition = new Vector3i();

    public Vector3fAttribute positionAttribute() {
        return position;
    }

    private void onSetExactPosition(Vector3fc prev, Vector3fc position) {
        this.gridPosition.set(Math.round(position.x()), Math.round(position.y()), Math.round(position.z()));
    }

    /**
     * Get this element's position.
     * 
     * @return The exact position.
     */
    public Vector3fc getPosition() {
        return position.getValue();
    }

    /**
     * Get this element's grid position.
     * 
     * @return Position aligned to the block grid.
     */
    public Vector3ic getGridPosition() {
        return gridPosition;
    }

    /**
     * Set this element's position.
     * 
     * @param position Exact position.
     */
    public void setPosition(Vector3fc position) {
        this.position.setValue(position);
    }

    /**
     * Set this element's position.
     * 
     * @param position Position on the block grid.
     */
    public void setPosition(Vector3ic position) {
        this.position.setValue(new Vector3f(position));
    }

    public void readXML(Element element) {
        String id = element.attributeValue("id");
        if (id != null && !id.isEmpty())
            setId(id);
    
        for (Element attribElement : element.elements("attribute")) {
            String attributeName = attribElement.attributeValue("name");
            if (attributeName == null || attributeName.isEmpty())
                continue;

            Attribute<?> attribute = getAttribute(attributeName);
            if (attribute == null) {
                LogUtils.getLogger().warn("No attribute called '" + attributeName + "'!");
                continue;
            }

            try {
                attribute.readXML(attribElement);
            } catch (InvalidXMLException e) {
                LogUtils.getLogger()
                        .error(String.format("Unable to parse attribute '%s' on item: %s", attributeName, id), e);
            }
        }
    }

    public void writeXML(Element element) {
        element.addAttribute("id", getId());
        
        for (var entry : getAttributes().entrySet()) {
            Element attribElement = element.addElement("attribute");
            entry.getValue().writeXML(attribElement);
            attribElement.addAttribute("name", entry.getKey());
        }
    }
}
