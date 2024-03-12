package com.netsafe.netsafe.controller;

import com.netsafe.netsafe.pojo.Result;
import com.netsafe.netsafe.utils.LogUtil;
import com.netsafe.netsafe.utils.RandomValidateCodeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Verify")
public class PicVerifyController {

    @GetMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            //设置相应类型,告诉浏览器输出的内容为图片
            response.setContentType("image/jpeg");

            //设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);

            RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();

            randomValidateCode.getRandomCode(request, response);//输出验证码图片方法

        } catch (Exception e) {

            LogUtil.LOG("获取验证码失败>>>>   " + e);

        }
    }

    @PostMapping(value = "/checkVerify", headers = "Accept=application/json")
    public Result checkVerify(@RequestParam String verifyInput, HttpSession session) {
        try {

            //从session中获取随机数
            String inputStr = verifyInput;

            String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
            LogUtil.LOG("你的验证码: "+inputStr + "你的attr:"+random);
            if (random == null || "".equals(random) || !random.equalsIgnoreCase(inputStr)) {
                return Result.error("验证码错误");
            } else {
                return Result.success();
            }

        } catch (Exception e) {
            LogUtil.LOG("验证码校验失败"+e);
            return Result.error("验证码校验失败");
        }
    }
}
