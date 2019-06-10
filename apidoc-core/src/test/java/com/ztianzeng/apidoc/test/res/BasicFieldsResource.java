package com.ztianzeng.apidoc.test.res;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-07 12:04
 */
public class BasicFieldsResource {


    /**
     * Operation Summary
     *
     * <p>Operation Description</p>
     *
     * @param subscriptionId
     */
    @RequestMapping("/1")
    public void getSummaryAndDescription(@RequestParam("subscriptionId") String subscriptionId) {
    }

    /**
     * parameter description
     *
     * @param subscriptionId
     */
    @RequestMapping("/2")
    public void getSummaryAndDescription2(String subscriptionId) {

    }

    @RequestMapping("/3")
    public void getSummaryAndDescription3(String subscriptionId) {
    }

    @RequestMapping("/4")
    public void getSummaryAndDescription4(String subscriptionId) {
    }

    @RequestMapping("/5")
    public void getSummaryAndDescription5(String subscriptionId) {
    }

    @RequestMapping("/6")
    public void getSummaryAndDescription6(String subscriptionId) {
    }

}