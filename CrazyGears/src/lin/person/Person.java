package lin.person;

import java.util.ArrayList;

import lin.appliance.Appliance;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Person extends TiledSprite{
    ArrayList<Appliance> appliances = new ArrayList<Appliance>();
    public boolean cry = false;
    public boolean satisfied = false;
    public Person(float pX, float pY, float pWidth, float pHeight,
            ITiledTextureRegion pTiledTextureRegion, ArrayList<Appliance> pAppliances,
            VertexBufferObjectManager pTiledSpriteVertexBufferObject) {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
                pTiledSpriteVertexBufferObject);
        this.setZIndex(1);
        appliances = pAppliances;
    }
    public boolean update() {
        for (Appliance appliance : appliances) {
            if (!appliance.dead) {
                cry = false;
            }else {
                cry = true;
                break;
            }
        }
        for (Appliance appliance : appliances) {
           
            if (appliance.work) {
                satisfied = true;
            }else {
                satisfied = false;
                break;
            }
        }
        if (cry) {
            setCurrentTileIndex(2);
        }else if (satisfied) {
            setCurrentTileIndex(1);
        }else {
            setCurrentTileIndex(0);
        }
        return true;
    }
}
