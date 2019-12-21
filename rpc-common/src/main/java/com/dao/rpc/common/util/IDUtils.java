package com.dao.rpc.common.util;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * @author terry
 * @date 18-5-25
 */
public class IDUtils {

    /**
     * 10
     */
    private static final int TEN = 10;

    /**
     * 100
     */
    private static final int HUNDRED = 100;

    /**
     * 上一次时间戳
     */
    private static long lastTimeMill = -1L;

    /**
     * 序列号
     */
    private static long sequence = 0L;

    /**
     * 最大序列号
     */
    private static final int MAX_SEQUENCE = 999;

    /**
     * 生成唯一Id
     *
     * @return 唯一Id
     */
    public static String nextId() {

        long currentTimeMill;

        long currentSequence;

        String sequenceStr;

        synchronized (IDUtils.class) {

            currentTimeMill = currentTimeMill();

            if (currentTimeMill == lastTimeMill) {
                if (sequence >= MAX_SEQUENCE) {
                    sequence = 0;
                    currentTimeMill = nextTimeMill();
                } else {
                    sequence += 1;
                }
            } else {
                sequence = 0L;
            }

            currentSequence = sequence;

            lastTimeMill = currentTimeMill;
        }

        if (currentSequence < TEN) {
            sequenceStr = String.format("00%d", currentSequence);
        } else if (currentSequence < HUNDRED) {
            sequenceStr = String.format("0%d", currentSequence);
        } else {
            sequenceStr = String.valueOf(currentSequence);
        }

        int machineId = ThreadLocalRandom.current().nextInt(TEN);

        return String.format("%d%s%d", currentTimeMill, sequenceStr, machineId).substring(2);
    }

    /**
     * 当前timeMill
     *RegexUtils
     * @return
     */
    private static long currentTimeMill() {
        String now = LocalDateTime.now().toString();
        String replaceAll = getReplaceAll(now, "[^\\d]+", "");
        return Long.valueOf(replaceAll);
    }

    private static String getReplaceAll(final String input,
                                       final String regex,
                                       final String replacement) {
        if (input == null) return "";
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }

    /**
     * 下一个timeMill
     *
     * @return
     */
    private static long nextTimeMill() {
        long curTimeMill = currentTimeMill();
        while (curTimeMill <= lastTimeMill) {
            curTimeMill = currentTimeMill();
        }
        return curTimeMill;
    }


}
