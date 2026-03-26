package com.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class MenuPanel extends JPanel {
    private final Color brandColor = new Color(20, 60, 140);
    private final Color screenBgColor = new Color(245, 248, 255);

    public MenuPanel(Consumer<Integer> menuActionConsumer, ActionListener ejectListener) {
        setLayout(new BorderLayout(20, 20));
        setBackground(screenBgColor);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("원하시는 거래를 선택해주십시오", SwingConstants.CENTER);
        titleLabel.setForeground(brandColor);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBackground(screenBgColor);

        String[] btnTitles = {"잔액 조회", "현금 입금", "현금 출금", "계좌 이체", "거래 내역 조회", "거래 종료 (카드 반환)"};

        for (int i = 0; i < btnTitles.length; i++) {
            JButton btn = new JButton(btnTitles[i]);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(50, 50, 50));
            btn.setBorder(new LineBorder(new Color(200, 200, 200), 2, true));
            btn.setFocusPainted(false);

            if (i == 5) { // 종료 버튼
                btn.setBackground(new Color(240, 240, 240));
                btn.setForeground(Color.RED.darker());
                btn.addActionListener(ejectListener);
            } else {
                final int idx = i;
                btn.addActionListener(e -> menuActionConsumer.accept(idx));
            }
            buttonPanel.add(btn);
        }
        add(buttonPanel, BorderLayout.CENTER);
    }
}