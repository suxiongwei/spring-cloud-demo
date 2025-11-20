package indi.mofan.account.controller;

import indi.mofan.account.tcc.AccountTccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountTccController {

    @Autowired
    private AccountTccService accountTccService;

    @GetMapping("/tcc/debit")
    public String tccDebit(@RequestParam("userId") String userId,
                           @RequestParam("money") int money) {
        accountTccService.prepare(null, userId, money);
        return "account tcc try success";
    }
}