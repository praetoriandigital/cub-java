package com.ivelum.exceptions;

import com.ivelum.exception.BadRequestException;
import com.ivelum.model.ApiError;

/**
 * Example of exception around handling User.login BadRequest exceptions
 */
public class PasswordChangeRequiredExampleException extends BadRequestException {
  private String redirectUrl;
  
  public PasswordChangeRequiredExampleException(ApiError err) {
    super(err);
    this.redirectUrl = err.params.get("redirect");
  }
  
  /**
   * @return redirect value to the password reset page
   */
  public String getRedirectUrl() {
    return this.redirectUrl;
  }
}
