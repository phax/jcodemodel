package com.helger.jcodemodel.plugin.maven;

import java.util.Map;

public interface CodeModelBuilder {

	void configure(Map<String, String> params);

	void build();

}
