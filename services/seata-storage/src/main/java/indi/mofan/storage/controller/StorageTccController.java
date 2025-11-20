package indi.mofan.storage.controller;

import indi.mofan.storage.tcc.StorageTccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageTccController {

    @Autowired
    private StorageTccService storageTccService;

    @GetMapping("/tcc/deduct")
    public String tccDeduct(@RequestParam("commodityCode") String commodityCode,
                            @RequestParam("count") Integer count) {
        storageTccService.prepare(null, commodityCode, count);
        return "storage tcc try success";
    }
}