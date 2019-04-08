package com.ivelum.exception;

import com.ivelum.model.ApiError;

public class BadRequestException extends ApiException {
  private ApiError apiError;

  public BadRequestException(ApiError err) {
    super(err.description, null);
    apiError = err;
  }

  public ApiError getApiError() {
    return apiError;
  }
}
