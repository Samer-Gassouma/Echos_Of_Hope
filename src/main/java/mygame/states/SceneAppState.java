package mygame.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.shaderblowex.filter.MipmapBloomFilter;
import com.capdevon.engine.SimpleAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;



public class SceneAppState extends SimpleAppState {

    private DirectionalLight sun;
    private FilterPostProcessor fpp;

    private boolean generateLightProbe = true;

 


    @Override
    public void simpleInit() {
        setupSkyBox();
        setupScene();
        setupLights();
        setupFilters();
    }

    
    private void setupSkyBox() {
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupScene() {
        Spatial scene = assetManager.loadModel("Scenes/plan.gltf");
        scene.setName("Floor");
        scene.move(0, 0, 0);
        scene.scale(10f);
        CollisionShape shape = CollisionShapeFactory.createMeshShape(scene);
        RigidBodyControl rgb = new RigidBodyControl(shape, 0f);
        scene.addControl(rgb);
        PhysicsSpace.getPhysicsSpace().add(rgb);
        rootNode.attachChild(scene);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);


        Spatial Buildings = assetManager.loadModel("Scenes/destroyed_buildings.glb");
        Buildings.setName("Buildings");
        Buildings.move(5, 0, 0);
        Buildings.scale(2f);
        CollisionShape shape2 = CollisionShapeFactory.createMeshShape(Buildings);
        RigidBodyControl rgb2 = new RigidBodyControl(shape2, 0f);
        Buildings.addControl(rgb2);
        PhysicsSpace.getPhysicsSpace().add(rgb2);
        rootNode.attachChild(Buildings);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);

        Spatial ps_flag = assetManager.loadModel("Models/palestine_flag.glb");
        ps_flag.setName("ps_flag");
        ps_flag.scale(10f);
        ps_flag.move(0, 0, 0);
        CollisionShape shape3 = CollisionShapeFactory.createMeshShape(ps_flag);
        RigidBodyControl rgb3 = new RigidBodyControl(shape3, 0f);
        ps_flag.addControl(rgb3);
        getPhysicsSpace().add(rgb3);
        rootNode.attachChild(ps_flag);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);
       

        /* nature sound - keeps playing in a loop. */
       /*  AudioNode audio = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", AudioData.DataType.Stream);
        audio.setLooping(true);
        audio.setPositional(false);
        audio.setVolume(2);
        rootNode.attachChild(audio);
        audio.play();
        */
    }

    private void setupLights() {
        sun = new DirectionalLight();
        sun.setName("SunLight");
        sun.setDirection(new Vector3f(-4.9236743f, -1.27054665f, 5.896916f));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        if (generateLightProbe) {
            EnvironmentCamera envCam = new EnvironmentCamera(); // Make an env camera
            stateManager.attach(envCam);
            envCam.initialize(stateManager, app); // Manually initialize so we can add a probe before the next update happens
            LightProbe probe = LightProbeFactory.makeProbe(envCam, rootNode);
            probe.getArea().setRadius(10); // Set the probe's radius in world units
            rootNode.addLight(probe);
            
        } else {
            // add a PBR probe.
            Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
            LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
            lightProbe.getArea().setRadius(100);
            rootNode.addLight(lightProbe);
        }
    }

    private void setupFilters() {
        // Shadows
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
//        dlsf.setLight(sun);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        LightScatteringFilter lsf = new LightScatteringFilter(sun.getDirection().mult(-300));
        lsf.setLightDensity(0.5f);

//        BloomFilter bloom = new BloomFilter();
//        bloom.setExposurePower(55);
//        bloom.setBloomIntensity(1.0f);
                
        MipmapBloomFilter bloom = new MipmapBloomFilter(MipmapBloomFilter.Quality.High, MipmapBloomFilter.GlowMode.Scene);
        bloom.setExposurePower(0.7f);
        bloom.setBloomIntensity(0.4f, 0.55f);

        SSAOFilter ssao = new SSAOFilter(5f, 10f, 0.8f, 0.70f);

        FXAAFilter fxaa = new FXAAFilter();

        fpp = new FilterPostProcessor(assetManager);
//        int numSamples = settings.getSamples();
//        if (numSamples > 0) {
//            fpp.setNumSamples(numSamples);
//        }
//
//        fpp.addFilter(dlsf);
        fpp.addFilter(ssao);
        fpp.addFilter(bloom);
        fpp.addFilter(lsf);
        fpp.addFilter(fxaa);
        viewPort.addProcessor(fpp);
    }


  

}
