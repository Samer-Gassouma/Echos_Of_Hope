package mygame.states;

import com.capdevon.engine.SimpleAppState;
import com.jme3.scene.Node;

import mygame.controls.Spawner;
import mygame.prefabs.FoodPrefab;

public class FoodAppState extends SimpleAppState {

    @Override
    protected void simpleInit() {

        Node foods = new Node("FoodSpwaner");
        rootNode.attachChild(foods);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 10;
        spawner.radius = 6;
        spawner.height = 5f;
        spawner.spawnTime = 3f;

        spawner.prefab = new FoodPrefab(app);
        foods.addControl(spawner);
    }

}
