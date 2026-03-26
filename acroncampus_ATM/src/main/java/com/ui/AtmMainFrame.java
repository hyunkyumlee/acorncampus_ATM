package com.ui;

import javax.swing.*;
import java.awt.*;
import com.entity.Account;
import com.entity.AtmMachine;
import com.service.interfacePackage.AccountAuthService;
import com.service.interfacePackage.AdminService;
import com.service.interfacePackage.TransactionService;
import com.service.servicePackage.AccountAuthServiceImp;
import com.service.servicePackage.DeunAdmin;
import com.service.servicePackage.transaction.TransactionBalanceService;
import com.ui.panels.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.ActionListener;

public class AtmMainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel screenPanel; // ATM 화면 부분

    private String selectedAccount = "";
    private boolean isMachineReady = true;

    // 화면 패널들
    private AnimationPanel animationPanel;
    private JPanel passwordPanel;
    
    // 서비스 로직을 담당할 객체들
    private AccountAuthService accountAuthService;
    private TransactionService transactionService;
    private AdminService adminService;
    private AtmMachine atmMachine;

    // 브랜드 컬러 (우리/신한은행 느낌의 블루)
    private Color brandColor = new Color(20, 60, 140);
    private Color screenBgColor = new Color(245, 248, 255); // 밝은 스크린 배경

    public AtmMainFrame() {
        // 서비스 객체들 초기화 및 의존성 주입
        initServices();

        setTitle("ACRON ATM 1인칭 시뮬레이터");
        setSize(850, 850); // 물리 키패드 공간 확보를 위해 세로 길이 약간 늘림
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(40, 45, 50)); // 주변 배경 (어두운 실내)
        setLayout(new BorderLayout(0, 10));

        // --- [ATM 기계 몸체 패널] ---
        JPanel machineBodyPanel = new JPanel(new BorderLayout(0, 10));
        machineBodyPanel.setBackground(new Color(225, 230, 235)); // 밝은 메탈/플라스틱 느낌
        machineBodyPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 40, 10, 40),
                BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.GRAY)
        ));

        // --- [상단 간판 (브랜드)] ---
        JLabel brandLabel = new JLabel(" 에이콘 은행 ACORN BANK", SwingConstants.LEFT);
        brandLabel.setOpaque(true);
        brandLabel.setBackground(brandColor);
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        brandLabel.setBorder(new EmptyBorder(15, 20, 15, 20));
        machineBodyPanel.add(brandLabel, BorderLayout.NORTH);

        // --- [중앙 스크린 세팅] ---
        cardLayout = new CardLayout();
        screenPanel = new JPanel(cardLayout);
        screenPanel.setBackground(screenBgColor);

        // 스크린을 감싸는 두꺼운 베젤
        JPanel bezelPanel = new JPanel(new BorderLayout());
        bezelPanel.setBackground(new Color(15, 15, 15)); // 검은색 얇은 모니터 테두리
        bezelPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 30, 10, 30), // 베젤 바깥 여백
                new LineBorder(brandColor, 8, true) // 은행 컬러 프레임
        ));
        bezelPanel.add(screenPanel, BorderLayout.CENTER);

        // --- 패널 초기화 및 CardLayout에 추가 ---
        WelcomePanel welcomePanel = new WelcomePanel();
        animationPanel = new AnimationPanel(screenBgColor, brandColor);
        PasswordPanel passwordPanel = new PasswordPanel(e -> processPassword(), e -> ejectBankbook());
        MenuPanel menuPanel = new MenuPanel(this::handleMenuAction, e -> ejectBankbook());

        screenPanel.add(welcomePanel, "WELCOME");
        screenPanel.add(animationPanel, "ANIMATION");
        screenPanel.add(passwordPanel, "PASSWORD");
        screenPanel.add(menuPanel, "MENU");
        this.passwordPanel = passwordPanel; // 키패드 연동을 위해 참조 유지

        machineBodyPanel.add(bezelPanel, BorderLayout.CENTER);

        // --- [하단 하드웨어 패널 (키패드, 투입구)] ---
        ActionListener adminListener = e -> showAdminDialog();
        HardwarePanel hardwarePanel = new HardwarePanel(passwordPanel, adminListener, e -> processPassword());
        machineBodyPanel.add(hardwarePanel, BorderLayout.SOUTH);

        add(machineBodyPanel, BorderLayout.CENTER);

        // --- [인벤토리 (기존 유지)] ---
        Consumer<Account> accountSelectionConsumer = this::handleAccountSelection;
        HotbarPanel hotbarPanel = new HotbarPanel(accountAuthService, accountSelectionConsumer);
        add(hotbarPanel, BorderLayout.SOUTH);
    }

    /**
     * 서비스 객체들을 생성하고 서로의 의존성을 연결해주는 메서드
     */
    private void initServices() {
        // 1. 인증 서비스 생성 및 샘플 계좌 데이터 초기화
        this.accountAuthService = new AccountAuthServiceImp();
        this.accountAuthService.initSampleAccounts();

        // 2. 다른 서비스와 공유할 데이터(계좌 리스트, ATM 기기) 준비
        List<Account> accounts = this.accountAuthService.getAccounts();
        this.atmMachine = new AtmMachine(1_000_000_000L); // 초기 자본금 10억

        // 3. 거래 서비스와 관리자 서비스에 공유 데이터 주입
        this.transactionService = new TransactionBalanceService((ArrayList<Account>) accounts, this.atmMachine);
        this.adminService = new DeunAdmin(this.atmMachine, accounts, (TransactionBalanceService) this.transactionService);
    }

    /**
     * HotbarPanel에서 계좌(카드)가 선택되었을 때 호출되는 메서드
     * @param account 선택된 계좌 객체
     */
    private void handleAccountSelection(Account account) {
        if (isMachineReady) {
            isMachineReady = false;
            selectedAccount = account.getAccountNo();

            cardLayout.show(screenPanel, "ANIMATION");
            animationPanel.startAnimation(selectedAccount, () -> {
                ((PasswordPanel) passwordPanel).clearPassword();
                cardLayout.show(screenPanel, "PASSWORD");
            });
        } else {
            JOptionPane.showMessageDialog(this, "이미 기계가 사용 중입니다!", "경고", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 메뉴 버튼 클릭 시 실제 서비스 로직을 호출하는 메서드
     * @param index 클릭된 버튼의 인덱스
     */
    private void handleMenuAction(int index) {
        try {
            switch (index) {
                case 0: // 잔액조회 [영석 파트]
                    long balance = transactionService.checkBalance(selectedAccount);
                    JOptionPane.showMessageDialog(this, "현재 잔액은 " + String.format("%,d", balance) + "원 입니다.", "잔액 조회", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1: // 입금
                    String depAmountStr = JOptionPane.showInputDialog(this, "하단 투입구에 넣을 금액을 입력하세요 (1000원 단위):");
                    if (depAmountStr != null && !depAmountStr.isEmpty()) {
                        long depAmount = Long.parseLong(depAmountStr);
                        if (depAmount > 0 && depAmount % 1000 == 0) {
                            transactionService.deposit(selectedAccount, depAmount);
                            JOptionPane.showMessageDialog(this, String.format("%,d", depAmount) + "원이 입금 처리되었습니다.");
                        } else {
                            JOptionPane.showMessageDialog(this, "입금은 1,000원 단위로만 가능합니다.", "입금 단위 오류", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                case 2: // 출금
                    String widAmountStr = JOptionPane.showInputDialog(this, "출금하실 금액을 입력하세요 (10000원 단위):");
                    if (widAmountStr != null && !widAmountStr.isEmpty()) {
                        long widAmount = Long.parseLong(widAmountStr);
                        if (widAmount > 0 && widAmount % 10000 == 0) {
                            transactionService.withdraw(selectedAccount, widAmount);
                            JOptionPane.showMessageDialog(this, String.format("%,d", widAmount) + "원 출금 완료.\n명세표와 현금을 챙겨주세요.");
                        } else {
                            JOptionPane.showMessageDialog(this, "출금은 10,000원 단위로만 가능합니다.", "출금 단위 오류", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                case 3: // 이체
                    String toAccountNo = JOptionPane.showInputDialog(this, "이체받을 계좌번호를 입력하세요:");
                    if (toAccountNo != null && !toAccountNo.isEmpty()) {
                        String transAmountStr = JOptionPane.showInputDialog(this, "이체할 금액을 입력하세요:");
                        if (transAmountStr != null && !transAmountStr.isEmpty()) {
                            long transAmount = Long.parseLong(transAmountStr);
                            transactionService.transfer(selectedAccount, toAccountNo, transAmount);
                            JOptionPane.showMessageDialog(this, toAccountNo + " 계좌로 " + String.format("%,d", transAmount) + "원 이체 완료.");
                        }
                    }
                    break;
                case 4: // 내역
                    List<String> history = transactionService.getRecentHistory(selectedAccount);
                    JTextArea textArea = new JTextArea(10, 40);
                    textArea.setText(String.join("\n", history));
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    JOptionPane.showMessageDialog(this, scrollPane, "최근 거래 내역", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "금액은 숫자로만 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // TransactionService에서 발생한 예외 (잔액 부족 등)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "거래 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 거래를 종료하고 카드를 반환하는 로직
     */
    private void ejectBankbook() {
        // [현겸 파트] 로그아웃 처리
        accountAuthService.logout();
        selectedAccount = "";
        isMachineReady = true;
        cardLayout.show(screenPanel, "WELCOME");
        JOptionPane.showMessageDialog(this, "카드가 반환되었습니다.\n잊지 말고 챙겨가주십시오.", "거래 종료", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAdminDialog() {
        String adminPwd = JOptionPane.showInputDialog(this, "관리자 암호를 입력하세요:");
        if (adminService.adminLogin(adminPwd)) {
            showAdminMenu(); // 관리자 메뉴 호출
        } else if (adminPwd != null) { // 사용자가 취소를 누르지 않았을 때만 메시지 표시
            JOptionPane.showMessageDialog(this, "암호가 틀렸습니다.", "접근 거부", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 비밀번호를 검증하는 로직
     */
    private void processPassword() {
        String pwd = ((PasswordPanel) passwordPanel).getPassword();
        // [현겸 파트] 로그인 서비스 호출
        boolean authSuccess = accountAuthService.login(selectedAccount, pwd);

        if (authSuccess) {
            cardLayout.show(screenPanel, "MENU");
        } else {
            // 로그인 실패 시, 계좌가 잠겼는지 확인하여 다른 메시지 표시
            Account account = accountAuthService.findAccount(selectedAccount);
            if (account != null && account.isLocked()) {
                JOptionPane.showMessageDialog(this, "비밀번호 3회 오류로 계좌가 잠겼습니다.\n거래를 취소합니다.", "계좌 잠김", JOptionPane.ERROR_MESSAGE);
                ejectBankbook(); // 잠겼으면 통장 강제 반환
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다. 다시 입력해주세요.", "인증 실패", JOptionPane.WARNING_MESSAGE);
                ((PasswordPanel) passwordPanel).clearPassword(); // 비밀번호 필드만 초기화
            }
        }
    }

    /**
     * 관리자 메뉴를 팝업으로 보여주는 메서드
     */
    private void showAdminMenu() {
        String[] options = {"ATM 총 현금 확인", "현금 추가", "현금 회수", "전체 거래내역 확인", "취소"};
        int choice = JOptionPane.showOptionDialog(this, "관리자 메뉴를 선택하세요.", "관리자 모드",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        try {
            switch (choice) {
                case 0: // 총 현금 확인
                    long totalCash = adminService.checkAtmTotalCash();
                    JOptionPane.showMessageDialog(this, "ATM 총 현금 잔고: " + String.format("%,d", totalCash) + "원");
                    break;
                case 1: // 현금 추가
                    String addAmountStr = JOptionPane.showInputDialog(this, "추가할 금액을 입력하세요:");
                    if (addAmountStr != null) adminService.addAtmCash(Long.parseLong(addAmountStr));
                    break;
                case 2: // 현금 회수
                    String subAmountStr = JOptionPane.showInputDialog(this, "회수할 금액을 입력하세요:");
                    if (subAmountStr != null) adminService.withdrawAtmCash(Long.parseLong(subAmountStr));
                    break;
                case 3: // 전체 거래내역
                    List<String> allLogs = adminService.viewAllTransactionLogs();
                    JTextArea textArea = new JTextArea(20, 50);
                    textArea.setText(String.join("\n", allLogs));
                    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "전체 거래 내역", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AtmMainFrame frame = new AtmMainFrame();
            frame.setVisible(true);
        });
    }
}