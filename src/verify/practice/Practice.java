package verify.practice;

import verify.BooleanImage;
import verify.Eraser;
import verify.Sissors;
import verify.VerifyCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@SuppressWarnings("serial")
public class Practice extends JFrame implements MouseListener {

    public static void main(String[] args) throws UnsupportedOperationException, InterruptedException,
            IOException {
        new Practice();
    }

    BlockingQueue<String> numQueue = new ArrayBlockingQueue<>(1);
    BlockingQueue<BooleanImage> imageQueue = new ArrayBlockingQueue<>(17);
    BlockingQueue<SaveOption> saveQueue = new ArrayBlockingQueue<>(17);
    JTextArea area = new JTextArea();

    private Practice() {
        super("Code Verifying Practice");
        init();
        Thread downThread = new Thread(this::downloadPro);
        downThread.setDaemon(true);
        Thread saveThread = new Thread(this::savePro);
        saveThread.setDaemon(true);
        downThread.start();
        saveThread.start();
        practicePro();
    }

    private void init() {

        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setForeground(Color.WHITE);
        area.setSize(550, 360);
        area.setBackground(Color.BLACK);
        JPanel southPanel = new JPanel(new FlowLayout());
        String[] btnTexts = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "PASS"};
        Button btn;
        for (String text : btnTexts) {
            btn = new Button(text);
            southPanel.add(btn);
            btn.addMouseListener(this);
        }
        setLayout(new BorderLayout());
        add(area, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void downloadPro() {
        BooleanImage img;
        List<BooleanImage> pieces;
        while (true) {
            try {
                img = new BooleanImage(VerifyCode.getImage(null));
                Eraser.erase(img);
                pieces = Sissors.cut(img);
                if (pieces == null) continue;
                for (BooleanImage piece : pieces)
                    imageQueue.put(piece);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void practicePro() {
        BooleanImage img;
        String num;
        while (true) {
            try {
                img = imageQueue.take();
                area.setText(img.toString());
                num = numQueue.take();
                if (!num.equals("PASS")) saveQueue.put(new SaveOption(num, img));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void savePro() {
        SaveOption save;
        File file;
        Random random = new Random();
        FileWriter out;
        while (true) {
            try {
                save = saveQueue.take();
                do {
                    file = new File("data/codes/" + save.num + "/" + random.nextDouble() + ".eye");
                } while (file.exists());
                out = new FileWriter(file);
                out.write(save.image.toString('1', '0'));
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        Button btn = (Button) arg0.getSource();
        try {
            numQueue.put(btn.getText());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}

class Button extends JButton {
    public Button(String text) {
        super(text);
        this.setFont(new Font("Arial", Font.PLAIN, 14));
        this.setHorizontalAlignment(CENTER);
        this.setVerticalAlignment(CENTER);
        this.setSize(30, 18);
    }
}

class SaveOption {

    public final String num;
    public final BooleanImage image;

    public SaveOption(String num, BooleanImage image) {
        this.num = num;
        this.image = image;
    }

}
