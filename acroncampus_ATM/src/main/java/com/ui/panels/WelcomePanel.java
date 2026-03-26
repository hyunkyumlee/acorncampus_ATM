package com.ui.panels;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel() {
        setBackground(new Color(245, 248, 255));
        setLayout(new BorderLayout());

        JLabel welcomeText = new JLabel("<html><center><span style='font-size:24px; color:#143c8c;'><b>환영합니다</b></span><br><br><span style='font-size:16px; color:#333333;'>아래 인벤토리에서<br>사용할 카드를 클릭하여 기계에 넣어주세요</span></center></html>", SwingConstants.CENTER);
        add(welcomeText, BorderLayout.CENTER);
    }
}