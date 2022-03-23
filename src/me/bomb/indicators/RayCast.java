package me.bomb.indicators;

import java.util.ArrayList;
import org.bukkit.util.Vector;

class RayCast {
	//origin = start position
    //direction = direction in which the raytrace will go
    protected Vector origin, direction;

    RayCast(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
    RayCast(Vector origin, float yaw,float pitch) {
        this.origin = origin;
        float f = TheMath.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = TheMath.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -TheMath.cos(-pitch * 0.017453292F);
        float f3 = TheMath.sin(-pitch * 0.017453292F);
        this.direction = new Vector((double) (f1 * f2), (double) f3, (double) (f * f2));;
    }
    //get a point on the raytrace at X blocks away
    protected Vector getPostion(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    //checks if a position is on contained within the position
    protected boolean isOnLine(Vector position) {
        double t = (position.getX() - origin.getX()) / direction.getX();
        ;
        if (position.getBlockY() == origin.getY() + (t * direction.getY()) && position.getBlockZ() == origin.getZ() + (t * direction.getZ())) {
            return true;
        }
        return false;
    }

    //get all postions on a raytrace
    protected ArrayList<Vector> traverse(double blocksAway, double accuracy) {
        ArrayList<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPostion(d));
        }
        return positions;
    }

    //intersection detection for current raytrace with return
    protected Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
        ArrayList<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }

    //intersection detection for current raytrace
    protected boolean intersects(Vector min, Vector max, double blocksAway, double accuracy) {
        ArrayList<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }

    //bounding box instead of vector
    protected Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy) {
        ArrayList<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return position;
            }
        }
        return null;
    }
    //bounding box instead of vector
    protected boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
        ArrayList<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return true;
            }
        }
        return false;
    }

    //general intersection detection
    protected static boolean intersects(Vector position, Vector min, Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else if (position.getZ() < min.getZ() || position.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }
}
