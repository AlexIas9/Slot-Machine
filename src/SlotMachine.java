import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SlotMachine extends JFrame {
    private JLabel slot1, slot2, slot3;
    private JButton spinButton;
    private JLabel resultLabel;
    private JLabel coinsLabel;
    private JTextField betField;
    private Random random;
    private int coins = 100; // Numărul inițial de monede
    private Timer timer;
    private int[] results = new int[3];
    private int spinCount = 0; // Contor pentru rotiri
    private final int MAX_SPINS = 10; // Număr maxim de rotații înainte să se oprească

    // Array pentru a stoca imaginile redimensionate
    private ImageIcon[] resizedIcons = new ImageIcon[6];

    // Calea imaginilor
    private String[] imagePaths = {
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol1.png",
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol2.png",
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol3.png",
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol4.png",
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol5.png",
            "C:/Users/ias_a/OneDrive/Desktop/VS/Slot_Machine/Poze/symbol6.png"
    };

    public SlotMachine() {
        random = new Random();

        // Încărcăm imaginile într-un background thread pentru a evita blocarea GUI
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < imagePaths.length; i++) {
                resizedIcons[i] = resizeImage(imagePaths[i], 100, 100);
                // Verificăm dacă imaginea a fost încărcată corect
                if (resizedIcons[i] == null) {
                    System.err.println("Eroare la încărcarea imaginii: " + imagePaths[i]);
                }
            }
        });

        // Setările ferestrei
        setTitle("Slot Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Fereastra va fi centrată

        // Panel pentru sloturi
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new GridLayout(1, 3, 10, 10));

        // Inițializăm sloturile cu imagini temporare (până sunt încărcate cele finale)
        slot1 = new JLabel(new ImageIcon(), JLabel.CENTER);
        slot2 = new JLabel(new ImageIcon(), JLabel.CENTER);
        slot3 = new JLabel(new ImageIcon(), JLabel.CENTER);

        slotPanel.add(slot1);
        slotPanel.add(slot2);
        slotPanel.add(slot3);

        add(slotPanel, BorderLayout.CENTER);

        // Eticheta pentru rezultat
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setForeground(Color.RED);
        add(resultLabel, BorderLayout.NORTH);

        // Butonul de spin
        spinButton = new JButton("Spin");
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.addActionListener(new SpinAction());
        add(spinButton, BorderLayout.SOUTH);

        // Panel pentru pariuri și monede
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 1));

        // Eticheta pentru monede
        coinsLabel = new JLabel("Monede: " + coins, JLabel.CENTER);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        coinsLabel.setForeground(Color.BLUE);
        controlPanel.add(coinsLabel);

        // Pariu
        betField = new JTextField("10");
        betField.setHorizontalAlignment(JTextField.CENTER);
        betField.setFont(new Font("Arial", Font.PLAIN, 18));
        controlPanel.add(new JLabel("Introdu pariul: ", JLabel.CENTER));
        controlPanel.add(betField);

        add(controlPanel, BorderLayout.WEST);

        setVisible(true);
    }

    // Metoda pentru a redimensiona imaginile (o singură dată)
    private ImageIcon resizeImage(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(path);
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (Exception e) {
            System.err.println("Eroare la redimensionarea imaginii: " + path);
            return null;
        }
    }

    // Metoda pentru a simula rotirea fiecărui slot
    private void spinSlots() {
        int bet = Integer.parseInt(betField.getText());

        if (bet > coins) {
            resultLabel.setText("Pariul este prea mare! Scade-l.");
            return;
        }

        // Scade pariul din monede
        coins -= bet;
        coinsLabel.setText("Monede: " + coins);

        spinCount = 0; // Resetăm numărătoarea rotațiilor

        // Pornim un timer pentru a anima rolele, cu un delay mai mic (50ms)
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fiecare rolă primește o valoare aleatorie între 0 și 5
                results[0] = random.nextInt(6);
                results[1] = random.nextInt(6);
                results[2] = random.nextInt(6);

                // Verificăm dacă imaginile au fost încărcate și le actualizăm
                slot1.setIcon(resizedIcons[results[0]]);
                slot2.setIcon(resizedIcons[results[1]]);
                slot3.setIcon(resizedIcons[results[2]]);

                spinCount++;

                // Oprirea animației după MAX_SPINS
                if (spinCount >= MAX_SPINS) {
                    timer.stop(); // Oprim timerul după numărul maxim de rotații
                    checkResults(bet);
                }
            }
        });
        timer.start();
    }

    // Verificăm rezultatele și ajustăm monedele
    private void checkResults(int bet) {
        if (results[0] == results[1] && results[1] == results[2]) {
            // Toate sunt la fel => câștig
            int winAmount = bet * 3;
            coins += winAmount;
            resultLabel.setText("Felicitări! Ai câștigat " + winAmount + " monede!");
        } else {
            resultLabel.setText("Ai pierdut. Încearcă din nou.");
        }
        coinsLabel.setText("Monede: " + coins);

        // Verificăm dacă utilizatorul mai are monede
        if (coins <= 0) {
            resultLabel.setText("Joc terminat! Nu mai ai monede.");
            spinButton.setEnabled(false);
        }
    }

    // Acțiunea declanșată la apăsarea butonului de spin
    private class SpinAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Ne asigurăm că nu mai există un timer activ înainte să pornim unul nou
            if (timer == null || !timer.isRunning()) {
                spinSlots(); // Doar dacă nu e deja în mișcare, pornește sloturile
            }
        }
    }

    public static void main(String[] args) {
        new SlotMachine();
    }
}
