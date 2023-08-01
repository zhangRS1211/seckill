package com.zhang.seckill.util;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 10:57
 */

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author: LC
 * @date 2022/3/2 5:46 下午
 * @ClassName: UUIDUtil
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
