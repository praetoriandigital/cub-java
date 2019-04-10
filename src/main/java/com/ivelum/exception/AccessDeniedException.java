package com.ivelum.exception;

import com.ivelum.model.ApiError;

public class AccessDeniedException extends ApiException {
  private ApiError apiError;

  public AccessDeniedException(ApiError err) {
    super(err.description, null);
    apiError = err;
  }

  public ApiError getApiError() {
    return apiError;
  }
}
