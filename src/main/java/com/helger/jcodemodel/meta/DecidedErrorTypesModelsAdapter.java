/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.helger.jcodemodel.meta;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;

/**
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class DecidedErrorTypesModelsAdapter
{
  private static final Logger logger = Logger.getLogger (DecidedErrorTypesModelsAdapter.class.getName ());

  static int toJMod (final Collection <Modifier> modifierCollection)
  {
    int modifiers = 0;
    for (final Modifier modifier : modifierCollection)
    {
      switch (modifier)
      {
        case ABSTRACT:
          modifiers |= JMod.ABSTRACT;
          break;
        case FINAL:
          modifiers |= JMod.FINAL;
          break;
        case NATIVE:
          modifiers |= JMod.NATIVE;
          break;
        case PRIVATE:
          modifiers |= JMod.PRIVATE;
          break;
        case PROTECTED:
          modifiers |= JMod.PROTECTED;
          break;
        case PUBLIC:
          modifiers |= JMod.PUBLIC;
          break;
        case STATIC:
          modifiers |= JMod.STATIC;
          break;
        case SYNCHRONIZED:
          modifiers |= JMod.SYNCHRONIZED;
          break;
        case TRANSIENT:
          modifiers |= JMod.TRANSIENT;
          break;
        case VOLATILE:
          modifiers |= JMod.VOLATILE;
          break;
        default:
          logger.log (Level.WARNING, "Skpping unsupported modifier: {0}", modifier);
      }
    }
    return modifiers;
  }

  private static EClassType toClassType (final ElementKind kind)
  {
    switch (kind)
    {
      case CLASS:
        return EClassType.CLASS;
      case ENUM:
        return EClassType.ENUM;
      case INTERFACE:
        return EClassType.INTERFACE;
      case ANNOTATION_TYPE:
        return EClassType.ANNOTATION_TYPE_DECL;
      default:
        throw new UnsupportedOperationException ("Unsupported ElementKind: " + kind);
    }
  }

  private final Elements _elementUtils;
  private final ErrorTypePolicy _errorTypePolicy;
  private final JCodeModel _codeModel;

  DecidedErrorTypesModelsAdapter (final JCodeModel codeModel,
                                  final Elements elementUtils,
                                  final ErrorTypePolicy errorTypePolicy)
  {
    this._elementUtils = elementUtils;
    this._errorTypePolicy = errorTypePolicy;
    this._codeModel = codeModel;
  }

  public JDefinedClass getClass (final TypeElement element) throws CodeModelBuildingException, ErrorTypeFound
  {
    final Element enclosingElement = element.getEnclosingElement ();
    if (enclosingElement instanceof PackageElement)
    {
      final PackageElement packageElement = (PackageElement) enclosingElement;
      final JPackage jpackage = _codeModel._package (packageElement.getQualifiedName ().toString ());
      final JDefinedClass result = jpackage._getClass (element.getSimpleName ().toString ());
      if (result != null)
        return result;

      final JDefinedClass jclass = defineClass (element);
      jclass.hide ();
      return jclass;
    }
    else
      if (enclosingElement instanceof TypeElement)
      {
        final JDefinedClass enclosingClass = getClass ((TypeElement) enclosingElement);
        for (final JDefinedClass innerClass : enclosingClass.classes ())
        {
          final String fullName = innerClass.fullName ();
          if (fullName != null && fullName.equals (element.getQualifiedName ().toString ()))
          {
            return innerClass;
          }
        }
        throw new IllegalStateException (MessageFormat.format ("Inner class should always be defined if outer class is defined: inner class {0}, enclosing class {1}",
                                                               element,
                                                               enclosingClass));
      }
      else
        throw new IllegalStateException ("Enclosing element should be package or class");
  }

  private JDefinedClass defineClass (final TypeElement element) throws CodeModelBuildingException, ErrorTypeFound
  {
    final Element enclosingElement = element.getEnclosingElement ();
    if (enclosingElement instanceof PackageElement)
    {
      PackageElement packageElement = (PackageElement) enclosingElement;
      return defineTopLevelClass (element, new TypeEnvironment (packageElement.getQualifiedName ().toString ()));
    }

    // Only top-level classes can be directly defined
    return getClass (element);
  }

  private JDefinedClass defineTopLevelClass (final TypeElement element,
                                             final TypeEnvironment environment) throws CodeModelBuildingException,
                                                                                ErrorTypeFound
  {
    final EClassType classType = toClassType (element.getKind ());
    int modifiers = toJMod (element.getModifiers ());
    if (classType.equals (EClassType.INTERFACE))
    {
      modifiers &= ~JMod.ABSTRACT;
      modifiers &= ~JMod.STATIC;
    }
    final Element enclosingElement = element.getEnclosingElement ();
    if (!(enclosingElement instanceof PackageElement))
    {
      throw new IllegalStateException ("Expecting top level class");
    }
    final PackageElement packageElement = (PackageElement) enclosingElement;
    final JPackage _package = _codeModel._package (packageElement.getQualifiedName ().toString ());
    JDefinedClass newClass;
    try
    {
      newClass = _package._class (modifiers, element.getSimpleName ().toString (), classType);
    }
    catch (final JClassAlreadyExistsException ex)
    {
      throw new CodeModelBuildingException (ex);
    }
    declareInnerClasses (newClass, element, environment);
    final ClassFiller filler = new ClassFiller (_codeModel, this, newClass);
    filler.fillClass (element, environment);
    return newClass;
  }

  private void declareInnerClasses (final JDefinedClass klass,
                                    final TypeElement element,
                                    final TypeEnvironment environment) throws CodeModelBuildingException
  {
    for (final Element enclosedElement : element.getEnclosedElements ())
    {
      if (enclosedElement.getKind ().equals (ElementKind.INTERFACE) ||
          enclosedElement.getKind ().equals (ElementKind.CLASS))
      {
        final EClassType classType = toClassType (enclosedElement.getKind ());
        int modifiers = toJMod (enclosedElement.getModifiers ());
        if (classType.equals (EClassType.INTERFACE))
        {
          modifiers &= ~JMod.ABSTRACT;
          modifiers &= ~JMod.STATIC;
        }
        JDefinedClass enclosedClass;
        try
        {
          enclosedClass = klass._class (modifiers, enclosedElement.getSimpleName ().toString (), classType);
        }
        catch (final JClassAlreadyExistsException ex)
        {
          throw new CodeModelBuildingException (ex);
        }
        declareInnerClasses (enclosedClass, (TypeElement) enclosedElement, environment);
      }
    }
  }

  void defineInnerClass (final JDefinedClass enclosingClass,
                         final TypeElement element,
                         final TypeEnvironment environment) throws CodeModelBuildingException, ErrorTypeFound
  {
    for (final JDefinedClass innerClass : enclosingClass.classes ())
    {
      if (innerClass.fullName ().equals (element.getQualifiedName ().toString ()))
      {
        final ClassFiller filler = new ClassFiller (_codeModel, this, innerClass);
        filler.fillClass (element, environment);
        return;
      }
    }
    throw new IllegalStateException (MessageFormat.format ("Inner class should always be defined if outer class is defined: inner class {0}, enclosing class {1}",
                                                           element,
                                                           enclosingClass));
  }

  AbstractJClass ref (final TypeElement element) throws CodeModelBuildingException, ErrorTypeFound
  {
    try
    {
      final Class <?> klass = Class.forName (element.getQualifiedName ().toString ());
      final AbstractJType declaredClass = _codeModel.ref (klass);
      return (AbstractJClass) declaredClass;
    }
    catch (final ClassNotFoundException ex)
    {
      return getClass (element);
    }
  }

  AbstractJType toJType (final TypeMirror type, final TypeEnvironment environment) throws CodeModelBuildingException,
                                                                                   ErrorTypeFound
  {
    try
    {
      return type.accept (new TypeMirrorToJTypeVisitor (_codeModel, this, _errorTypePolicy, environment), null);
    }
    catch (final RuntimeErrorTypeFound ex)
    {
      throw ex.getCause ();
    }
    catch (final RuntimeCodeModelBuildingException ex)
    {
      throw ex.getCause ();
    }
  }

  Map <? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults (final AnnotationMirror annotation)
  {
    return _elementUtils.getElementValuesWithDefaults (annotation);
  }
}
