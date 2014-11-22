package jetbrick.web.mvc.action.annotation;

import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.WebConfig;

public final class RequestBodyArgumentGetter implements AnnotatedArgumentGetter<RequestBody, Object> {

    private RequestBodyGetter<?> requestBodyGetter;

    @Override
    public void initialize(ArgumentContext<RequestBody> ctx) {
        requestBodyGetter = WebConfig.getRequestBodyGetterResolver().resolve(ctx.getRawParameterType());
        if (requestBodyGetter == null) {
            throw new IllegalStateException("Unable to resolve RequestBodyGetter for " + ctx.getRawParameterType());
        }
    }

    @Override
    public Object get(RequestContext ctx) throws Exception {
        return requestBodyGetter.get(ctx);
    }

}
