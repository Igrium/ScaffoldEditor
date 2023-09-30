package com.igrium.scaffold.level.item;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.igrium.scaffold.level.attribute.Attrib;
import com.igrium.scaffold.level.attribute.AttributeHolder;
import com.igrium.scaffold.level.attributes.Vector3fAttribute;

public abstract class ScaffoldItem extends AttributeHolder {
    
    @Attrib(displayName = "Position")
    private final Vector3fAttribute position = new Vector3fAttribute();
    private Vector3i gridPosition = new Vector3i();

    public ScaffoldItem() {
        position.addChangeListener(this::onSetExactPosition);
    }

    public Vector3fAttribute positionAttribute() {
        return position;
    }

    private void onSetExactPosition(Vector3fc prev, Vector3fc position) {
        this.gridPosition.set(Math.round(position.x()), Math.round(position.y()), Math.round(position.z()));
    }

    /**
     * Get this item's position.
     * @return The exact position.
     */
    public Vector3fc getPosition() {
        return position.getValue();
    }

    /**
     * Get this item's grid position.
     * @return Position aligned to the block grid.
     */
    public Vector3ic getGridPosition() {
        return gridPosition;
    }

    /**
     * Set this item's position.
     * @param position Exact position.
     */
    public void setPosition(Vector3fc position) {
        this.position.setValue(position);
    }

    /**
     * Set this item's position.
     * @param position Position on the block grid.
     */
    public void setPosition(Vector3ic position) {
        this.position.setValue(new Vector3f(position));
    }
}
