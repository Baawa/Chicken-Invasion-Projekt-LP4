package com.chicken.invasion.Enemy_Throwable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albin on 2016-08-06.
 */
public interface ThrowableCollection {
    <T extends Throwable> ArrayList<T> getThrowables();
}