package com.service.servicePackage.transaction;

import com.entity.Account;
import com.entity.AtmMachine;
import com.entity.TransactionLog;
import com.service.interfacePackage.TransactionService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionBalanceService implements TransactionService {
    private final ArrayList<Account> accountList = new ArrayList<>();
    private AtmMachine atmMachine;
    private static final DateTimeFormatter HISTORY_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TransactionBalanceService() {
    }

    public TransactionBalanceService(Account account) {
        this.accountList.add(account);
    }

    public TransactionBalanceService(ArrayList<Account> accountList) {
        this.accountList.addAll(accountList);
    }

    public TransactionBalanceService(ArrayList<Account> accountList, AtmMachine atmMachine) {
        this.accountList.addAll(accountList);
        this.atmMachine = atmMachine;
    }

    public void addAccount(Account account) {
        this.accountList.add(account);
    }

    // 계좌번호로 계좌 조회
    private Account findAccount(String accountNo) {
        for (Account account : accountList) {
            if (account.getAccountNo().equals(accountNo)) {
                return account;
            }
        }
        return null;
    }

    // 잔액이 0보다 작아지면 출금 또는 이체를 중단
    private void validateBalance(Account account) throws Exception {
        if (account.getBalance() < 0) {
            throw new Exception("잔액이 부족합니다.");
        }
    }

    // 거래 시점의 계좌 상태를 로그 객체로 저장
    private void recordTransaction(Account account, String type, long amount) {
        TransactionLog transactionLog = new TransactionLog(
                account.getAccountNo(),
                type,
                amount,
                account.getBalance()
        );
        account.addTransactionLog(transactionLog);
    }

    // 거래 유형을 사용자 표시 문자열로 변환
    public String formatTransactionType(String type) {
        if (type.equals("recordDeposit")) {
            return "입금";
        } else if (type.equals("recordWithdraw")) {
            return "출금";
        } else if (type.equals("recordTransferPlus")) {
            return "이체받음";
        } else if (type.equals("recordTransferMinus")) {
            return "이체보냄";
        }
        return type;
    }

    // 로그 1건을 반환용 문자열로 변환
    public String formatHistory(TransactionLog transactionLog) {
        String amountPrefix =
                (transactionLog.getTransactionType().equals("recordWithdraw")
                        || transactionLog.getTransactionType().equals("recordTransferMinus")) ? "-" : "+";

        return transactionLog.getTimestamp().format(HISTORY_TIME_FORMAT)
                + " | "
                + formatTransactionType(transactionLog.getTransactionType())
                + " | "
                + amountPrefix + transactionLog.getAmount()
                + " | 잔액: "
                + transactionLog.getBalanceAfter();
    }

    // 2.1 현재 잔액 확인
    @Override
    public long checkBalance(String accountNo) {
        Account account = findAccount(accountNo);
        return account.getBalance();
    }

    // 2.2 현금 입금
    @Override
    public void deposit(String accountNo, long amount) {
        Account account = findAccount(accountNo);
        if (amount % 1000 == 0) {
            account.setBalance(account.getBalance() + amount);
            atmMachine.addCash(amount);
            recordTransaction(account, "recordDeposit", amount);
        }
    }

    // 2.3 현금 출금 (잔액 차감 및 단위 제한(2.4), 부족 시 제한)
    @Override
    public void withdraw(String accountNo, long amount) throws Exception {
        Account account = findAccount(accountNo);
        if (amount % 10000 == 0) {
            account.setBalance(account.getBalance() - amount);
            validateBalance(account);
            atmMachine.withdrawCash(amount);
            recordTransaction(account, "recordWithdraw", amount);
        }
    }

    // 4.1 ,4.2 보내는 계좌 차감 + 받는 계좌 합산, 없는 계좌 체크
    @Override
    public void transfer(String fromAccountNo, String toAccountNo, long amount) throws Exception {
        Account fromAccount = findAccount(fromAccountNo);
        Account toAccount = findAccount(toAccountNo);

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        validateBalance(fromAccount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        recordTransaction(fromAccount, "recordTransferMinus", amount);
        recordTransaction(toAccount, "recordTransferPlus", amount);
    }

    // 5.1 입금/출금/이체 발생 시 시간, 금액, 거래 유형 기록
    @Override
    public void recordTransaction(String accountNo, String type, long amount) {
        Account account = findAccount(accountNo);
        recordTransaction(account, type, amount);
    }

    // 5.2 최근 거래 내역 리스트 보기
    @Override
    public List<String> getRecentHistory(String accountNo) {
        Account account = findAccount(accountNo);
        List<String> recentHistory = new ArrayList<>();

        if (account == null) {
            return recentHistory;
        }

        List<TransactionLog> accountRecord = account.getAccountRecord();

        // 최신 기록이 먼저 오도록 뒤에서부터 역순으로 반환용 리스트 생성
        for (int i = accountRecord.size() - 1; i >= 0; i--) {
            recentHistory.add(formatHistory(accountRecord.get(i)));
        }

        return recentHistory;
    }
}
