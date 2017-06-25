import javax.swing.*;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 4782495194023021856L;

    private MainFrame() {
        super("Program multiwątkowy");
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Panel panel = new Panel();
        setContentPane(panel);
        setVisible(true);

        /*
          Przycisk, który po kliknięciu dodaje do głównego pierścienia kolejny poruszający się okrąg.
         */
        JButton JButtonAddThread = new JButton("+");
        JButtonAddThread.addActionListener(e -> panel.circles.add(new Circle(panel, panel.ring)));
        JButtonAddThread.setBounds(panel.getWidth() / 2 - 70, 20, 50, 50);
        panel.add(JButtonAddThread);

        /*
          Przycisk, który po kliknięciu usuwa z głównego pierścienia ostatnio dodany okrąg.
         */
        JButton JButtonDeleteThread = new JButton("-");
        JButtonDeleteThread.addActionListener(e -> {
            try {
                panel.circles.remove(panel.circles.size() - 1);
            } catch (ArrayIndexOutOfBoundsException e2) {
                JOptionPane.showMessageDialog(null, "Brak watkow. Nie ma czego usuwac!");
            }
            panel.repaint();
        });
        JButtonDeleteThread.setBounds(panel.getWidth() / 2 + 30, 20, 50, 50);
        panel.add(JButtonDeleteThread);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
