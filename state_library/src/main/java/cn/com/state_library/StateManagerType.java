package cn.com.state_library;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by JokerWan on 2019-06-04.
 * Function: 区分三种视图
 */
@IntDef({StateManagerType.EMPTY,
        StateManagerType.ERROR,
        StateManagerType.LOADING})
@Retention(RetentionPolicy.SOURCE)
public @interface StateManagerType {
    int EMPTY = 0x000000;
    int ERROR = 0x000001;
    int LOADING = 0x000002;
}
