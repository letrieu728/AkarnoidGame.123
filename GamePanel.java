package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        initGame();
        timer = new Timer(10, this);
        timer.start();
    }

    private void initGame() {
        // TODO: khởi tạo ball, paddle, bricks
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: vẽ ball, paddle, bricks, items
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: update game mỗi tick
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO: xử lý phím trái/phải
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO: xử lý nhả phím
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
