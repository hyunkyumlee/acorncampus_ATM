package com.ui.panels;

import javax.swing.*;
import java.awt.*;

public class AnimationPanel extends JPanel {
    private int itemY;
    private Timer timer;
    private String accNumber = "";
    private Runnable onComplete;

    private final Color screenBgColor;
    private final Color brandColor;

    public AnimationPanel(Color screenBgColor, Color brandColor) {
        this.screenBgColor = screenBgColor;
        this.brandColor = brandColor;
        setBackground(this.screenBgColor);
    }

    public void startAnimation(String accNumber, Runnable onComplete) {
        this.accNumber = accNumber;
        this.onComplete = onComplete;
        this.itemY = getHeight() > 0 ? getHeight() : 500;

        if (timer != null && timer.isRunning()) timer.stop();

        timer = new Timer(20, e -> {
            itemY -= 8;
            if (itemY < 150) {
                timer.stop();
                Timer delay = new Timer(500, ev -> onComplete.run());
                delay.setRepeats(false);
                delay.start();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(240, 200, 100));
        g.fillRoundRect(getWidth() / 2 - 60, itemY, 120, 180, 10, 10);

        g.setColor(Color.BLACK);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        g.drawString("Acorn Card", getWidth() / 2 - 40, itemY + 30);
        g.drawString(accNumber, getWidth() / 2 - 35, itemY + 60);

        g.setColor(screenBgColor);
        g.fillRect(0, 0, getWidth(), 170);

        g.setColor(Color.BLACK);
        g.fillRoundRect(getWidth() / 2 - 80, 160, 160, 10, 5, 5);

        g.setColor(brandColor);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g.drawString("카드를 읽는 중입니다...", getWidth() / 2 - 100, 100);
    }
}