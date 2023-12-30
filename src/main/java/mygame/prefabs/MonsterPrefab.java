package mygame.prefabs;

//import com.capdevon.anim.Animator;
import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

import mygame.ai.AIControl;
import mygame.weapon.Damageable;

public class MonsterPrefab extends PrefabComponent {

    public boolean usePhysics = true ;
    public float radius = 0.4f;
    public float height = 1.6f;

    private PhysicsSpace phySpace;

    public MonsterPrefab(Application app) {
        super(app);
        this.phySpace = getPhysicsSpace();
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {

        Node enemy = (Node) assetManager.loadModel("Models/fake_g-toilet.glb"); // WIP...
        enemy.setName("Monster-" + nextSeqId());
        enemy.setLocalTranslation(position);
        enemy.setLocalRotation(rotation);
       // enemy.addControl(new Animator());


        if (usePhysics) {
            BetterCharacterControl bcc = new BetterCharacterControl(radius, height, 10f);
            enemy.addControl(bcc);
            phySpace.add(bcc);
              
            /*bcc.getRigidBody().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
            bcc.setPhysicsDamping(1f);*/
            
            CollisionShape shape = CollisionShapeFactory.createMeshShape(enemy);
            RigidBodyControl rigidBody = new RigidBodyControl(shape, 0);
            enemy.addControl(rigidBody);
            PhysicsSpace.getPhysicsSpace().add(rigidBody);

        } else {
            RigidBodyControl rbc = createRigidBody(radius, height);
            enemy.addControl(rbc);
            phySpace.add(rbc);
            rbc.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
            //rbc.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        }

        BitmapText hud = createBitmapText(ColorRGBA.White, "label-placeholder", .1f);
        hud.setName("Healthbar");
        hud.setLocalTranslation(0, 2f, 0);
        enemy.attachChild(hud);

        AIControl aiControl = new AIControl();
        aiControl.hud = hud;
        aiControl.player = getRootNode().getChild("Player");
        enemy.addControl(aiControl);

        Damageable m_Damageable = new Damageable();
        enemy.addControl(m_Damageable);

        app.enqueue(() -> parent.attachChild(enemy));

        return enemy;
    }

    private BitmapText createBitmapText(ColorRGBA color, String text, float size) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setColor(color);
        bmp.setText(text);
        bmp.setBox(new Rectangle((-bmp.getLineWidth() / 2) * bmp.getSize(), 0f, bmp.getLineWidth() * bmp.getSize(), bmp.getLineHeight()));
        bmp.setAlignment(BitmapFont.Align.Center);
        bmp.setShadowMode(ShadowMode.Off);
        bmp.setQueueBucket(RenderQueue.Bucket.Transparent);
        bmp.setSize(size);
        bmp.addControl(new BillboardControl());
        return bmp;
    }

    private RigidBodyControl createRigidBody(float radius, float height) {

        CapsuleCollisionShape capsule = new CapsuleCollisionShape(radius, (height - (2 * radius)));
        CompoundCollisionShape collShape = new CompoundCollisionShape();
        Vector3f position = new Vector3f(0, (height / 2f), 0);
        collShape.addChildShape(capsule, position);

        // Setup root motion physics control
        RigidBodyControl rbc = new RigidBodyControl(collShape);
        rbc.setKinematic(true);
        rbc.setKinematicSpatial(true);

        return rbc;
    }

}
