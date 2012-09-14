/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
 */
package org.exoplatform.services.wcm.publication;

import java.util.List;

import javax.jcr.Node;

import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.navigation.NavigationContext;
import org.exoplatform.services.ecm.publication.PublicationPlugin;
import org.exoplatform.services.wcm.publication.listener.post.PostCreateContentEventListener;
import org.exoplatform.services.wcm.publication.listener.post.PostEditContentEventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Hoa Pham
 * hoa.pham@exoplatform.com
 * Sep 29, 2008
 */

/**
 * Base class of Webpage Publication plugins.
 * Webpage publication plugins implement a publication lifecycle. Each time a new
 * custom lifecycle needs to be defined, a new plugin has to be implemented
 * and registered with the Publication Service.
 */
public abstract class WebpagePublicationPlugin extends PublicationPlugin {

  /**
   * Update the lifecycle of the content depending of its current state.
   * This method is generally called when the node has been modified (saved).
   *
   * @param node the node
   * @param remoteUser the remote user
   *
   * @throws Exception the exception
   *
   * @see PostCreateContentEventListener
   * @see PostEditContentEventListener
   */
  public abstract void updateLifecyleOnChangeContent(Node node, String remoteUser) throws Exception;

  /**
   * Update the lifecycle of the content depending of its current state.
   * This method is generally called when the node has been modified (saved).
   *
   * @param node the node
   * @param remoteUser the remote user
   * @param newState the new state
   *
   * @throws Exception the exception
   *
   * @see PostCreateContentEventListener
   * @see PostEditContentEventListener
   */
  public abstract void updateLifecyleOnChangeContent(Node node, String remoteUser, String newState) throws Exception;

  /**
   * Gets the lifecycle type.
   *
   * @return the lifecycle type
   */
  public abstract String getLifecycleType();

  /**
   * Gets the list page navigation uri.
   *
   * @param page the page
   * @param remoteUser the remote user
   *
   * @return the list page navigation uri
   *
   * @throws Exception the exception
   */
  public abstract List<String> getListUserNavigationUri(Page page, String remoteUser) throws Exception;
}
