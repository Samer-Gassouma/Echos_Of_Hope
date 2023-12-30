package mygame.prefabs;

import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class MyFlagPrefab extends PrefabComponent{
    
    public float size = 0.5f;
    public float mass = 30f;

    public MyFlagPrefab(Application app) {
        super(app);
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
  

        Node flag = new Node("flag" + nextSeqId());
        Spatial ps_flag = assetManager.loadModel("Models/palestine_flag.glb");
        ps_flag.setName("ps_flag");
        ps_flag.scale(10f);
        CollisionShape shape3 = CollisionShapeFactory.createMeshShape(ps_flag);
        RigidBodyControl rgb3 = new RigidBodyControl(shape3, 0f);
        ps_flag.addControl(rgb3);
        getPhysicsSpace().add(rgb3);
        flag.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        flag.setQueueBucket(RenderQueue.Bucket.Opaque);
        flag.attachChild(ps_flag);
        position.y = 0;

        flag.setLocalTranslation(position);
        flag.setLocalRotation(rotation);

        flag.move(position.x, 0, position.z);
        parent.attachChild(flag);

    
 

        return flag;
    }

    public void DestoryFlag(Spatial flag) {
        flag.removeFromParent();
        
    }
}
