package com.college.admission.security;

import com.college.admission.annotation.AdminOnly;
import com.college.admission.exception.AuthorizationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class SecurityMethodInvoker {

    private SecurityMethodInvoker() {
    }


    // ============================================================
    // INVOKE METHOD WITH SECURITY CHECK
    // ============================================================

    public static Object invoke(
            Object target,
            String methodName,
            Class<?>[] parameterTypes,
            Object[] arguments
    ) throws Exception {

        if (target == null) {

            throw new IllegalArgumentException(
                    "Target object cannot be null."
            );
        }

        if (methodName == null
                || methodName.isBlank()) {

            throw new IllegalArgumentException(
                    "Method name cannot be empty."
            );
        }

        Method method =
                target.getClass().getMethod(
                        methodName,
                        parameterTypes
                );

        // --------------------------------------------------------
        // CHECK @AdminOnly
        // --------------------------------------------------------

        if (method.isAnnotationPresent(
                AdminOnly.class
        )) {

            SecurityManager
                    .getInstance()
                    .getAuthorizationService()
                    .requireAdmin();
        }

        try {

            return method.invoke(
                    target,
                    arguments
            );

        } catch (InvocationTargetException e) {

            Throwable cause =
                    e.getCause();

            if (cause instanceof Exception) {

                throw (Exception) cause;
            }

            throw e;
        }
    }
}