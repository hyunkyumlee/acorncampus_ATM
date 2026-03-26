package com.service.servicePackage;

import com.entity.AtmMachine;

public class DeunAdminTest {

    public static void main(String[] args){

        // 생성자인데, 매개변수를 받지 않아서 새 Atm기계(객체)를 만드는 것을 막음
        DeunAdmin admin = new DeunAdmin();

        // 1번 메서드 돈 조회하기
        long result = admin.checkAtmTotalCash();
        System.out.println(result);
        System.out.println();

        // 2번 돈추가
        long temp = 200000;
        admin.addAtmCash(temp);
        System.out.println("추가한 금액 : " + temp + " 원");
        System.out.println();

        // 3번 돈회수
        long temp2 = 200000000;
        admin.withdrawAtmCash(temp2);
        System.out.println("회수한 금액 : " + temp2 + " 원");
        System.out.println();

        // 6번 관리자 로그인


        // 4번 관리자 로그 확인
        

        // 5번 (영석 같이) 입출금 로그




    }
}
