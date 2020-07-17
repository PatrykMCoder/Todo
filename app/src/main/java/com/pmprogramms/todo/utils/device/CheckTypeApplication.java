package com.pmprogramms.todo.utils.device;

import com.pmprogramms.todo.BuildConfig;

public class CheckTypeApplication {
    public static boolean isDebugApp() {
        return BuildConfig.DEBUG;
    }
}
