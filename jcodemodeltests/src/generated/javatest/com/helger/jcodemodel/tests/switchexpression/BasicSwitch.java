package com.helger.jcodemodel.tests.switchexpression;

public class BasicSwitch {
    private int nullCount;

    public Number plus1(Object o) {
        return switch (o) {
            case Integer i -> 
                (i + 1);
            case Character c when (c >= '0') && (c<= '9') -> 
                ((c -'0')+ 1);
            case Character c -> 
                (c + 1);
            case null -> {
                nullCount += 1;
                yield nullCount;
            }
            default -> {
                throw new UnsupportedOperationException(("case not handled : "+ o));
            }
        }
        ;
    }
}
