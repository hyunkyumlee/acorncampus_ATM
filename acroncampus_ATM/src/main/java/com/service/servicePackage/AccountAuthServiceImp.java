package com.service.servicePackage;

import com.data.FileDataManager;
import com.entity.Account;
import com.service.interfacePackage.AccountAuthService;

import java.util.ArrayList;
import java.util.List;

public class AccountAuthServiceImp implements AccountAuthService {

    private List<Account> accounts;
    private final FileDataManager fileDataManager;

    public AccountAuthServiceImp(FileDataManager fileDataManager) {
        this.fileDataManager = fileDataManager;
        this.accounts = fileDataManager.loadAccounts();
        // 만약 파일에서 불러온 계좌가 없다면, 샘플 데이터로 초기화
        if (this.accounts.isEmpty()) {
            initSampleAccounts();
        } else {
            // 프로그램 시작 시 모든 계좌를 강제로 로그아웃 상태로 만듭니다.
            // 비정상 종료 등으로 isLogined가 true로 저장된 경우를 방지하기 위함입니다.
            for (Account account : this.accounts) {
                account.setLogined(false);
            }
        }
    }

    // 1.1 초기 데이터 세팅 (샘플 계좌 3~5개 생성)
    @Override
    public void initSampleAccounts() {
        // 이 메서드는 이제 파일이 없을 때 최초 데이터를 생성하는 역할
        this.accounts = new ArrayList<>();
        accounts.add(new Account("000001", "홍길동", "1234", 1900000));
        accounts.add(new Account("000002", "김철수", "1234", 656000));
        accounts.add(new Account("000003", "박영희", "1234", 45000));
        accounts.add(new Account("000004", "김덕배", "1234", 2187600));
        fileDataManager.saveAccounts(accounts); // 생성된 초기 데이터를 파일에 저장
    }

    // 1.3계좌번호로 특정 계좌 객체 찾아오기
    @Override
    public Account findAccount(String accountNo) {
        for (Account account : accounts) {
            if (account.getAccountNo().equals(accountNo)) {
                return account;
            }
        }
        return null;
    }

    // UI에서 계좌 목록을 표시하기 위해 전체 계좌 리스트를 반환
    @Override
    public List<Account> getAccounts() {
        return this.accounts;
    }

    // 로그아웃 해야 로그인 가능한 로직은 외부에서 구현
    @Override
    public boolean login(String accountNo, String password) {
        Account account = findAccount(accountNo);

        // 중복 로그인 방지
        for(Account a : accounts){
            if(a.isLogined() == true){
                System.out.println("로그아웃이 필요합니다.");
                return false;
            }
        }

        if (account == null) {
            System.out.println("계좌가 존재하지 않습니다.");
            return false;
        }
        if (account.isLocked()) {
            System.out.println("계좌가 잠겨있습니다. 해제하려면 은행을 방문해 주세요.");
            return false;
        }

        if (!account.getPassword().equals(password)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            handlePasswordError(account);
            fileDataManager.saveAccounts(accounts); // 비밀번호 오류 후 상태 저장
            return false;
        }

        System.out.println("로그인 성공");
        account.resetPasswordErrorCount();
        account.setLogined(true);
        fileDataManager.saveAccounts(accounts); // 로그인 성공 후 상태 저장
        return true;
    }

    // 3.2 로그아웃
    @Override
    public void logout() {
        for(Account account : accounts) {
            if (account.isLogined() == true) {
                System.out.println("로그아웃 하였습니다.");
                account.setLogined(false);
                fileDataManager.saveAccounts(accounts); // 로그아웃 후 상태 저장
            }
        }
    }

    // 3.3 비밀번호 오류 횟수 카운트 및 계좌 잠금
    @Override
    public void handlePasswordError(Account account) {

        account.increasePasswordErrorCount();
        System.out.println("비밀번호 오류 횟수: " + account.getPasswordErrorCount());
        if(account.getPasswordErrorCount() >= 3){
            account.setLocked(true);
            System.out.println("비밀번호 오류로 인해 계좌가 잠금되었습니다. 해제하려면 은행을 방문해 주세요.");
        }

    }
}
