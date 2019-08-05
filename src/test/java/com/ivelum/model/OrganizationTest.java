package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.NotFoundException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class OrganizationTest extends CubModelBaseTest {

  @Test
  public void testRead() throws CubException {
    String objUrl = ApiResource.getInstanceUrl(
        ApiResource.getInstanceName(Organization.class), "org_123");

    setGetMock(objUrl, "organization", 200, Cub.apiKey);
    Organization org = Organization.get("org_123");

    assertEquals("org_123", org.id);
    assertEquals("name", org.name);
    assertFalse(org.moderatorApproved);
    assertNotNull(org.created);
    assertNotNull(org.modified);
    assertEquals(1, org.tags.size());
    assertTrue(org.tags.contains("EMS"));
    assertEquals("website", org.website);
    assertFalse(org.state.isExpanded());
    assertEquals("stt_123", org.state.getId());
    assertEquals("phone", org.phone);
    assertEquals("fax", org.fax);
    assertEquals("employees", org.employees);
    assertEquals("email", org.email);
    assertEquals("county", org.county);
    assertEquals("city", org.city);
    assertEquals("logo", org.logo);
    assertEquals("address", org.address);
    assertEquals("hrPhone", org.hrPhone);
    assertEquals("postalCode", org.postalCode);
    assertFalse(org.country.isExpanded());
    assertEquals("cry_123", org.country.getId());
    assertNull(org.deleted);
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String orgJson = getFixture("organization_deleted");

    Organization org = (Organization) Cub.factory.fromString(orgJson);
    assertTrue(org.deleted);
  }

  @Test
  public void testNotFound() {
    try {
      Organization.get("non_exists_id");
      // exception expected
      fail();
    } catch (NotFoundException e) {
      assertTrue(e.getMessage().contains("Organization id=non_exists_id not found"));
    } catch (CubException e) {
      e.printStackTrace();
      fail(); // unexpected
    }
  }


  @Test
  public void testStateExpandableIdOnly() throws CubException {
    List<CubObject> organizations = Organization.list();
    String orgId = organizations.get(0).id;
    Params params = new Params();
    params.setExpands("state", "country", "state__country");
    Organization org = Organization.get(orgId, params);
    assertNotNull(org.state);
    assertNotNull(org.state.getId());
    assertNotNull(org.state.getExpanded());

    State state = org.state.getExpanded();

    assertNotNull(state.country);
    assertNotNull(state.country.getId());
    assertNotNull(state.country.getExpanded());

    assertNotNull(org.country);
    assertNotNull(org.country.getId());
    assertNotNull(org.country.getExpanded());
  }

  @Test
  public void testListOrganizations() throws CubException {
    List<CubObject> organizations = Organization.list();
    List<String> ids = new LinkedList<>();
    for (CubObject item : organizations) {
      ids.add(((Organization) item).id);
    }

    Params params = new Params();
    params.setOffset(20).setCount(5);
    List<CubObject> anotherPage = Organization.list(params);

    assertEquals(5, anotherPage.size());

    for (CubObject item : anotherPage) {
      assertFalse(ids.contains(((Organization) item).id));
    }
  }
  
  @Test
  public void testCreateOrganization() throws CubException {
    Organization organizationFixture = (Organization) Cub.factory.fromString(
        getFixture("organization"));
    String apiKey = "apiKey";
    String endpoint = String.format("/%s/register", User.classUrl);
    mockPostToListEndpoint(Organization.class, 200, "organization", apiKey);
    
    Organization org = new Organization();
    org.name = organizationFixture.name;
    org.address = organizationFixture.address;
    org.postalCode = organizationFixture.postalCode;
    if (organizationFixture.country.getId() != null) {
      org.country = new ExpandableField<>(organizationFixture.country.getId());
    }
    
    if (organizationFixture.state.getId() != null) {
      org.state = new ExpandableField<>(organizationFixture.state.getId());
    }
    org.employees = organizationFixture.employees; // you can find posssible values in lexipol admin
    org.phone = organizationFixture.phone; // you can find posssible values in lexipol admin
    org.city = organizationFixture.city;
    // Test saving withouot tags
    org.save(new Params(apiKey));// if you want to use default key just do not pass params
    // emulate new organization object
    org.id = null; // reset id
    org.tags = organizationFixture.tags;
    org.save(new Params(apiKey));// save as new object
    assertNotNull(org.id);
    assertEquals(organizationFixture.name, org.name);
    assertEquals(organizationFixture.postalCode, org.postalCode);
  }
}
