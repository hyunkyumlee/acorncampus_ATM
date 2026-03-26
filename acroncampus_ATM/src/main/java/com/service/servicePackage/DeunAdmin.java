package com.service.servicePackage;

import com.entity.Account;
import com.entity.AdminLog;
import com.entity.AtmMachine;
import com.entity.TransactionLog;
import com.service.interfacePackage.AdminService;
import com.service.servicePackage.transaction.TransactionBalanceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeunAdmin implements AdminService {

    private AtmMachine atmMachine;
    private List<Account> accounts;
    private TransactionBalanceService transactionBalanceService;

    public DeunAdmin() {
        this.atmMachine = new AtmMachine(1000000000);  // 10억
        this.accounts = new ArrayList<>();
    }

    public DeunAdmin(AtmMachine atmMachine, List<Account> accounts,
                     TransactionBalanceService transactionBalanceService) {
        this.atmMachine = atmMachine;
        this.accounts = accounts;
        this.transactionBalanceService = transactionBalanceService;
    }

    // 6.1 ATM 기기 내 총 현금 잔고 확인
    @Override
    public long checkAtmTotalCash() {   // 기계 안의 총 돈 1
        return atmMachine.getTotalCash();
    }

    // 6.2 ATM 기기에 현금 추가하기
    @Override
    public void addAtmCash(long amount) {   // 기계의 돈 채우기 2
         atmMachine.addCash(amount);
         recordAdminLog( "현금추가", amount, LocalDateTime.now());
         System.out.println("현재 ATM 총금액 : " + atmMachine.getTotalCash() + " 원");
    }

    // 6.3 ATM 기기에 현금 빼기
    @Override
    public void withdrawAtmCash(long amount) {  // 기계의 돈 빼기 3
        atmMachine.withdrawCash(amount);
        recordAdminLog( "현금회수", amount, LocalDateTime.now());
        System.out.println("현재 ATM 총금액 : " + atmMachine.getTotalCash() + " 원");
    }

    // 6.6 관리자 전용 암호 인증
    @Override
    public boolean adminLogin(String adminPassword) {   // 관리자 로그인 4
        // 비번은 1234 임
        String password = "1234";
        if (adminPassword.equals(password)){
            return true;
        } else {
            return false;
        }
    }

    private final List<AdminLog> adminLogs = new ArrayList<>();

    public List<AdminLog> getAdminLogs() {
        return adminLogs;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // 6.4 현금 추가하고 뺄 경우의 로그 기록
    @Override
    public void recordAdminLog(String type, long amount, LocalDateTime timestamp) {   // 5 관리자 로그 기계 자체에서 입출금 기록
        adminLogs.add(new AdminLog(type, amount, timestamp));
    }

    // 6.5 거래 내역 확인 - Log 에 접근하여 확인
    @Override
    public List<String> viewAllTransactionLogs() {
        // 정렬을 위해 시간과 메시지를 묶어주는 내부 클래스
        class LogEntry {
            LocalDateTime time;
            String logMessage;
            LogEntry(LocalDateTime time, String logMessage) {
                this.time = time;
                this.logMessage = logMessage;
            }
        }

        List<LogEntry> entries = new ArrayList<>();

        // 모든 계좌를 순회하며 거래 내역 수집
        for (Account account : this.accounts) {
            for (TransactionLog log : account.getAccountRecord()) {
                // 기존 서비스의 포맷 기능을 사용하여 로그 문자열 생성
                String formattedLog = transactionBalanceService.formatHistory(log);
                // 관리자용으로 [이름(계좌번호)] 정보를 앞에 추가
                String adminViewLog = String.format("[%s(%s)] %s", account.getOwnerName(), account.getAccountNo(), formattedLog);
                entries.add(new LogEntry(log.getTimestamp(), adminViewLog));
            }
        }

        // 모든 거래 내역을 시간순(오름차순)으로 정렬
        entries.sort(Comparator.comparing(e -> e.time));

        // 정렬된 결과에서 메시지만 추출하여 리스트로 반환
        List<String> allLogs = new ArrayList<>();
        for (LogEntry e : entries){
            allLogs.add(e.logMessage);
        }
        return allLogs;
    }


}
