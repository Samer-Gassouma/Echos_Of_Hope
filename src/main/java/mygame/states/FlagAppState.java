package mygame.states;



import com.capdevon.engine.SimpleAppState;
import com.jme3.scene.Node;

import mygame.controls.Spawner;
import mygame.prefabs.MyFlagPrefab;


public class FlagAppState extends SimpleAppState  {
    
    @Override
    protected void simpleInit() {

        Node flags = new Node("FlagSpwaner");
        rootNode.attachChild(flags);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 10;
        spawner.radius = 20;
        spawner.height = 5f;
        spawner.spawnTime = 3f;
        spawner.prefab = new MyFlagPrefab(app);
        flags.addControl(spawner);
    }

    public void DestoryFlag(Node flag) {
        flag.removeFromParent();
        
    }
}
