package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public class MemberPosition extends ApiResource {
  public Date created;
  public Integer dayFrom;
  public Integer dayTo;
  public Integer monthFrom;
  public Integer monthTo;
  public Integer yearTo;
  public Integer yearFrom;
  public ExpandableField<Member> member;
  public String position;
  public String unit;

  /**
   * Passed in the webhooks only. Indicates that object was deleted in cub.
   */
  public Boolean deleted;


  public static MemberPosition get(String id, Params params)
          throws CubException, UnsupportedEncodingException {

    return (MemberPosition) get(id, MemberPosition.class, params);
  }

  public static List<CubObject> list(Params params)
          throws CubException, UnsupportedEncodingException {

    return list(MemberPosition.class, params);
  }
}
