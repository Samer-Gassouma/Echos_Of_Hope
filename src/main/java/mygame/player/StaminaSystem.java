package mygame.player;

import com.jme3.math.FastMath;

public class StaminaSystem {
    private float maxStamina;
    private float currentStamina;
    
    public StaminaSystem(float maxStamina) {
        this.maxStamina = maxStamina;
        this.currentStamina = maxStamina;
    }

    public void decreaseStamina(float amount) {
        currentStamina -= amount;
        currentStamina = FastMath.clamp(currentStamina, 0, maxStamina);

    }

    public void increaseStamina(float amount) {
        currentStamina += amount;
        currentStamina = FastMath.clamp(currentStamina, 0, maxStamina);
    }

    public float getCurrentStamina() {
        return currentStamina;
    }

    public float getMaxStamina() {
        return maxStamina;
    }
}
