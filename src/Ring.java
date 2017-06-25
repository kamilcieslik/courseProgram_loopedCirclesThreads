import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

class Ring {
    int RING_DIAMETER;

    /*
     * Rysowanie głównego pierścienia.
     * Średnica pierścienia zależna jest zawsze od krótszej krawędzi panelu.
     */
    void draw(JPanel panel, Graphics g) {
        g.setColor(Color.BLACK);
        if ((RING_DIAMETER != panel.getWidth() - 200) || (RING_DIAMETER != panel.getHeight() - 200))
            if (panel.getWidth() < panel.getHeight())
                RING_DIAMETER = panel.getWidth() - 200;
            else
                RING_DIAMETER = panel.getHeight() - 200;
        g.drawOval((panel.getWidth() / 2 - RING_DIAMETER / 2), (panel.getHeight() / 2 - RING_DIAMETER / 2), RING_DIAMETER, RING_DIAMETER);
    }
}