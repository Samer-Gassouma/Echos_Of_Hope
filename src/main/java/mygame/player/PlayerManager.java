package mygame.player;

import java.util.ArrayList;
import java.util.Random;

import com.capdevon.anim.Animator;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.capdevon.util.LineRenderer;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import mygame.AudioLib;
import mygame.audio.SoundManager;
import mygame.camera.BPCameraCollider;
import mygame.prefabs.ArrowPrefab;
import mygame.prefabs.ExplosionPrefab;
import mygame.prefabs.ExplosiveArrowPrefab;
import mygame.prefabs.FoodPrefab;
import mygame.weapon.CrosshairData;
import mygame.weapon.FireWeapon;
import mygame.weapon.RangedBullet;
import mygame.weapon.RangedWeapon;
import mygame.weapon.Weapon;
import mygame.weapon.Weapon.WeaponType;

import mygame.states.FlagAppState;
import mygame.prefabs.MyFlagPrefab;


public class PlayerManager extends SimpleAppState {


    private float elapsedTime_pev = 0f;
    private float gameDuration = 300f; 
    private boolean gameEnded = false;

    private Node player;
    private StaminaSystem staminaSystem;

    private PlayerInput playerInput;
    private PlayerControl playerControl;
    private int health = 100;
    private BitmapText healthText;

    private BitmapText gameOverText;
    private boolean initialized = false;

    private int score = 0;
    private BitmapText scoreText;

    private BitmapText staminaBarText;
    private BitmapText RetryBtn;

    private MyFlagPrefab flagPrefab;

    private FoodPrefab food;

    private Random random = new Random();


    public enum FoodEffect {
        HEALTH_BOOST,
        SPEED_UP,
        POISON,
    }


    private boolean isSpeedBoostActive = false;
    private float initialMoveSpeed;
    private float initialRunSpeed;
    private float speedBoostDuration = 10f;
    private float elapsedTime = 0f;
    boolean isDead = false;
    BitmapText timerText;

    public PlayerManager() {
        
    }

    public Node getPlayer() {
        if (player == null) {
            player = new Node("Player");
            initPlayer(player);
        }
        return player;
    }

    void initPlayer(Node player) {
        this.player = player;
    }

    public void setPlayer(Node player) {
        this.player = player;
       
    }

  

    void speedUp() {
        if (!isSpeedBoostActive && elapsedTime >= speedBoostDuration) {
            playerControl = player.getControl(PlayerControl.class);
            
            initialMoveSpeed = playerControl.moveSpeed;
            initialRunSpeed = playerControl.runSpeed;
            
            playerControl.moveSpeed += 1f;
            playerControl.runSpeed += 3f;
            
            isSpeedBoostActive = true;
            elapsedTime = 0f; 
        }
    }
    
    @Override
    protected void simpleInit() {
        this.flagPrefab = new MyFlagPrefab(app);
        this.food = new FoodPrefab(app);
        staminaSystem = new StaminaSystem(100);
        setupPlayer();
        registerInput();
        StaminaBar_init();
        
        healthText = createUIText(20, settings.getHeight() - 40, ColorRGBA.Blue);
        
        scoreText = createUIText(settings.getWidth() - 100, settings.getHeight() - 20, ColorRGBA.Red); 
        timerText = createUIText(settings.getWidth() /2, settings.getHeight() - 20, ColorRGBA.White); 

        initialized = true;

        updateScoreText();
        updateHealthText();


        getPhysicsSpace().addCollisionListener((PhysicsCollisionListener) new PhysicsCollisionListener() {
            @Override
            public void collision(PhysicsCollisionEvent event) {
                if ("ps_flag".equals(event.getNodeA().getName()) || "ps_flag".equals(event.getNodeB().getName())) {
                    Spatial flag = "ps_flag".equals(event.getNodeA().getName()) ? event.getNodeA() : event.getNodeB();
                    collectFlag(flag);
                    flagPrefab.DestoryFlag(flag);
                }
                if ("enemy".equals(event.getNodeA().getName()) || "enemy".equals(event.getNodeB().getName())) {
                    Spatial enemy = "enemy".equals(event.getNodeA().getName()) ? event.getNodeA() : event.getNodeB();
                    kill_enemy();
                }
                String FoodNames[] = { "BREAD", "CUPCAKE", "PIZZA", "MUSHROOM_POISON"}; 

                for (String name : FoodNames) {
                    if (name.equals(event.getNodeA().getName()) || name.equals(event.getNodeB().getName())) {
                        Spatial food = name.equals(event.getNodeA().getName()) ? event.getNodeA() : event.getNodeB();
                        PhysicsSpace.getPhysicsSpace().removeAll(food);
                        food.removeFromParent();                        
                        increaseScore(10);
                        staminaSystem.increaseStamina(25);
                        System.out.println("Food Type Interacted with : " + name);


                        FoodEffect effect = getRandomFoodEffect(); 

                        switch (effect) {
                            case HEALTH_BOOST:
                                health += getHealthBoostValue(name);
                                break;
                            case SPEED_UP:
                                speedUp();
                                break;
                            case POISON:
                                health -= getPoisonValue(name);
                                break;
                            
                        }

                       
                        
                    }

                }
            }
        });
    }

    private FoodEffect getRandomFoodEffect() {
        FoodEffect[] effects = FoodEffect.values();
        return effects[random.nextInt(effects.length)];
    }

    private int getHealthBoostValue(String foodName) {
        switch (foodName) {
            case "BREAD":
                return 10;
            case "PIZZA":
                return 30;
            default:
                return 0;
        }
    }

    private int getPoisonValue(String foodName) {
        return foodName.equals("MUSHROOM_POISON") ? 10 : 0;
    }

    private void registerInput() {
        GInputAppState ginput = getState(GInputAppState.class);
        ginput.addActionListener(playerInput);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (!initialized) {
            return;
        }
        elapsedTime_pev += tpf;

        updateTimerText();

        if (elapsedTime_pev >= gameDuration) {
            endGame();
        }
        if (isSpeedBoostActive) {
            elapsedTime += tpf;
            
 
            if (elapsedTime >= speedBoostDuration) {
                playerControl.moveSpeed = initialMoveSpeed;
                playerControl.runSpeed = initialRunSpeed;
                
                isSpeedBoostActive = false;
            }
        }
        app.getListener().setLocation(player.getWorldTranslation());

       
        if (isWalking()) {
            if(staminaSystem.getCurrentStamina() > 0){
                staminaSystem.decreaseStamina(1 * tpf); 
            }else{
                health -= 0.1 * tpf;
            }
             
        }
        if (isRunning()) {
            if(staminaSystem.getCurrentStamina() > 0){
                staminaSystem.decreaseStamina(2 * tpf); 
            }else{
                health -= 0.2 * tpf;
            }
            
        }
        
        
        if (health == 0) {
            gameOver();
            isDead = true;
        }
       

        updateStaminaBar(); 
        updateHealthText();
        updateScoreText();
    }
    private void updateTimerText() {
        int minutes = (int) (gameDuration - elapsedTime_pev) / 60;
        int seconds = (int) (gameDuration - elapsedTime_pev) % 60;
        String timerString = String.format("Time Left: %02d:%02d", minutes, seconds);

        timerText.setText(timerString);
    }
    private void endGame() {
        gameEnded = true;
        gameOverText = createUIText(settings.getWidth() / 2, settings.getHeight() / 2, ColorRGBA.Red);
        gameOverText.setText("Game Over");
        gameOverText.setLocalTranslation(gameOverText.getLocalTranslation().add(-gameOverText.getLineWidth() / 2, gameOverText.getLineHeight() / 2, 0));
        unregisterInput();
        initialized = false;
        
        if (isDead){
            gameOverText.setText("You Lost");
            
           
    

        }else{
            gameOverText.setText("You Won");
        } 
      BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

            RetryBtn = new BitmapText(guiFont, false);
            RetryBtn.setSize(guiFont.getCharSet().getRenderedSize());
            RetryBtn.setText("Retry");
            RetryBtn.setLocalTranslation(settings.getWidth() / 2f - RetryBtn.getLineWidth() / 2f, settings.getHeight() - 250, 0);
            guiNode.attachChild(RetryBtn);

            inputManager.addMapping("Retry", new KeyTrigger(KeyInput.KEY_P));
            inputManager.addListener(actionListener, "Retry");
    }

       
    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (!isPressed) {
            if (name.equals("Retry")) {

                hideButtons();
                respawnPlayer();
            }
        }
    };
    
    private void hideButtons() {
        RetryBtn.setCullHint(BitmapText.CullHint.Always);
        
    }
    private boolean isWalking() {

        if (playerInput.isWalking()) {
            return true;
        }
        return false;
    }

    private boolean isRunning() {
        if (playerInput.isRunning()) {
            return true;
        }
        return false;
    }

    private void updateStaminaBar() {
        // Update UI to visualize stamina bar
        float currentStamina = staminaSystem.getCurrentStamina();
        float maxStamina = staminaSystem.getMaxStamina();
        int barLength = 20;
        int filledLength = (int) ((currentStamina / maxStamina) * barLength);

        String barText = "Stamina (" + staminaSystem.getCurrentStamina() +"): " + "|".repeat(filledLength) + "_".repeat(barLength - filledLength);
        staminaBarText.setText(barText);
    }

    public Vector3f getPlayerPosition() {
        return player.getWorldTranslation();
    }

    private void updateHealthText() {
        healthText.setText("Health: " + health);
    }
    private void updateScoreText() {
        if (scoreText != null) {
            scoreText.setText("Score: " + score); // Set the text for scoreText
        }
    }

    public void increaseScore(int value) {
        score += value;
        scoreText.setText("Score: " + score);
    }

    private void collectFlag(Spatial flag) {
        // Remove the flag from the scene
        flag.removeFromParent();

        // Increase the player's score by a certain value (e.g., 10)
        increaseScore(10);
    }


     void StaminaBar_init(){
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        staminaBarText = new BitmapText(guiFont, false);
        staminaBarText.setSize(guiFont.getCharSet().getRenderedSize());
        staminaBarText.setColor(ColorRGBA.Green);
        staminaBarText.setText("Stamina(" + staminaSystem.getCurrentStamina() +") : |||||||||||");
        staminaBarText.setLocalTranslation(settings.getWidth() - 200, settings.getHeight() - 40, 0);
        guiNode.attachChild(staminaBarText);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    void respawnPlayer() {
        player.setLocalTranslation(0, 0, 0);
        player.setLocalTransform(player.getLocalTransform());
        staminaSystem = new StaminaSystem(100);
        health = 100;
        updateHealthText();
        registerInput();
        gameOverText.setText("Loading...");
        gameOverText.setText("Respawning in 3");
                gameOverText.setLocalTranslation(gameOverText.getLocalTranslation().add(-gameOverText.getLineWidth() / 2, gameOverText.getLineHeight() / 2, 0));

        app.enqueue(() -> {
            gameOverText.setText("Respawning in 2");
            return null;
        });
        app.enqueue(() -> {
            gameOverText.setText("Respawning in 1");
            return null;
        });
        app.enqueue(() -> {
            gameOverText.setText("");
            return null;
        });
        //wait 3 seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initialized = true;
        gameOverText.removeFromParent();
    }

    void kill_enemy() {
        score += 20;
        updateScoreText();
    }

    private void gameOver() {
        gameOverText = createUIText(settings.getWidth() / 2, settings.getHeight() / 2, ColorRGBA.Red);
        gameOverText.setText("Game Over");
        gameOverText.setLocalTranslation(gameOverText.getLocalTranslation().add(-gameOverText.getLineWidth() / 2, gameOverText.getLineHeight() / 2, 0));
        unregisterInput();
        initialized = false;
    }

    private void unregisterInput() {
        GInputAppState ginput = getState(GInputAppState.class);
        ginput.removeActionListener(playerInput);
    }


    private void setupPlayer() {
        //player = (Node) assetManager.loadModel("Models/Archer/Erika.j3o");
        player = (Node) assetManager.loadModel("Models/player/girl.j3o");

        player.setName("Player");

        // add Physics & Animation Control
        player.addControl(new Animator());
        player.addControl(new BetterCharacterControl(.4f, 1.8f, 80f));
        getPhysicsSpace().add(player);
        rootNode.attachChild(player);

        BPCameraCollider bpCamera = new BPCameraCollider(camera, inputManager);
        bpCamera.setXOffset(-0.5f);
        bpCamera.setYHeight(1.8f);
        bpCamera.setMinDistance(1f);
        bpCamera.setMaxDistance(3f);
        bpCamera.setMinVerticalRotation(-FastMath.DEG_TO_RAD * (20));
        bpCamera.setMaxVerticalRotation(FastMath.DEG_TO_RAD * (30));
        bpCamera.setRotationSpeed(1f);
        bpCamera.setIgnoreTag("TagPlayer");
        player.addControl(bpCamera);

      WeaponUIManager m_WeaponUIManager = new WeaponUIManager();
        m_WeaponUIManager.weaponText = createUIText(20, settings.getHeight() - 20, ColorRGBA.Red);
        player.addControl(m_WeaponUIManager);

        LineRenderer lr = new LineRenderer(app);
        lr.setLineWidth(3f);
        player.addControl(lr);

        PlayerWeaponManager m_PlayerWeaponManager = new PlayerWeaponManager();
        m_PlayerWeaponManager.assetManager = assetManager;
        m_PlayerWeaponManager.camera = camera;
        m_PlayerWeaponManager.addWeapon(createRangedWeapon());
        m_PlayerWeaponManager.addWeapon(createFireWeapon());
        m_PlayerWeaponManager.shootSFX = SoundManager.createAudioBuffer(AudioLib.ARROW_HIT);
        m_PlayerWeaponManager.reloadSFX = SoundManager.createAudioBuffer(AudioLib.BOW_PULL);
        player.addControl(m_PlayerWeaponManager);

        PlayerControl m_PlayerControl = new PlayerControl();
        m_PlayerControl.camera = camera;
        m_PlayerControl.footstepsSFX = SoundManager.createAudioBuffer(AudioLib.GRASS_FOOTSTEPS);
        player.addControl(m_PlayerControl);

        playerInput = new PlayerInput();
        player.addControl(playerInput);
      
    }

    private Weapon createFireWeapon() {
        FireWeapon fWeapon = new FireWeapon();
        fWeapon.name = "SniperRifle";
        fWeapon.weaponType = WeaponType.Normal;
        fWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("+"));
        
        Spatial rifle = createFakeRifleModel();
        rifle.addControl(fWeapon);
        
        return fWeapon;
    }

    private Weapon createRangedWeapon() {
        RangedWeapon rWeapon = new RangedWeapon();
        rWeapon.name = "Bow";
        rWeapon.weaponType = WeaponType.Bow;
        rWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("-.-"));
        Spatial bow = createFakeBowModel();
        bow.addControl(rWeapon);

        RangedBullet[] bullets = new RangedBullet[3];
        
        // 1.
        ExplosionPrefab eFlame = new ExplosionPrefab(app);
        eFlame.assetName = "Scenes/jMonkey/Flame.j3o";
        eFlame.explosionColor = ColorRGBA.Orange.clone();
        eFlame.lifeTimeVFX = 1.05f;
        
        ExplosiveArrowPrefab fArrow = new ExplosiveArrowPrefab(app);
        fArrow.name = "FlameArrow";
        fArrow.mass = 6f;
        fArrow.explosionPrefab = eFlame;
        bullets[0] = fArrow;

        // 2.
        ExplosionPrefab ePoison = new ExplosionPrefab(app);
        ePoison.assetName = "Scenes/jMonkey/Poison.j3o";
        ePoison.explosionColor = new ColorRGBA(0, 1.0f, 0.452f, 1f);
        ePoison.lifeTimeVFX = 8.85f;
        
        ExplosiveArrowPrefab pArrow = new ExplosiveArrowPrefab(app);
        pArrow.name = "PoisonArrow";
        pArrow.mass = 6f;
        pArrow.explosionPrefab = ePoison;
        bullets[1] = pArrow;
        
        // 3.
        ArrowPrefab arrow = new ArrowPrefab(app);
        arrow.mass = 6f;
        arrow.name = "Arrow";
        bullets[2] = arrow;
        
        // set arrows
        rWeapon.setBullets(bullets);

        return rWeapon;
    }

    private Node createFakeRifleModel() {
        Node model = new Node("Rifle");
        Geometry geo = makeGeometry("Weapon.GeoMesh", new Sphere(8, 8, .05f), ColorRGBA.Red);
        model.setCullHint(Spatial.CullHint.Never);
        model.attachChild(geo);

        return model;
    }

    private Node createFakeBowModel() {
        Node model = new Node("ArcherToolkit");
//        Geometry bow = createGeometry("Bow.GeoMesh", new Sphere(8, 8, .05f), ColorRGBA.Red);
//        Geometry arrow = createGeometry("Arrow", new Sphere(8, 8, .05f), ColorRGBA.Green);
//        Geometry quiver = createGeometry("Quiver", new Sphere(8, 8, .05f), ColorRGBA.Green);
//        model.setCullHint(Spatial.CullHint.Never);
//        model.attachChild(bow);
//        model.attachChild(arrow);
//        model.attachChild(quiver);

        return model;
    }
    
    private Geometry makeGeometry(String name, Mesh mesh, ColorRGBA color) {
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        return geo;
    }
    
    private BitmapText createUIText(float xPos, float yPos, ColorRGBA color) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText hud = new BitmapText(font);
        hud.setSize(font.getCharSet().getRenderedSize());
        hud.setLocalTranslation(xPos, yPos, 0);
        hud.setColor(color);
        guiNode.attachChild(hud);
        return hud;
    }

    /* A centered plus sign to help the player aim. */
    private BitmapText getCrossHair(String text) {
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText(text);
        float width = settings.getWidth() / 2 - ch.getLineWidth() / 2;
        float height = settings.getHeight() / 2 + ch.getLineHeight() / 2;
        ch.setLocalTranslation(width, height, 0);
        return ch;
    }

    public class player {
    }
}
