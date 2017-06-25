import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

class Circle implements Runnable {
    private Random random = new Random();
    private final Panel panel;
    private Ring ring;
    private float x = 0;
    private float y = 0;
    private float diameter;
    private double alpha = 0;
    private double stepLength;
    private Thread referenceToThread;
    private int R = random.nextInt(255);
    private int G = random.nextInt(255);
    private int B = random.nextInt(255);
    private Circle blockedCircle = null;
    private Circle blockerCircle = null;

    Circle(Panel panel, Ring ring) {
        this.ring = ring;
        this.panel = panel;
        this.alpha = random.nextInt(350);
        this.stepLength = (1 + random.nextInt(10));
        this.diameter = 20;
        Thread t = new Thread(this);
        this.referenceToThread = t;
        t.start();
    }

    /*
     * Funkcja wywoływana w sekcji krytycznej panelu w metodzie run.
     * Zwraca okrąg blokujący.
     */
    private Circle IsCollision() {
        for (int i = 0; i < panel.circles.size(); i++) {
            if (panel.circles.get(i) != this) {
                float dx;
                float dy;
                double betha = alpha + 2 * (stepLength / 1000);
                dx = (float) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(betha));
                dy = (float) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(betha));

                float xd = dx - panel.circles.get(i).x;
                float yd = dy - panel.circles.get(i).y;

                float sumRadius = this.diameter / 2 + panel.circles.get(i).diameter / 2;
                float sqrRadius = sumRadius * sumRadius;

                float distSqr = (xd * xd) + (yd * yd);

                boolean collision = false;

                if (distSqr <= sqrRadius) {
                    collision = true;
                }

                if (collision) {
                    return panel.circles.get(i);
                }
            }
        }
        return null;
    }

    /*
     * Funkcja wywoływana w metodzie run po wykryciu w sekcji krytycznej okręgu blokującego.
     * Okrąg blokujący będący wynikiem poprzedniej funkcji jest przekazywany jako argument do tej.
     * Spawdzanie kolizji nie odbywa się niepotrzebnie dla całej listy okręgów ale tylko dla tych których dotyczy
     * możliwość kolizji.
     * Pozwoliło zaoszczędzić sporo niepotrzebnej pracy procesorowi.
     */
    private boolean IsCollision(Circle blockerCircle) {
        float dx;
        float dy;
        double betha = alpha + 2 * (stepLength / 1000);
        dx = (float) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(betha));
        dy = (float) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(betha));

        float xd = dx - blockerCircle.x;
        float yd = dy - blockerCircle.y;

        float sumRadius = this.diameter / 2 + blockerCircle.diameter / 2;
        float sqrRadius = sumRadius * sumRadius;

        float distSqr = (xd * xd) + (yd * yd);

        boolean collision = false;

        if (distSqr <= sqrRadius) {
            collision = true;
        }

        return collision;
    }

    /*
     * Funkcja wywoływana w metodzie rysującej okrąg lecz tylko przy pierwszym rysowaniu (gdy x oraz y = 0).
     */
    private boolean IsCollision(float potentialX, float potentialY) {
        for (int i = 0; i < panel.circles.size(); i++) {
            if (panel.circles.get(i).referenceToThread != this.referenceToThread) {

                double betha = alpha + 2 * (stepLength / 1000);
                potentialX = (float) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(betha));
                potentialY = (float) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(betha));

                float xd = potentialX - panel.circles.get(i).x;
                float yd = potentialY - panel.circles.get(i).y;

                float sumRadius = this.diameter / 2 + panel.circles.get(i).diameter / 2;
                float sqrRadius = sumRadius * sumRadius;

                float distSqr = (xd * xd) + (yd * yd);

                boolean collision = false;

                if (distSqr <= sqrRadius) {
                    collision = true;
                }

                if (collision) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doOneStep() {
        alpha += (stepLength / 1000);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
        }
        this.x = (int) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(alpha));
        this.y = (int) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(alpha));
    }

    @Override
    public void run() {
        for (; ; ) {
            if (blockedCircle == null) {
                synchronized (this.panel) {
                    if (IsCollision() != null) {
                        blockedCircle = this;
                        blockerCircle = IsCollision();
                    }
                }
            }

            if (blockedCircle != null) {
                try {
                    do {
                        this.panel.repaint();
                    } while (IsCollision(blockerCircle));
                } catch (NullPointerException ignored) {

                }
                blockedCircle = null;

            }
            doOneStep();
            this.panel.repaint();
        }
    }

    /*
     * Rysowanie okręgu.
     * Warunek instrukcji warunkowej prawdziwy tylko dla nowego okręgu.
     * Dzięki poniższym liniom kodu instrukcji warunkowej nie będzie sytuacji w której nowy okrąg powstałby nachodząc
     * na inny - pseudolosowanie potencjalnych współrzędnych aż do momentu w którym nowy okrąg nie będzie kolidował z
     * innymi.
     */
    void draw(Graphics g) {
        if ((this.x == 0) && (this.y == 0)) {
            float potentialX;
            float potentialY;
            do {
                this.alpha = random.nextInt(350);
                potentialX = (float) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(alpha));
                potentialY = (float) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(alpha));
            } while (IsCollision(potentialX, potentialY));
            this.x = potentialX;
            this.y = potentialY;
        } else {
            this.x = (float) ((panel.getWidth() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.sin(alpha));
            this.y = (float) ((panel.getHeight() / 2 - this.diameter / 2) + ring.RING_DIAMETER / 2 * Math.cos(alpha));
        }

        g.setColor(new Color(R, G, B));
        g.fillOval((int) x, (int) y, (int) diameter, (int) diameter);
        g.setColor(new Color(0, 0, 0));
        g.drawOval((int) x, (int) y, (int) diameter, (int) diameter);
    }
}