/// Typed expressions, designed in this package, improve the usage of JCM.
/// 
/// They limit the calls that can be made using designed variables, reducing potential bugs.
/// They also reduces the burden of writing the methods name, and checking the params. 
/// 
///  - primitives, Object and String are hardcoded to match the various java's operators (Object is needed for String)
///  - other classes are programmatically constructed to represent the matched class methods.  

package com.helger.jcodemodel.expressions.typed;
