package com.ivelum.exception;

import com.ivelum.model.ApiError;

public class UnauthorizedException extends ApiException {
    private ApiError apiError;

    public UnauthorizedException(ApiError err) {
        super(err.description, null);
        apiError = err;
    }

    public ApiError getApiError() {
        return apiError;
    }
}
