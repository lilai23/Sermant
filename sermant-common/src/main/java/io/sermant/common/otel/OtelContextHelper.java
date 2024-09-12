package io.sermant.common.otel;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-28
 */
public class OtelContextHelper {
    public static Context currentContext() {
        return Context.current();
    }

    public static Span currentSpan() {
        return Span.current();
    }

    public static Span spanFromContext(Context context) {
        return Span.fromContext(context);
    }
}
