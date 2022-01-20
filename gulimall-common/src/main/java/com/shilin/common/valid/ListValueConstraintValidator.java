package com.shilin.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-15 19:14:30
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private final Set<Integer> set = new HashSet<>();
    //初始化
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value : values) {
            set.add(value);
        }

    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
