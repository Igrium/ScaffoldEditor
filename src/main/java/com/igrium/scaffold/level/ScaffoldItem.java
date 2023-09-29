package com.igrium.scaffold.level;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public abstract class ScaffoldItem {
    private Vector3f position;
    private Vector3i gridPosition;

    /**
     * Get this item's position.
     * @return The exact position.
     */
    public Vector3fc getPosition() {
        return position;
    }

    /**
     * Get this item's grid position.
     * @return Position aligned to the block grid.
     */
    public Vector3i getGridPosition() {
        return gridPosition;
    }

    /**
     * Set this item's position.
     * @param position Exact position.
     */
    public void setPosition(Vector3fc position) {
        this.position.set(position);
        this.gridPosition.set(Math.round(position.x()), Math.round(position.y()), Math.round(position.z()));
    }

    /**
     * Set this item's position.
     * @param position Position on the block grid.
     */
    public void setPosition(Vector3ic position) {
        this.position.set(position);
        this.gridPosition.set(position);
    }
}
