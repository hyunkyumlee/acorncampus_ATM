package com.service.servicePackage;

import com.entity.AdminLog;
import com.entity.AtmMachine;
import com.service.interfacePackage.AdminService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeunAdmin implements AdminService {

    private AtmMachine atmMachine = new AtmMachine(1000000000);  //10억

    public DeunAdmin(AtmMachine atmMachine) {
        this.atmMachine = atmMachine;
    }

    public DeunAdmin() {

    }

    @Override
    public long checkAtmTotalCash() {   // 기계 안의 총 돈 1
        return atmMachine.getTotalCash();
    }

    // 돈은 10,000원 단위

    @Override
    public void addAtmCash(long amount) {   // 기계의 돈 채우기 2
         atmMachine.addCash(amount);
         recordAdminLog( "현금추가", amount, LocalDateTime.now());
         System.out.println("현재 ATM 총금액 : " + atmMachine.getTotalCash() + " 원");
    }

    @Override
    public void withdrawAtmCash(long amount) {  // 기계의 돈 빼기 3
        atmMachine.withdrawCash(amount);
        recordAdminLog( "현금회수", amount, LocalDateTime.now());
        System.out.println("현재 ATM 총금액 : " + atmMachine.getTotalCash() + " 원");
    }

    @Override
    public boolean adminLogin(String adminPassword) {   // 관리자 로그인 6
        // 비번은 1234 임
        String password = "1234";
        if (adminPassword.equals(password)){
            return true;
        } else {
            return false;
        }
    }

    private final List<AdminLog> adminLogs = new ArrayList<>();

    @Override
    public void recordAdminLog(String type, long amount, LocalDateTime timestamp) {   // 4 관리자 로그 기계 자체에서 입출금 기록
        adminLogs.add(new AdminLog(type, amount, timestamp));
    }

    @Override
    public List<String> viewAllTransactionLogs() {  // 영석 같이 5, 기계 자체 거래 내역 확인
        return List.of();
    }



}
