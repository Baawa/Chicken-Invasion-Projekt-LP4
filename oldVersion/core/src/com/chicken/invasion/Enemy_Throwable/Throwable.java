package com.chicken.invasion.Enemy_Throwable;

import com.chicken.invasion.Helpers.CollisionRect;

/**
 * Created by Albin on 2016-08-06.
 */
public interface Throwable {
    void decHP(float amount);
    float getHP();

    void setX(float x);
    void setY(float y);

    float getX();
    float getY();

    float getRotation();

    float getDamage();

    Object getSprite();

    String getImageURL();

    void setCollided(Boolean collided);
    Boolean hasCollided();

    void throwToPoint(float x, float y);

    boolean isThrown();

    void updatePosition();

    float getScale();

    CollisionRect getCollisionRect();

    void removeFromWorld();

    void removeSprite();

    void dispose();
}
