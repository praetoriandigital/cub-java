package com.ivelum.net;

import static junit.framework.TestCase.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.ApiConnectionException;
import com.ivelum.exception.CubException;
import com.ivelum.exception.NotFoundException;
import com.ivelum.model.CubObject;
import com.ivelum.model.Organization;

import java.util.List;

import org.junit.After;
import org.junit.Test;


public class ApiResourceTest extends CubModelBaseTest  {

  static final class NonExistsModel extends ApiResource {
    static List<CubObject> list() throws CubException {
      return list(NonExistsModel.class);
    }
  }

  @Test
  public void testListNotExistsModel() throws CubException {
    try {
      NonExistsModel.list();
      fail(); // exception must be thrown
    } catch (NotFoundException e) {
      assert (e.getMessage().contains("Unknown command"));
    }
  }

  @Test(expected = ApiConnectionException.class)
  public void testConnectionException() throws CubException {
    Cub.baseUrl = "http://localhost:9999/"; // invalid cub url

    Organization.get("any_id"); // any model is ok
  }
}
