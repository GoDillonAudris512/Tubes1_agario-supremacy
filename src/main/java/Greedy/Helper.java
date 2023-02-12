package Greedy;

import Models.*;

public class Helper {
    public double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public double getDistanceFromCenter (GameObject item) {
        var triangleX = Math.abs(item.getPosition().x);
        var triangleY = Math.abs(item.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public int getHeadingBetween(GameObject other, GameObject bot) {
        var direction = toDegrees(Math.atan2(other.getPosition().y - bot.getPosition().y,
                other.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    public int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    public int getHeadingFromCenter(GameObject other) {
        return (toDegrees(Math.atan2(other.getPosition().y, other.getPosition().x)) + 360) % 360;
    }
}