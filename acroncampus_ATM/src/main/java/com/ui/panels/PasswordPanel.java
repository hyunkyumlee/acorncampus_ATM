package com.ui.panels;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class PasswordPanel extends JPanel {
    private final JPasswordField passwordField;
    private final Color brandColor = new Color(20, 60, 140);
    private final Color screenBgColor = new Color(245, 248, 255);

    public PasswordPanel(ActionListener confirmListener, ActionListener cancelListener) {
        setLayout(null);
        setBackground(screenBgColor);

        JLabel titleLabel = new JLabel("비밀번호 4자리를 입력해주십시오", SwingConstants.CENTER);
        titleLabel.setForeground(brandColor);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setBounds(150, 80, 400, 40);
        add(titleLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBounds(250, 150, 200, 60);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(new LineBorder(Color.GRAY, 2));
        add(passwordField);

        JButton confirmBtn = new JButton("확인");
        confirmBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        confirmBtn.setBackground(brandColor);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBounds(250, 250, 200, 50);
        confirmBtn.addActionListener(confirmListener);
        add(confirmBtn);

        JButton cancelBtn = new JButton("거래 취소");
        cancelBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        cancelBtn.setBackground(new Color(200, 50, 50));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBounds(250, 310, 200, 50);
        cancelBtn.addActionListener(cancelListener);
        add(cancelBtn);
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void clearPassword() {
        passwordField.setText("");
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }
}