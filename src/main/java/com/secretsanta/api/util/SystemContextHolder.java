package com.secretsanta.api.util;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import com.secretsanta.api.model.SystemContext;

public class SystemContextHolder {

    private static final ThreadLocal<SystemContext> systemContextHolder = new NamedThreadLocal<>("SystemContext");

    private SystemContextHolder() {
    }

    public static void resetSystemContext() {
        systemContextHolder.remove();
    }

    /**
     * Associate the given SystemContext with the current thread,
     */
    public static void setSystemContext(@Nullable SystemContext SystemContext) {
        systemContextHolder.set(SystemContext);
    }

    /**
     * Return the SystemContext associated with the current thread, if any.
     * @return the current SystemContext, or {@code null} if none
     */
    @Nullable
    public static SystemContext getSystemContext() {
        return systemContextHolder.get();
    }
    
    public static String getSchema() {
        return getSystemContext().getSchema();
    }
    
    public static int getCurrentYear() {
        return getSystemContext().getCurrentYear();
    }

}
