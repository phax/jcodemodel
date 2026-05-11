package com.helger.jcodemodel.tests.switchexpression;

public class ESwitch {

    public static Number daysIn(Object o) {
        return switch (o) {
            case EnumMonths.JAN, EnumMonths.MAR -> 
                31;
            case EnumMonths.FEB -> 
                28;
            case YEAR -> 
                365;
            case WEEK -> 
                7;
            case MONTH -> 
                throw new UnsupportedOperationException("a month can have 28, 30 or 31 days.");
            case null, default -> {
                throw new UnsupportedOperationException();
            }
        }
        ;
    }
}
