package mygame.player;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;

public class StaminaBar  extends SimpleApplication {
    private BitmapText staminaBar;
    private StaminaSystem staminaSystem;

    @Override
    public void simpleInitApp() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Create a stamina bar text element
        staminaBar = new BitmapText(guiFont, false);
        staminaBar.setSize(guiFont.getCharSet().getRenderedSize());
        staminaBar.setColor(ColorRGBA.Green); // Set the color of the stamina bar
        staminaBar.setText("Stamina: |||||||||||"); // Initial representation of stamina bar
        staminaBar.setLocalTranslation(20, settings.getHeight() - 30, 0); // Position the bar on the screen
        guiNode.attachChild(staminaBar);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Update stamina bar representation based on player's current stamina
        float currentStamina = staminaSystem.getCurrentStamina(); // Replace this with your actual player's stamina value
        updateStaminaBar(currentStamina);
    }

    // Update the stamina bar visual representation
    private void updateStaminaBar(float currentStamina) {
        int maxStamina = 100; // Set your maximum stamina value here
        int barLength = 20; // Set the length of the stamina bar representation
        int filledLength = (int) ((currentStamina / maxStamina) * barLength);

        // Update the stamina bar text with || to represent the stamina level
        String barText = "Stamina: " + "|".repeat(filledLength) + "_".repeat(barLength - filledLength);
        staminaBar.setText(barText);
    }

    // Replace this method with your actual player's stamina retrieval logic
    
}
