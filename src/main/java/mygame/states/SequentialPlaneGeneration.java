package mygame.states;


import com.capdevon.engine.SimpleAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;

public class SequentialPlaneGeneration extends SimpleAppState {

    private float planeWidth = 5f; // Adjust the width of the plane
    private float planeLength = 5f; // Adjust the length of the planedd
    private float planeHeight = 0.1f; // Adjust the height of the plane

    private float spawnInterval = 2f; // Time interval between plane spawns
    private float timeSinceLastSpawn = 0f;

    private Geometry currentPlane;
    private Geometry nextPlane;

    float next_Z_Position = 0f;


    @Override
    public void simpleInit() {
        spawnInitialPlane();
    }

    @Override
    public void update(float tpf) {

        timeSinceLastSpawn += tpf;
        if (timeSinceLastSpawn >= spawnInterval) {
            generateNextPlane();
            timeSinceLastSpawn = 0f;
        }
    }

    private void spawnInitialPlane() {
        currentPlane = generatePlane(0f, 0f, 0f);
        rootNode.attachChild(currentPlane);
    }

    private void generateNextPlane() {
        next_Z_Position += planeLength;
        if (nextPlane != null) {
            rootNode.detachChild(nextPlane);
        }
        nextPlane = generatePlane(0, 0f, next_Z_Position);
        rootNode.attachChild(nextPlane);
    
        // Destroy the previous plane
        if (currentPlane != null) {
            // Remove its RigidBodyControl from the physics space
            RigidBodyControl currentRigidBody = currentPlane.getControl(RigidBodyControl.class);
            if (currentRigidBody != null) {
                app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(currentRigidBody);
            }
    
            rootNode.detachChild(currentPlane);
            currentPlane = nextPlane;
        }
        spawnBuildingsOnSide(planeWidth , planeHeight , -next_Z_Position); // Left side
        spawnBuildingsOnSide(planeWidth , planeHeight, next_Z_Position);
    }

    private String[] buildingModels = {
        "Models/Buildings/Building_01.j3o",
    };
        private void spawnBuildingsOnSide(float xOffset, float yOffset, float zOffset) {
        // Logic to spawn buildings on a particular side of the road
        // Example logic to spawn buildings on the left/right side of the road
        // Modify this based on your building models and desired spawning logic

        // Sample code to spawn a building on the side of the road
        String randomModel = buildingModels[FastMath.nextRandomInt(0, buildingModels.length - 1)];
        Spatial building = app.getAssetManager().loadModel(randomModel);
        building.setLocalTranslation(xOffset, yOffset, zOffset);
        // Add necessary scaling, rotation, etc., to position the building correctly
        // ...
        building.scale(0.1f);

        // Attach the building to the root node
        rootNode.attachChild(building);
    }
   private Geometry generatePlane(float x, float y, float z) {
        Box planeShape = new Box(planeWidth , planeHeight, planeLength );
        Geometry plane = new Geometry("Plane", planeShape);

        Material planeMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        planeMat.setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/RoadTexture.jpg"));
        plane.setMaterial(planeMat);

        plane.setLocalTranslation(x, y, z);

        CollisionShape planeCollisionShape = CollisionShapeFactory.createBoxShape(plane);
        RigidBodyControl rigidBody = new RigidBodyControl(planeCollisionShape, 0f);
        plane.addControl(rigidBody);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(rigidBody);

        return plane;
    }
}
