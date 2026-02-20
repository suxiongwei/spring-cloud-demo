package indi.mofan.account.controller;

import indi.mofan.account.bean.AccountTbl;
import indi.mofan.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AccountRestController {

    @Autowired
    AccountService accountService;

    /**
     * 扣减账户余额
     */
    @GetMapping("/debit")
    public String debit(@RequestParam("userId") String userId,
                        @RequestParam("money") int money) {
        accountService.debit(userId, money);
        return "account debit success";
    }

    @GetMapping("/addBack")
    public String addBack(@RequestParam("userId") String userId,
                          @RequestParam("money") int money) {
        accountService.addBack(userId, money);
        return "account add back success";
    }

    @GetMapping("/snapshot")
    public Map<String, Object> snapshot(@RequestParam("userId") String userId) {
        AccountTbl account = accountService.snapshot(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("exists", account != null);
        if (account != null) {
            result.put("money", account.getMoney());
            result.put("id", account.getId());
        }
        return result;
    }
}
