package com.ivelum.exceptions;

import com.ivelum.exception.BadRequestException;
import com.ivelum.model.ApiError;

/**
 * Example of exception around handling User.login BadRequest exceptions
 */
public class LoginErrorExampleException extends BadRequestException {
  public LoginErrorExampleException(ApiError err) {
    super(err);
  }
}
