package com.ivelum.exception;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ivelum.model.ApiError;
import com.ivelum.model.ApiErrorResponse;

public class ApiException extends CubException {
  private static final Gson gson = new Gson();

  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public static ApiException fromJson(JsonElement e) throws CubRuntimeException {
    ApiErrorResponse errResponse = gson.fromJson(e, ApiErrorResponse.class);

    ApiError error = errResponse.error;

    if (error == null) {
      throw new CubRuntimeException("Unhandled error response without error object", null);
    }

    if (error.code == null) {
      throw new CubRuntimeException("Unhandled error response without code", null);
    }

    if (error.code == 404) {
      return new NotFoundException(error.description, null);
    }

    if (error.code == 400) {
      return new BadRequestException(errResponse.error);
    }
    return new ApiException(error.description, null);
  }
}
