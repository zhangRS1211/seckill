package com.zhang.seckill.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 校验工具类
 * @Author zcode
 * @Data 2023/7/28 21:07
 */
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");

    public static boolean isMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
        return false;
    }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
