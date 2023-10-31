import processing.core.*;

import java.util.ArrayList;

public class ContagionSimulation extends PApplet {

    public static final int ballAmount = 50;
    public float diameter = 20;
    public static ArrayList<Ball> balls;
    public static ArrayList<Integer> infectionData = new ArrayList<>();
    public static ArrayList<Integer> immunityData = new ArrayList<>();
    public static ArrayList<Integer> neutralData = new ArrayList<>();
    public float spreadChance = 75;
    public float spreadChanceImmune = 30;
    public float immuneChance = 65;
    public float immuneChanceSpread = 15;
    public static int infected;
    public static int immune;
    public static int neutral;
    public static DataStore dataStore = new DataStore();
    double start;
    boolean runtimeCheck = true;

    public void settings() {
        size(600, 600);
    }

    public void setup() {
        String[] graphArgs = {"GraphingWindow"};
        SecondApplet graphingWindow = new SecondApplet();
        PApplet.runSketch(graphArgs, graphingWindow);

        String[] graphArgs2 = {"ImmuneGraphingWindow"};
        ThirdApplet immuneGraphingWindow = new ThirdApplet();
        PApplet.runSketch(graphArgs2, immuneGraphingWindow);

        String[] graphArgs3 = {"NeutralGraphingWindow"};
        FourthApplet neutralGraphingWindow = new FourthApplet();
        PApplet.runSketch(graphArgs3, neutralGraphingWindow);

        background(255);
        balls = new ArrayList<>();
        initBalls();
    }

    public void draw() {
        if(runtimeCheck == true) {
            start = System.currentTimeMillis();
            runtimeCheck = false;
        }
        background(255);
        for (Ball ball : balls) {
            ball.move();
            ball.generateGraphics();
        }

        if(checkVictory() != null) {
            long winKey = dataStore.findNextWinKey();
            long timeKey = dataStore.findNextTimeKey();
            double runtime = (System.currentTimeMillis() - start)/1000;
//            System.out.println(winKey);
//            System.out.println(timeKey);
            dataStore.putWin(winKey, checkVictory());
            dataStore.putTime(timeKey, runtime);
            outputData();
//            System.out.println(dataStore.getTime(timeKey));
//            System.out.println(dataStore.getWin(winKey));
            exit();
        }
    }


    void initBalls() {
        for (int i = 0; i < ballAmount; i++) {
            PVector pos;
            boolean overlap;

            do {
                overlap = false;
                pos = new PVector(random((diameter / 2) + 1, width - ((diameter / 2) + 1)), random((diameter / 2) + 1, height - ((diameter / 2) + 1)));

                for (Ball existingBall : balls) {
                    float distance = dist(pos.x, pos.y, existingBall.pos.x, existingBall.pos.y);
                    if (distance < (diameter / 2 + existingBall.diam / 2)) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);

            PVector vel = PVector.random2D().mult(2);

            if (i == 0) {
                balls.add(new Ball(pos, vel, diameter, diameter, true, false));
            } else if (i == 1) {
                balls.add(new Ball(pos, vel, diameter, diameter, false, true));
            } else {
                balls.add(new Ball(pos, vel, diameter, diameter, false, false));
            }

        }
    }

    static String checkVictory() {
        int immuneCounter = 0;
        int infectedCounter = 0;
        for(Ball ball : balls) {
            if(ball.immunity == true) {
                immuneCounter++;
            } else if (ball.status == true) {
                infectedCounter++;
            }
        }

        if(immuneCounter == ballAmount) {
            return "Immune";
        } else if (infectedCounter == ballAmount) {
            return "Infected";
        }

        return null;
    }

    void outputData() {
        System.out.println("The average runtime of this simulation is: " + dataStore.averageRuntime() + " seconds.");
        System.out.println("Immune ball win percentage: " + dataStore.immuneWinPercentage() + "%");
        System.out.println("Infected ball win percentage: " + dataStore.infectedWinPercentage() + "%");
    }

    class Ball {
        PVector pos;
        PVector vel;
        float diam;
        boolean status;
        boolean immunity;

        Ball(PVector pos, PVector vel, float diam, float diameter, boolean status, boolean immunity) {
            this.pos = pos;
            this.vel = vel;
            this.diam = diam;
            this.status = status;
            this.immunity = immunity;
        }

        void move() {
            pos.add(vel);

            if (pos.x - (diam / 2) < 0) {
                pos.x = diam / 2;
                vel.x *= -1;
            } else if (pos.x + (diam / 2) > width) {
                pos.x = width - diam / 2;
                vel.x *= -1;
            }

            if (pos.y - (diam / 2) < 0) {
                pos.y = diam / 2;
                vel.y *= -1;
            } else if (pos.y + (diam / 2) > height) {
                pos.y = height - diam / 2;
                vel.y *= -1;
            }

            for (Ball otherBall : balls) {
                if (otherBall != this && collisionChecker(otherBall)) {
                    float overlap = (diam / 2 + otherBall.diam / 2) - dist(pos.x, pos.y, otherBall.pos.x, otherBall.pos.y);
                    PVector unstuck = PVector.sub(pos, otherBall.pos).setMag(overlap / 2);
                    pos.add(unstuck);
                    otherBall.pos.sub(unstuck);

                    PVector saver = vel;
                    vel = otherBall.vel;
                    otherBall.vel = saver;

                    if (spread() && otherBall.status && !this.status && this.immunity == false) {
                        status = true;
                    } else if (spreadToImmune() && otherBall.status && this.status == false && this.immunity == true) {
                        status = false;
                        immunity = false;
                    } else if (spreadImmune() && otherBall.status == false && this.status == false && this.immunity == false && otherBall.immunity == true) {
                        immunity = true;
                    } else if (spreadToInfected() && otherBall.status == false && this.status && this.immunity == false && otherBall.immunity == true) {
                        immunity = false;
                        status = false;
                    }
                }
            }
        }


        void generateGraphics() {
            if (status) {
                fill(238, 75, 43);
            } else if (immunity) {
                fill(50,205,50);
            } else {
                fill(30, 144, 255);
            }
            ellipse(pos.x, pos.y, diam, diam);
        }

        boolean spread() {
            if (random(100) < spreadChance) {
                return true;
            } else {
                return false;
            }
        }

        boolean spreadToImmune() {
            if(random(100) < spreadChanceImmune) {
                return true;
            } else {
                return false;
            }
        }

        boolean spreadImmune() {
            if(random(100) < immuneChance) {
                return true;
            } else {
                return false;
            }
        }

        boolean spreadToInfected() {
            if(random(100) < immuneChanceSpread) {
                return true;
            } else {
                return false;
            }
        }

        boolean collisionChecker(Ball otherBall) {
            float distance = dist(pos.x, pos.y, otherBall.pos.x, otherBall.pos.y);
            return distance < (diam / 2 + otherBall.diam / 2);
        }
    }


    public static void main(String[] args) {
        PApplet.main("ContagionSimulation", args);
//        dataStore.wipeData();
        dataStore.printDataStore();
    }
}
