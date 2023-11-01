package com.byyw.demo.controller;

import java.io.InputStream;

import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestController {

    // dele
    @RequestMapping(value = "/", method = { RequestMethod.GET })
    public void testTest(HttpServletRequest request,
            HttpServletResponse response) {
        try {

            JSONObject jo = new JSONObject();
            jo.set("code", 0);

            response.reset();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getOutputStream().write(jo.toString().getBytes());

            InputStream is = request.getInputStream();

            while (true) {
                response.getOutputStream().write("1".getBytes());
                response.getOutputStream().flush();
                Thread.sleep(1000);
                if (2 == 3) {
                    break;
                }
            }

            System.out.println("endendend");
            // while (true) {
            // response.getOutputStream().write("1".getBytes());
            // if (3 == 2) {
            // break;
            // }
            // Thread.sleep(1000);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
