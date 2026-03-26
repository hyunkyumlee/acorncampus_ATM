package com.service.interfacePackage;

import com.entity.Account;

import java.util.List;

public interface AccountAuthService {
    // 1. 계좌/고객 데이터 관리 [cite: 7]
    //1.2 계좌정보 초기 데이터를 세팅해야됌
    void initSampleAccounts(); // 1.1 초기 데이터 세팅 (샘플 계좌 3~5개 생성) [cite: 9]
    Account findAccount(String accountNo); // 1.3계좌번호로 특정 계좌 객체 찾아오기
    List<Account> getAccounts(); // 전체 계좌 목록 가져오기 (UI 연동용)

    // 3. 사용자 인증 및 보안 [cite: 16]
    boolean login(String accountNo, String password); // 3.1 로그인 체크 [cite: 17]
    void logout(); // 3.2 로그아웃 및 세션 종료 [cite: 18]
    void handlePasswordError(Account account); // 3.3 비밀번호 오류 횟수 카운트 및 계좌 잠금 [cite: 19]
}
