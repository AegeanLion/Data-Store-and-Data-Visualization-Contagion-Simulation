import processing.core.PApplet;
import processing.core.PFont;

public class FourthApplet extends PApplet {
    int interval = 1000;
    int lastUpdateTime = 0;

    public void settings() {
        size(500, 250);
    }

    public void setup() {
        PFont font = createFont("Arial", 7);
        textFont(font);

        background(255);
    }

    public void draw() {
        background(255);
        drawGrid();
        writeText();
        updater();
        threadYielder();
        graphGenerator();
    }

    void graphGenerator() {
        stroke(0, 0, 255);
        noFill();
        beginShape();

        for (int i = 0; i < ContagionSimulation.neutralData.size(); i++) {
            float x = map(i, 0, ContagionSimulation.neutralData.size() - 1, 0, width);
            float y = map(ContagionSimulation.neutralData.get(i), 0, ContagionSimulation.ballAmount, height, 0);
            vertex(x, y);
        }

        endShape();
    }

    void drawGrid() {
        stroke(0);
        for(int i = 1; i < 10; i++) {
            int y = i * (height)/5;
            line(0, y, width, y);
        }

        for(int i = 1; i < 10; i++) {
            int x = i * (width)/10;
            line(x, 0, x, height);
        }
    }

    int countNeutralBalls() {
        int counter = 0;
        for(ContagionSimulation.Ball ball: ContagionSimulation.balls) {
            if (ball.immunity == false && ball.status == false) {
                counter++;
            }
        }
        return counter;
    }

    void threadYielder() {
        while(ContagionSimulation.infected == ContagionSimulation.ballAmount || ContagionSimulation.immune == ContagionSimulation.ballAmount) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void updater() {
        if (millis() - lastUpdateTime >= interval) {
            ContagionSimulation.neutral = countNeutralBalls();
            ContagionSimulation.neutralData.add(ContagionSimulation.neutral);
            lastUpdateTime = millis();
        }
    }

    void writeText() {
        stroke(0, 0, 255);
        fill(0, 0, 255);
        textSize(10);
        text("Neutral Count ^/V", 10, 10);
        text("<-Time->", 250, height - 10);
    }
}
