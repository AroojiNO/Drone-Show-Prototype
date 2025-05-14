import java.awt.Color;

public class Dot {
    float  alpha, startAlpha, targetAlpha; 

    double x, y, startX, startY, targetX, targetY;
    int delay;
    Color color, startColor, targetColor;

    Dot(double sx, double sy, double tx, double ty, Color initialColor) {
        this.x = sx; this.y = sy;
        this.startX = sx; this.startY = sy;
        this.targetX = tx; this.targetY = ty;
        this.delay = 0;
        this.color = initialColor;
        this.startColor = initialColor;
        this.targetColor = initialColor;
        this.alpha = 0f;
        this.startAlpha = 0f;
        this.targetAlpha = 1f;
    }
}