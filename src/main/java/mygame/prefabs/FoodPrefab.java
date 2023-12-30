package mygame.prefabs;

import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.ai.AIControl;
import mygame.weapon.Damageable;


public class FoodPrefab extends PrefabComponent {

    public enum FoodType {
        BREAD, CUPCAKE, PIZZA, MUSHROOM_POISON
    }

    public FoodPrefab(Application app) {
        super(app);
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Node food = new Node("Food");
        FoodType type = getRandomFoodType();
        Spatial foodModel = createFoodModel(type);
        
        foodModel.setName(type.name());
        food.attachChild(foodModel);

        System.out.println("Food Type: " + type.name());
        System.out.println("Spawn Position: " + position.toString());

       
        food.setLocalTranslation(position);
        food.setLocalRotation(rotation);
        parent.attachChild(food);

        return food;
    }
   
    

    private FoodType getRandomFoodType() {
        FoodType[] foodTypes = FoodType.values();
        int randomIndex = FastMath.nextRandomInt(0, foodTypes.length - 1);
        return foodTypes[randomIndex];
    }
    private Spatial createFoodModel(FoodType type) {
        Spatial foodModel = assetManager.loadModel(getGlbFilePath(type));

        switch (type) {
            case MUSHROOM_POISON:
                scaleAndNameModel(foodModel, 0.2f, "MUSHROOM_POISON");
                break;
            case BREAD:
                scaleAndNameModel(foodModel, 0.1f, "BREAD");
                break;
            case CUPCAKE:
                scaleAndNameModel(foodModel, 0.9f, "CUPCAKE");
                break;
            case PIZZA:
                scaleAndNameModel(foodModel, 4f, "PIZZA");
                break;
        }

        foodModel.scale(0.5f);
        foodModel.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        foodModel.setQueueBucket(Bucket.Transparent);

        CollisionShape foodShape = CollisionShapeFactory.createMeshShape(foodModel);
        RigidBodyControl foodRigidBody = new RigidBodyControl(foodShape, 0f);
        foodModel.addControl(foodRigidBody);
        getPhysicsSpace().add(foodRigidBody);

        return foodModel;
    }
    private void scaleAndNameModel(Spatial foodModel, float scale, String name) {
        foodModel.scale(scale);
        foodModel.setName(name);
    }


    private String getGlbFilePath(FoodType type) {
        switch (type) {
            case BREAD:
                return "Models/FoodItems/bread.glb";
            case CUPCAKE:
                return "Models/FoodItems/cupcake.glb";
            case PIZZA:
                return "Models/FoodItems/pizza.glb";
            case MUSHROOM_POISON:
                return "Models/FoodItems/mushroom_poison.glb";
         
            default:
                return ""; 
        }
    }

}
