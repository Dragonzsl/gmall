package com.shilin.gulimall.member.web;

import com.alibaba.fastjson.TypeReference;
import com.shilin.common.utils.R;
import com.shilin.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-11 12:39:40
 */
@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/orderList.html")
    public String orderList(@RequestParam(value = "pageNum",defaultValue = "1") String pageNum, Model model){

        Map<String ,Object> map = new HashMap<>();
        map.put("page", pageNum);
        R r = orderFeignService.listAndItem(map);
        LinkedHashMap<String, Object> data = r.getData("page",new TypeReference<LinkedHashMap<String,Object>>() {
        });
        model.addAttribute("orders", data);
        return "orderList";
    }
}
