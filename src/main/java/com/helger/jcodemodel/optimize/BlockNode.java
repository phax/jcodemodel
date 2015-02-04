/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel.optimize;

import java.util.ArrayList;
import java.util.List;

import com.helger.jcodemodel.JBlock;

final class BlockNode implements Comparable <BlockNode>
{
  static BlockNode root (final JBlock block)
  {
    return new BlockNode (block, null, 0);
  }

  final JBlock _block;
  private final BlockNode _parent;
  private final int _index;
  private List <BlockNode> _children;

  private BlockNode (final JBlock block, final BlockNode parent, final int index)
  {
    _block = block;
    _parent = parent;
    _index = index;
  }

  private BlockNode (final BlockNode parent, final JBlock block)
  {
    _block = block;
    _parent = parent;
    if (parent._children == null)
      parent._children = new ArrayList <BlockNode> (5);
    parent._children.add (this);
    _index = parent._children.size ();
  }

  BlockNode child (final JBlock block)
  {
    return new BlockNode (this, block);
  }

  public int compareTo (final BlockNode o)
  {
    if (this == o)
      return 0;
    if (this.isChildOf (o))
      return 1;
    if (o.isChildOf (this))
      return -1;
    if (_parent == o._parent)
      return _index - o._index;
    return _parent.compareTo (o._parent);
  }

  private boolean isChildOf (final BlockNode o)
  {
    BlockNode parent = _parent;
    while (parent != null)
    {
      if (parent == o)
        return true;
      parent = parent._parent;
    }
    return false;
  }
}
