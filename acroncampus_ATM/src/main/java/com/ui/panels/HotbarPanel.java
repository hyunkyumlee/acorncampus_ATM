package com.ui.panels;

import com.entity.Account;
import com.service.interfacePackage.AccountAuthService;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class HotbarPanel extends JPanel {

    public HotbarPanel(AccountAuthService accountAuthService, Consumer<Account> accountSelectionConsumer) {
        super(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBackground(new Color(40, 45, 50));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "내 지갑 (인벤토리)",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("맑은 고딕", Font.BOLD, 12), Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 5, 10), border));

        List<Account> accounts = accountAuthService.getAccounts();

        for (Account account : accounts) {
            String buttonText = account.getAccountNo() + "\n(" + account.getOwnerName() + ")";
            JButton slotBtn = new JButton("<html><center>" + buttonText.replaceAll("\n", "<br>") + "</center></html>");
            slotBtn.setPreferredSize(new Dimension(80, 70));
            slotBtn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
            slotBtn.setBackground(new Color(200, 205, 210));
            slotBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            slotBtn.addActionListener(e -> accountSelectionConsumer.accept(account));
            add(slotBtn);
        }
    }
}