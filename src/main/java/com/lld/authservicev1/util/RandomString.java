package com.lld.authservicev1.util;

public class RandomString {
    // Fields
    private Integer ordNumLB;
    private Integer ordNumUB;

    // CTOR
    public RandomString(Integer ordNumLB, Integer ordNumUB) {
        this.ordNumLB = ordNumLB;
        this.ordNumUB = ordNumUB;
    }

    public String ofLength(Integer length) {
        String randomStr = "";
        for (int i = 0; i < length; i++)
            randomStr += (char) this.generateRandomNumber(this.ordNumLB, this.ordNumUB);
        return randomStr;
    }

    private int generateRandomNumber(Integer lb, Integer ub) {
        return (int) (Math.random() * (ub - lb + 1)) + lb;
    }
}
