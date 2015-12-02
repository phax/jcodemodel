#!/usr/bin/env python
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
# Portions Copyright 2013-2015 Philip Helger + contributors
#
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common Development
# and Distribution License("CDDL") (collectively, the "License").  You
# may not use this file except in compliance with the License.  You can
# obtain a copy of the License at
# https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
# or packager/legal/LICENSE.txt.  See the License for the specific
# language governing permissions and limitations under the License.
#
# When distributing the software, include this License Header Notice in each
# file and include the License file at packager/legal/LICENSE.txt.
#
# GPL Classpath Exception:
# Oracle designates this particular file as subject to the "Classpath"
# exception as provided by Oracle in the GPL Version 2 section of the License
# file that accompanied this code.
#
# Modifications:
# If applicable, add the following below the License Header, with the fields
# enclosed by brackets [] replaced by your own identifying information:
# "Portions Copyright [year] [name of copyright owner]"
#
# Contributor(s):
# If you wish your version of this file to be governed by only the CDDL or
# only the GPL Version 2, indicate your decision by adding "[Contributor]
# elects to include this software in this distribution under the [CDDL or GPL
# Version 2] license."  If you don't indicate a single choice of license, a
# recipient has the option to distribute your version of this file under
# either the CDDL, the GPL Version 2 or to extend the choice of license to
# its licensees as provided above.  However, if you add GPL Version 2 code
# and therefore, elected the GPL Version 2 license, then the option applies
# only if the new code is made subject to such option by the copyright
# holder.
#

import sys
import os
import os.path
import xml.dom.minidom

# Source and credits:
# https://gist.github.com/neothemachine/4060735

if os.environ["TRAVIS_SECURE_ENV_VARS"] == "false":
  print "no secure env vars available, skipping deployment"
  sys.exit()

homedir = os.path.expanduser("~")

m2 = xml.dom.minidom.parse(homedir + '/.m2/settings.xml')
settings = m2.getElementsByTagName("settings")[0]

serversNodes = settings.getElementsByTagName("servers")
if not serversNodes:
  serversNode = m2.createElement("servers")
  settings.appendChild(serversNode)
else:
  serversNode = serversNodes[0]
  
sonatypeServerNode = m2.createElement("server")

sonatypeServerId = m2.createElement("id")
# See the name "ossrh" in the ph-parent-pom project
# Original name was "sonatype-nexus-snapshots"
idNode = m2.createTextNode("ossrh")
sonatypeServerId.appendChild(idNode)
sonatypeServerNode.appendChild(sonatypeServerId)

sonatypeServerUser = m2.createElement("username")
userNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
sonatypeServerUser.appendChild(userNode)
sonatypeServerNode.appendChild(sonatypeServerUser)

sonatypeServerPass = m2.createElement("password")
passNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])
sonatypeServerPass.appendChild(passNode)
sonatypeServerNode.appendChild(sonatypeServerPass)

serversNode.appendChild(sonatypeServerNode)
  
m2Str = m2.toxml()
f = open(homedir + '/.m2/snapshot-settings.xml', 'w')
f.write(m2Str)
f.close()
