package com.helger.jcodemodel.switchexpression;

import java.util.Iterator;
import java.util.List;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JThrow;
import com.helger.jcodemodel.JYield;

///
/// represents a list of blocks to which add statements, with syntaxic sugar for yield. Made specifically for switch (and yield)
public interface BlockSelection<Self extends BlockSelection<?>> extends Iterable<JBlock> {

  List<JBlock> blocks();

  /// cast this to the template Self type
  @SuppressWarnings("unchecked")
  default Self selfThis() {
    return (Self) this;
  }

  @Override
  default Iterator<JBlock> iterator() {
    return blocks().iterator();
  }

  default Self add(IJStatement stt) {
    for (JBlock jb : this) {
      jb.add(stt);
    }
    return selfThis();
  }

  default Self _throws(JCodeModel owner, Class<? extends Throwable> clazz, IJExpression... params) {
    return _throws(owner.ref(clazz), params);
  }

  /// create a new throw
  default Self _throws(AbstractJType t, IJExpression... params) {
    JInvocation _new = JExpr._new(t);
    if (params != null) {
      for (IJExpression ije : params) {
        _new.arg(ije);
      }
    }
    add(new JThrow(_new));
    return selfThis();
  }

  default Self yield(IJExpression exp) {
    add(new JYield(exp));
    return selfThis();
  }

}
