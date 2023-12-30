package mygame;

import com.capdevon.engine.Capture;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.Physics;
import com.capdevon.physx.TogglePhysicsDebugState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import mygame.audio.SoundManager;
import mygame.player.PlayerManager;
import mygame.states.CubeAppState;
import mygame.states.FlagAppState;
import mygame.states.FoodAppState;
import mygame.states.MonsterAppState;
import mygame.states.SceneAppState;



public class Main extends SimpleApplication {

    private BitmapText title;
    private BitmapText playButton;
    private BitmapText exitButton;
    private BitmapText infoText;
    private BitmapText loadingText;
    private BitmapText storyText;
    private BitmapText controlsText;
    boolean isLoading = false;
    boolean isPlayClicked = false;
    boolean isStartClicked = false;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(false);
        settings.setTitle("Echoes Of Hope");
        settings.setUseJoysticks(true);
        settings.setResolution(1280, 720);
        settings.setSamples(4);
        settings.setBitsPerPixel(32);

        app.setSettings(settings);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();

    }

    @Override
    public void simpleInitApp() {
//startGame();
        initSplashScreen();
        initTitle();
        initButtons();
        addBtnActions();
        init_info();
        inputManager.setCursorVisible(true);
        flyCam.setDragToRotate(true);
       
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (isLoading) {
            loadingText.setText(" Loading... ");
        }
        
        if(isPlayClicked){
            createInstructions();
        }
        if(isStartClicked){
            hideButtons2();
        }
    }

    private void createInstructions() {
        

        storyText = new BitmapText(guiFont, false);
        storyText.setSize(guiFont.getCharSet().getRenderedSize());
        storyText.setColor(ColorRGBA.White);
        storyText.setText("Story:\n\n" +
        "Amidst the world's turmoil, the conflict between Palestine and Israel persists. You, a Palestinian girl, must navigate this war-torn zone in a quest for survival.\n" +
        "Your primary objective is to gather food to sustain your stamina while fending off enemies to bolster your score.\n" +
        "Endure the chaos for 5 intense minutes to emerge victorious. But take heed, some food items might be poisoned – exercise caution.\n" +
        "Good luck, stay vigilant, and may fortune favor your journey!\n\n" +
        "Note: Some food may be poisoned, so be careful.");




        storyText.setLocalTranslation(100, 500, 0);
        guiNode.attachChild(storyText);

        controlsText = new BitmapText(guiFont, false);
        controlsText.setSize(guiFont.getCharSet().getRenderedSize());
        controlsText.setColor(ColorRGBA.White);
        controlsText.setText("Controls:\n\n" +
                             "use WSAD to move\n" +
                                "use mouse to look around\n" +
                                "use space to jump\n" +
                                "use left shift to run\n" +
                                "use E to aim and charge the rocket \n" +
                                "use left mouse button to shoot\n"+
                                "PRESS K TO START THE GAME\n wait a bit for the game to load after pressing K");

        controlsText.setLocalTranslation(150, 300, 0);
        guiNode.attachChild(controlsText);

    }
    private void initSplashScreen() {
        isLoading = true;

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        loadingText = new BitmapText(guiFont, false);
        loadingText.setSize(guiFont.getCharSet().getRenderedSize());
        loadingText.setText(" Loading..."); 
        loadingText.setLocalTranslation(settings.getWidth()  - loadingText.getLineWidth() - 50, 50, 0);
        guiNode.attachChild(loadingText);


    }

    void LoadingTxt(){
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        loadingText = new BitmapText(guiFont, false);
        loadingText.setSize(guiFont.getCharSet().getRenderedSize());
        loadingText.setText(" Loading..."); 
        loadingText.setLocalTranslation(settings.getWidth() - 250 , settings.getHeight() - 500, 0);
        guiNode.attachChild(loadingText);
    }

     private void initTitle() {
        
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        title = new BitmapText(guiFont, false);
        title.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        title.setText("Echoes of Hope");
        title.setLocalTranslation(settings.getWidth() / 2f - title.getLineWidth() / 2f, settings.getHeight() - 100, 0);
        guiNode.attachChild(title);
    }

    private void initButtons() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        playButton = new BitmapText(guiFont, false);
        playButton.setSize(guiFont.getCharSet().getRenderedSize());
        playButton.setText("Play");
        playButton.setLocalTranslation(settings.getWidth() / 2f - playButton.getLineWidth() / 2f, settings.getHeight() - 250, 0);
        guiNode.attachChild(playButton);

       

        exitButton = new BitmapText(guiFont, false);
        exitButton.setSize(guiFont.getCharSet().getRenderedSize());
        exitButton.setText("Exit");
        exitButton.setLocalTranslation(settings.getWidth() / 2f - exitButton.getLineWidth() / 2f, settings.getHeight() - 350, 0);
        guiNode.attachChild(exitButton);
    }

    void init_info(){
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        infoText = new BitmapText(guiFont, false);
        infoText.setSize(guiFont.getCharSet().getRenderedSize());
        infoText.setText(" Into : \n Click P to Play \n Click X to Exit"); 
        infoText.setLocalTranslation(settings.getWidth() - 250 , settings.getHeight() - 500, 0);
        guiNode.attachChild(infoText);

    }

    private void addBtnActions() {
        inputManager.addMapping("Play", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Exit", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(actionListener, "start");
        inputManager.addListener(actionListener, "Play", "Exit");
    }

    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (!isPressed) {
            if (name.equals("Play")) {
                isLoading = false;
                isPlayClicked = true;
                hideButtons();
            } else if (name.equals("Exit")) {
                stop();
            }
            else if (name.equals("start")) {
                isStartClicked = true;
                startGame();

            }
        }
    };
    
    private void hideButtons() {
        playButton.setCullHint(BitmapText.CullHint.Always);
        exitButton.setCullHint(BitmapText.CullHint.Always);
        infoText.setCullHint(BitmapText.CullHint.Never);
    }
    private void hideButtons2() {
        storyText.setCullHint(BitmapText.CullHint.Always);
        controlsText.setCullHint(BitmapText.CullHint.Always);
        loadingText.setCullHint(BitmapText.CullHint.Always);
        infoText.setCullHint(BitmapText.CullHint.Always);
    }
    private void startGame() {
        guiNode.detachAllChildren(); 
    
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        stateManager.detach(stateManager.getState(SceneAppState.class));
        stateManager.detach(stateManager.getState(CubeAppState.class));
        flyCam.setEnabled(false);
    
        SoundManager.init(assetManager);
    
        PlayerManager playerManager = new PlayerManager();
    
        BulletAppState physics = new BulletAppState();
        physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(Physics.DEFAULT_GRAVITY);
        physics.setDebugEnabled(false);
        isLoading = false;
        stateManager.attach(new SceneAppState());
        stateManager.attach(new CubeAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(playerManager);
        stateManager.attach(new TogglePhysicsDebugState());
        stateManager.attach(new MonsterAppState());
        stateManager.attach(new FoodAppState());
        stateManager.attach(new FlagAppState());
    
    }
    
 

}