/***************************************************************************
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 *
 **************************************************************************/
package org.exoplatform.ecm.webui.component.admin.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.jcr.Node;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.ListAccessImpl;
import org.exoplatform.ecm.webui.core.UIPagingGrid;
import org.exoplatform.services.cms.views.ManageViewService;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minh.dang@exoplatform.com
 * Feb 19, 2013
 * 4:26:03 PM  
 */

@ComponentConfig(
        template = "classpath:groovy/ecm/webui/core/UIGrid.gtmpl",
        events = {
            @EventConfig(listeners = UIViewPermissionList.DeleteActionListener.class, confirm = "UIViewPermissionList.msg.confirm-delete")
        }
    )
public class UIViewPermissionList extends UIPagingGrid {

  public static String[] PERMISSION_BEAN_FIELD = {"friendlyPermission"} ;
  public static String[] PERMISSION_ACTION = {"Delete"} ;
  
  private String viewName;
  
  public UIViewPermissionList() throws Exception {
    getUIPageIterator().setId("PermissionListPageIterator") ;
    configure("permission", PERMISSION_BEAN_FIELD, PERMISSION_ACTION) ;
  }

  public String getViewName() {
    return viewName;
  }
  
  public void setViewName(String name) {
    viewName = name;
  }  
  
  public String getFriendlyPermission(String permission) throws Exception {
    RequestContext context = RequestContext.getCurrentInstance();
    ResourceBundle res = context.getApplicationResourceBundle();
    String permissionLabel = res.getString(getId() + ".label.permission");
    if(permission.indexOf(":") > -1) {
      String[] arr = permission.split(":");
      if(arr[0].equals("*")) {
        permissionLabel = permissionLabel.replace("{0}", "Any");
      } else {
        permissionLabel = permissionLabel.replace("{0}", standardizeGroupName(arr[0]));
      }
      String groupName = arr[1];
      groupName = groupName.substring(groupName.lastIndexOf("/")+1); 
      permissionLabel = permissionLabel.replace("{1}", standardizeGroupName(groupName));
    } else {
      permissionLabel = standardizeGroupName(permission);
    }
    return permissionLabel;
  }
  
  @Override
  public void refresh(int currentPage) throws Exception {
    ManageViewService viewService = WCMCoreUtils.getService(ManageViewService.class);
    Node viewNode = viewService.getViewByName(viewName, WCMCoreUtils.getUserSessionProvider());
    List<PermissionBean> permissions = new ArrayList<PermissionBean>();
    String strPermission = viewNode.getProperty("exo:accessPermissions").getString();
    String[] arrPers = new String[] {};
    if(strPermission.contains(",")) {
      arrPers = strPermission.split(",");
    } else {
      arrPers = new String[] {strPermission};
    }
    for(String per : arrPers) {
      PermissionBean bean = new PermissionBean();
      bean.setPermission(per);
      bean.setFriendlyPermission(getFriendlyPermission(per));
      permissions.add(bean);
    }
    Collections.sort(permissions, new ViewPermissionComparator());
    ListAccess<PermissionBean> permissionBeanList = new ListAccessImpl<PermissionBean>(PermissionBean.class, permissions);
    getUIPageIterator().setPageList(new LazyPageList<PermissionBean>(permissionBeanList, getUIPageIterator().getItemsPerPage()));
    getUIPageIterator().setTotalItems(permissions.size());
    if (currentPage > getUIPageIterator().getAvailablePage()) {
      getUIPageIterator().setCurrentPage(getUIPageIterator().getAvailablePage());
    } else {
      getUIPageIterator().setCurrentPage(currentPage);
    }
  }
  
  static  public class DeleteActionListener extends EventListener<UIViewPermissionList> {
    public void execute(Event<UIViewPermissionList> event) throws Exception {
      UIViewPermissionList uiPermissionList = event.getSource();
      ManageViewService viewService = WCMCoreUtils.getService(ManageViewService.class);
      String permission = event.getRequestContext().getRequestParameter(OBJECTID);
      Node viewNode = viewService.getViewByName(uiPermissionList.getViewName(), WCMCoreUtils.getUserSessionProvider());
      String strPermission = viewNode.getProperty("exo:accessPermissions").getString();
      StringBuilder perBuilder = new StringBuilder();
      if(strPermission.indexOf(",") > -1) {
        String[] arrPer = strPermission.split(",");
        for(String per : arrPer) {
          if(per.equals(permission)) continue;
          if(perBuilder.length() > 0) perBuilder.append(",");
          perBuilder.append(per);
        }
      }
      viewNode.setProperty("exo:accessPermissions", perBuilder.toString());
      viewNode.save();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPermissionList.getParent());
    }
  }  
  
  static public class ViewPermissionComparator implements Comparator<PermissionBean> {
    public int compare(PermissionBean t1, PermissionBean t2) throws ClassCastException {
      String per1 = t1.getPermission();
      String per2 = t2.getPermission();
      return per1.compareToIgnoreCase(per2);
    }
  }   
  
  public static class PermissionBean {
    
    private String permssion ;
    private String friendlyPermission;

    public PermissionBean() {}

    public String getPermission(){ return this.permssion ; }
    public void setPermission( String permission) { this.permssion = permission ; }

    public String getFriendlyPermission() { return friendlyPermission; }
    public void setFriendlyPermission(String friendlyPer) { this.friendlyPermission = friendlyPer; }
  }
  
  public String standardizeGroupName(String groupName) throws Exception {
    groupName = groupName.replaceAll("-", " ");
    char[] stringArray = groupName.toCharArray();
    stringArray[0] = Character.toUpperCase(stringArray[0]);
    groupName = new String(stringArray);
    return groupName;
  }

}
