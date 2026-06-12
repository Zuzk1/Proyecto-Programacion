package com.cecyt.pomodoro;

import android.content.Context;
import android.util.TypedValue;

public class TemaUtils {

    /** Resuelve el color del atributo de tema (R.attr.theme...) para el tema actual del contexto. */
    public static int resolverColor(Context context, int attrResId) {
        TypedValue valor = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, valor, true);
        return valor.data;
    }
}
