// DronePanel.java
// Handles image loading, point sampling, and dot animation

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public class DronePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int IMAGE_COUNT = 1; // <- *SET TO DESIRED NUMBER OF IMAGES*
    private static final int DEFAULT_SPACING = 6;
    private static final int SOCCER_SPACING  = 1; 
    private static final int DOT_RADIUS = 3;
    private static final int FPS = 60;
    private static final int TRANSITION_SECONDS = 10;
    private static final int DURATION_FRAMES = FPS * TRANSITION_SECONDS;
    private static final int MAX_DELAY = FPS;
    private static final int BRIGHTNESS_THRESHOLD = 200;
    private static final String IMAGE_PATH_FMT = "images/frame%d.jpg";

    private static final Color[] FORMATION_COLORS = {
            new Color(0x000000), // first image = black
            new Color(0xFF6B6B), // second image = light-red
            new Color(0x629677), // third image = zomp
            new Color(0xFFD275), // fourth image = jasmine
            new Color(0x006494)  // fifth image = lapiz lazuli
    };

    private BufferedImage[] scaled = new BufferedImage[IMAGE_COUNT];
    @SuppressWarnings("unchecked")
    private List<Point>[] pointLists = (List<Point>[]) new List[IMAGE_COUNT];

    private List<Dot> dots = new ArrayList<>();
    private Timer timer;
    private int animationIndex = 0;
    private int frameTick = 0;
    private Random rand = new Random();

    public DronePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        loadAndDownscaleImages();
        sampleAndEqualizePoints();
        initInitialDots();
        timer = new Timer(1000 / FPS, this);
    }

    private void loadAndDownscaleImages() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            String path = String.format(IMAGE_PATH_FMT, i + 1);
            try {
                BufferedImage orig = ImageIO.read(new File(path));
                BufferedImage tmp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(orig, 0, 0, WIDTH, HEIGHT, null);
                g2.dispose();
                scaled[i] = tmp;
            } catch (IOException ex) {
                System.err.println("Error loading image: " + path);
                ex.printStackTrace();
            }
        }
    }

    private void sampleAndEqualizePoints() {
        @SuppressWarnings("unchecked")
        List<Point>[] raw = (List<Point>[]) new List[IMAGE_COUNT];

        for (int i = 0; i < IMAGE_COUNT; i++) {
            // choose spacing per image
            int spacing = (i == 2) ? SOCCER_SPACING : DEFAULT_SPACING;
            raw[i] = sampleMask(scaled[i], spacing);
        }
        int minSize = Arrays.stream(raw).mapToInt(List::size).min().orElse(0);
        for (int i = 0; i < IMAGE_COUNT; i++) {
            Collections.shuffle(raw[i]);
            pointLists[i] = new ArrayList<>(raw[i].subList(0, minSize));
        }
    }

    private void initInitialDots() {
        List<Point> start = pointLists[0];
        Color initialColor = FORMATION_COLORS[0];
        for (Point p : start) {
            int sx = rand.nextInt(WIDTH);
            int sy = rand.nextInt(HEIGHT);
            dots.add(new Dot(sx, sy, p.x, p.y, initialColor));
        }
    }

    public void startNextAnimation() {
        frameTick = 0;
        Color formationColor = FORMATION_COLORS[animationIndex];

        List<Point> targets = pointLists[animationIndex];
        for (int i = 0; i < dots.size(); i++) {
            Dot d = dots.get(i);
            d.startX = d.x;
            d.startY = d.y;
            Point np = targets.get(i);
            d.targetX = np.x;
            d.targetY = np.y;
            d.delay = rand.nextInt(MAX_DELAY);
            d.startColor = d.color;
            d.targetColor = formationColor;
            d.startAlpha = 0f;
            d.targetAlpha = 1f;
        }
        animationIndex = (animationIndex + 1) % IMAGE_COUNT;
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Dot d : dots) {
            // apply per‑dot opacity
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));
            g2.setColor(d.color);
            g2.fillOval((int) (d.x - DOT_RADIUS), (int) (d.y - DOT_RADIUS), DOT_RADIUS * 2, DOT_RADIUS * 2);
        }
        // reset to fully opaque for any other drawing
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frameTick++;
        boolean allDone = true;
        for (Dot d : dots) {
            int t = frameTick - d.delay;
            if (t < 0) { allDone = false; continue; }
            double prog = Math.min(1.0, (double)t / DURATION_FRAMES);
            double eased = 0.5 - 0.5 * Math.cos(Math.PI * prog);
            d.x = d.startX + (d.targetX - d.startX) * eased;
            d.y = d.startY + (d.targetY - d.startY) * eased;
            if (prog < 1.0) allDone = false;

            d.alpha = d.startAlpha + (d.targetAlpha - d.startAlpha) * (float)eased;

            // interpolate color channels
            int r = (int)(d.startColor.getRed()   + (d.targetColor.getRed()   - d.startColor.getRed())   * eased);
            int gC= (int)(d.startColor.getGreen() + (d.targetColor.getGreen() - d.startColor.getGreen()) * eased);
            int b = (int)(d.startColor.getBlue()  + (d.targetColor.getBlue()  - d.startColor.getBlue())  * eased);
            d.color = new Color(r, gC, b);

        }
        repaint();
        if (allDone) timer.stop();
    }

    private List<Point> sampleMask(BufferedImage img, int spacing) {
        List<Point> pts = new ArrayList<>();
        for (int y = 0; y < img.getHeight(); y += spacing) {
            for (int x = 0; x < img.getWidth(); x += spacing) {
                int rgb = img.getRGB(x, y);
                int avg = (((rgb>>16)&0xFF) + ((rgb>>8)&0xFF) + (rgb&0xFF)) / 3;
                if (avg < BRIGHTNESS_THRESHOLD) pts.add(new Point(x, y));
            }
        }
        return pts;
    }
}
