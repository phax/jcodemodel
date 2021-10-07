/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2021 Philip Helger + contributors
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
package com.helger.jcodemodel.util;

import javax.annotation.Nullable;

import com.helger.jcodemodel.writer.JCMWriter;

/**
 * <p>
 * JCodeModel is used to create a model of the code. This model is then exported
 * from the java representation into files with
 * {@link JCMWriter#build(com.helger.jcodemodel.writer.AbstractCodeWriter, com.helger.jcodemodel.writer.AbstractCodeWriter)}.
 * In order to export the model as files (both .java class definitions, and
 * resources), it needs to be sure the targeted environment can accept the files
 * names. It therefore needs to enforce constraints, to warn the user when it
 * tries to create a file whose name is invalid for the targeted platform.
 * </p>
 * <p>
 * The target file system is set at the creation of the JCM, with by default the
 * local platform specs: If I am coding on windows, by default I need to respect
 * windows' naming specs, and if on linux, linux' naming specs.
 * </p>
 * <p>
 * It's important that I can change the target platform because, the files may
 * be written into a jar file, or on a FTP server, instead of local platform
 * files, or in memory.
 * </p>
 * <p>
 * It's unlikely to need to change the target after the creation of the first
 * class, and can lead to errors - hence throw an unsupportedoperationException
 * when changing the target of theJCM after class/directory is already created.
 * </p>
 *
 * @author guiguilechat
 * @author Philip Helger
 * @since 3.4.0
 */
public interface IFileSystemConvention
{
  /**
   * @return <code>true</code> if the represented file system is case sensitive
   *         (e.g. Linux), <code>false</code> if it case insensitive (e.g.
   *         Windows)
   */
  boolean isCaseSensistive ();

  /**
   * Check if the passed name is valid for a directory according to the
   * underlying specifications. The names passed in to this method may not
   * contain a path separator.
   *
   * @param sPath
   *        The directory name to check.
   * @return <code>true</code> if the directory name is valid,
   *         <code>false</code> if not
   */
  boolean isValidDirectoryName (@Nullable String sPath);

  /**
   * Check if the passed name is valid for a file according to the underlying
   * specifications.
   *
   * @param sPath
   *        The filename to check.
   * @return <code>true</code> if the filename is valid, <code>false</code> if
   *         not
   */
  boolean isValidFilename (@Nullable String sPath);
}
