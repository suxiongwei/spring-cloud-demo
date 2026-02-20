package indi.mofan.storage.controller;


import indi.mofan.storage.bean.StorageTbl;
import indi.mofan.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StorageRestController {

    @Autowired
    StorageService storageService;

    @GetMapping("/deduct")
    public String deduct(@RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") Integer count) {

        storageService.deduct(commodityCode, count);
        return "storage deduct success";
    }

    @GetMapping("/addBack")
    public String addBack(@RequestParam("commodityCode") String commodityCode,
                          @RequestParam("count") Integer count) {
        storageService.addBack(commodityCode, count);
        return "storage add back success";
    }

    @GetMapping("/snapshot")
    public Map<String, Object> snapshot(@RequestParam("commodityCode") String commodityCode) {
        StorageTbl storage = storageService.snapshot(commodityCode);
        Map<String, Object> result = new HashMap<>();
        result.put("commodityCode", commodityCode);
        result.put("exists", storage != null);
        if (storage != null) {
            result.put("count", storage.getCount());
            result.put("id", storage.getId());
        }
        return result;
    }
}
