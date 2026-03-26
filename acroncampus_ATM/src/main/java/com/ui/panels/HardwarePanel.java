package com.ui.panels;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HardwarePanel extends JPanel {

    public HardwarePanel(PasswordPanel passwordPanel, ActionListener adminListener, ActionListener keypadConfirmListener) {
        super(new BorderLayout(20, 0));
        setBackground(new Color(210, 215, 220));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setPreferredSize(new Dimension(0, 180));

        // 왼쪽: 현금 및 명세표 나오는 곳
        JPanel slotPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        slotPanel.setOpaque(false);

        JLabel cashSlot = new JLabel("====== 현 금 (CASH) ======", SwingConstants.CENTER);
        cashSlot.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        cashSlot.setOpaque(true);
        cashSlot.setBackground(new Color(30, 30, 30));
        cashSlot.setForeground(Color.GREEN);
        cashSlot.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JPanel receiptAndAdminPanel = new JPanel(new BorderLayout());
        receiptAndAdminPanel.setOpaque(false);
        JLabel receiptSlot = new JLabel("▼ 명세표", SwingConstants.CENTER);
        receiptSlot.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        JButton adminBtn = new JButton("설정");
        adminBtn.setPreferredSize(new Dimension(60, 30));
        adminBtn.setBackground(Color.DARK_GRAY);
        adminBtn.setForeground(Color.WHITE);
        adminBtn.addActionListener(adminListener);

        receiptAndAdminPanel.add(receiptSlot, BorderLayout.CENTER);
        receiptAndAdminPanel.add(adminBtn, BorderLayout.EAST);

        slotPanel.add(cashSlot);
        slotPanel.add(receiptAndAdminPanel);
        add(slotPanel, BorderLayout.CENTER);

        // 오른쪽: 물리 숫자 키패드
        JPanel keypadPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        keypadPanel.setOpaque(false);
        keypadPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "KEYPAD"));

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "정정", "0", "확인"};
        for (String key : keys) {
            JButton keyBtn = new JButton(key);
            keyBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            keyBtn.setBackground(new Color(230, 230, 230));
            keyBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            if (key.equals("정정")) keyBtn.setBackground(new Color(255, 200, 100));
            if (key.equals("확인")) keyBtn.setBackground(new Color(100, 200, 100));

            keyBtn.addActionListener(e -> {
                JPasswordField pwField = passwordPanel.getPasswordField();
                if (pwField != null && passwordPanel.isShowing()) {
                    if (key.equals("정정")) {
                        pwField.setText("");
                    } else if (key.equals("확인")) {
                        keypadConfirmListener.actionPerformed(e);
                    } else {
                        pwField.setText(new String(pwField.getPassword()) + key);
                    }
                }
            });
            keypadPanel.add(keyBtn);
        }
        add(keypadPanel, BorderLayout.EAST);
    }
}