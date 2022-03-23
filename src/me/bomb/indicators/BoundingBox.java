package me.bomb.indicators;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

class BoundingBox {

    protected Vector max;
    protected Vector min;
    BoundingBox(Vector min, Vector max) {
        this.max = max;
        this.min = min;
    }
    BoundingBox(Entity entity){
    	switch(CSI.version) {
    	case v1_14_R1 :
    		net.minecraft.server.v1_14_R1.AxisAlignedBB box14 = ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity) entity).getHandle().getBoundingBox();
            min = new Vector(box14.minX - CSI.getBoxSizeModificator(),box14.minY - CSI.getBoxSizeModificator(),box14.minZ - CSI.getBoxSizeModificator());
            max = new Vector(box14.maxX + CSI.getBoxSizeModificator(),box14.maxY + CSI.getBoxSizeModificator(),box14.maxZ + CSI.getBoxSizeModificator());
    	break;
    	case v1_15_R1 :
    		net.minecraft.server.v1_15_R1.AxisAlignedBB box15 = ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity) entity).getHandle().getBoundingBox();
            min = new Vector(box15.minX - CSI.getBoxSizeModificator(),box15.minY - CSI.getBoxSizeModificator(),box15.minZ - CSI.getBoxSizeModificator());
            max = new Vector(box15.maxX + CSI.getBoxSizeModificator(),box15.maxY + CSI.getBoxSizeModificator(),box15.maxZ + CSI.getBoxSizeModificator());
    	break;
    	case v1_16_R3 :
    		net.minecraft.server.v1_16_R3.AxisAlignedBB box16 = ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity) entity).getHandle().getBoundingBox();
            min = new Vector(box16.minX - CSI.getBoxSizeModificator(),box16.minY - CSI.getBoxSizeModificator(),box16.minZ - CSI.getBoxSizeModificator());
            max = new Vector(box16.maxX + CSI.getBoxSizeModificator(),box16.maxY + CSI.getBoxSizeModificator(),box16.maxZ + CSI.getBoxSizeModificator());
    	break;
    	}
    	
    }
    //BoundingBox (AxisAlignedBB box){
    //    min = new Vector(box.minX,box.minY,box.minZ);
    //    max = new Vector(box.maxX,box.maxY,box.maxZ);
    //}

    protected Vector midPoint(){
        return max.clone().add(min).multiply(0.5);
    }

}