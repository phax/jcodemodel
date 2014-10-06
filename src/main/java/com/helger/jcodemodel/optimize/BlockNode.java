package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.JBlock;

import java.util.ArrayList;
import java.util.List;

final class BlockNode implements Comparable<BlockNode>
{

  static BlockNode root(JBlock block)
  {
    return new BlockNode (block, null, 0);
  }

  final JBlock block;
  private final BlockNode parent;
  private final int index;
  private List<BlockNode> children;

  private BlockNode (JBlock block, BlockNode parent, int index)
  {
    this.block = block;
    this.parent = parent;
    this.index = index;
  }

  private BlockNode (BlockNode parent, JBlock block)
  {
    this.block = block;
    this.parent = parent;
    if (parent.children == null)
      parent.children = new ArrayList<BlockNode> (5);
    parent.children.add (this);
    index = parent.children.size ();
  }

  BlockNode child (JBlock block)
  {
    return new BlockNode (this, block);
  }

  public int compareTo (BlockNode o)
  {
    if (this == o)
      return 0;
    if (this.isChildOf (o))
      return 1;
    if (o.isChildOf (this))
      return -1;
    if (this.parent == o.parent)
      return this.index - o.index;
    return this.parent.compareTo (o.parent);
  }

  private boolean isChildOf (BlockNode o)
  {
    BlockNode parent = this.parent;
    while (parent != null)
    {
      if (parent == o)
        return true;
      parent = parent.parent;
    }
    return false;
  }
}
