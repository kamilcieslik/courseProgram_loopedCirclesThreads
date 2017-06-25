import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

class Panel extends JPanel {
    private static final long serialVersionUID = 1L;
    Ring ring = new Ring();
    List<Circle> circles = new ArrayList<>();

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.ring.draw(this, g);
        Circle c;
        for (Iterator<Circle> localIterator = this.circles.iterator(); localIterator.hasNext(); c.draw(g)) {
            c = localIterator.next();
        }
    }
}
