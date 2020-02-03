package com.helger.jcodemodel.preprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
import com.helger.jcodemodel.JPackage;

/**
 * add a list of author entries to the javadoc of the first-level classes. This
 * makes tests on authors presence based on equality, so if an existing author
 * "John Theauthor" is already present, and "john theauthor" is to be added,
 * both will be present as authors.
 *
 * @author glelouet
 *
 */
public class AuthorProcessor extends AbstractJCodePreprocessor
{

  private final Set <String> authors = new LinkedHashSet <> ();

  /**
   * add a list of authors
   *
   * @param newAuthors
   *        the new authors String to add.
   */
  public void add (String... newAuthors)
  {
    if (newAuthors == null || newAuthors.length == 0)
      return;
    authors.addAll (Arrays.asList (newAuthors));
  }

  @Override
  public boolean apply (JCodeModel jcm, boolean firstPass)
  {
    if (authors.isEmpty ())
      return false;
    boolean modification = false;
    for (JPackage pck : jcm.getAllPackages ())
      for (JDefinedClass cl : pck.classes ())
        if (applyClass (cl))
          modification = true;
    return modification;
  }

  protected boolean applyClass (JDefinedClass cl)
  {
    Set <String> classAuthors = new LinkedHashSet <> ();
    if (cl.javadoc ().getTag (JDocComment.TAG_AUTHOR) != null)
      for (Object subpart : cl.javadoc ().getTag (JDocComment.TAG_AUTHOR))
        if (subpart instanceof String)
          addAuthors (classAuthors, (String) subpart);
    for (Object javadocPart : cl.javadoc ())
      if (javadocPart instanceof String)
      {
        String s_part = (String) javadocPart;
        if (s_part.startsWith ("@" + JDocComment.TAG_AUTHOR))
          addAuthors (classAuthors, s_part.substring (JDocComment.TAG_AUTHOR.length () + 1));
      }
    List <String> missingAuthors = new ArrayList <> (authors);
    missingAuthors.removeAll (classAuthors);
    for (String missingAuthor : missingAuthors)
      cl.javadoc ().append ("@" + JDocComment.TAG_AUTHOR + " " + missingAuthor + "\n");
    // TODO should be debug
    // System.err.println("class=" + cl.fullName() + " authors=" + classAuthors
    // + " missing=" + missingAuthors);
    return !missingAuthors.isEmpty ();
  }

  protected void addAuthors (Set <String> classAuthors, String authorPart)
  {
    for (String splitPart : authorPart.split ("[,;]"))
      classAuthors.add (splitPart.trim ());
  }

}
