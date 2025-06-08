package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.internal.AocRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * !!! EPILEPSY WARNING !!!
 * As seen on my short <a href="https://www.youtube.com/shorts/yHmSLlnexLM">Youtube Video</a>
 */
public class Day6Animation extends JPanel {

    public static void main(String[] args) {
        AocRunner.run(Day6Animated.class);
    }

    /**
     * Delay between redraws. Zero is still delayed by raw computations and draw calls
     */
    static final int SLEEP = 0;
    static final int PIXEL_WIDTH = 6;
    static final int SIZE = 130 * PIXEL_WIDTH;

    private BufferedImage canvas = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
    private RainbowRGBCycle rgbCycle = new RainbowRGBCycle(1);

    public BufferedImage getCanvas() {
        return canvas;
    }

    public Day6Animation() {
        this.setSize(SIZE, SIZE);
        this.setPreferredSize(new Dimension(SIZE, SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    public void reset(boolean repaint) {
        canvas = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        rgbCycle = new RainbowRGBCycle(1);
        if (repaint) {
            this.repaint();
        }
    }

    public void start() {
        JFrame frame = new JFrame("AOC 2024 Day 6");
        frame.add(this);
        frame.setSize(this.getWidth() + 16, this.getHeight() + 39);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void moveTo(int x, int y) {
        setColor(x, y, rgbCycle.getNext(), true);
    }

    public void moveToFast(MatrixPosition pos) {
        setColor(pos.col(), pos.row(), rgbCycle.getNext(), false);
    }

    public void moveTo(MatrixPosition pos) {
        moveTo(pos.col(), pos.row());
    }

    public void placeWallAt(int x, int y) {
        setColor(x, y, Color.GRAY.getRGB(), false);
    }

    public void placeWallAt(MatrixPosition pos) {
        placeWallAt(pos.col(), pos.row());
    }

    public void setColor(MatrixPosition pos, int color) {
        setColor(pos.col(), pos.row(), color, true);
    }

    public void setColor(int x, int y, int color, boolean repaint) {
        for (int xi = PIXEL_WIDTH * x; xi < (PIXEL_WIDTH * (x+1)); ++xi) {
            for (int yi = PIXEL_WIDTH * y; yi < (PIXEL_WIDTH) * (y+1); ++yi) {
                canvas.setRGB(xi, yi, color);
            }
        }
        if (repaint) {
            this.repaint();
        }
    }

    public class RainbowRGBCycle {
        private int red, green, blue; // Current RGB values
        private int step; // Increment for transitions
        private int phase; // Current phase of the RGB cycle (0 to 5)

        public RainbowRGBCycle() {
            this(1);
        }


        public RainbowRGBCycle(int step) {
            this.step = step;
            this.red = 255;
            this.green = 0;
            this.blue = 0;
            this.phase = 0; // Start with red to yellow transition
        }

        public int getNext() {
            // Transition between colors based on the current phase
            switch (phase) {
                case 0: // Red to Yellow (increase green)
                    green += step;
                    if (green >= 255) {
                        green = 255;
                        phase = 1; // Move to next phase
                    }
                    break;
                case 1: // Yellow to Green (decrease red)
                    red -= step;
                    if (red <= 0) {
                        red = 0;
                        phase = 2; // Move to next phase
                    }
                    break;
                case 2: // Green to Cyan (increase blue)
                    blue += step;
                    if (blue >= 255) {
                        blue = 255;
                        phase = 3; // Move to next phase
                    }
                    break;
                case 3: // Cyan to Blue (decrease green)
                    green -= step;
                    if (green <= 0) {
                        green = 0;
                        phase = 4; // Move to next phase
                    }
                    break;
                case 4: // Blue to Magenta (increase red)
                    red += step;
                    if (red >= 255) {
                        red = 255;
                        phase = 5; // Move to next phase
                    }
                    break;
                case 5: // Magenta to Red (decrease blue)
                    blue -= step;
                    if (blue <= 0) {
                        blue = 0;
                        phase = 0; // Wrap back to the first phase
                    }
                    break;
            }

            // Return the current color
            return (255 << 24) | (red << 16) | (green << 8) | blue;
        }

        public void reset() {
            // Reset to the initial state
            red = 255;
            green = 0;
            blue = 0;
            phase = 0;
        }
    }

}
