/*
 * Copyright 2005-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.gradle.conventions;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.CoreJavadocOptions;
import org.gradle.external.javadoc.MinimalJavadocOptions;

/**
 * Conventions for the {@link JavaBasePlugin}.
 *
 * @author Andy Wilkinson
 */
class JavaBasePluginConventions {

	static final String SPRING_MILESTONE_REPOSITORY_NAME = "Spring Milestones";

	static final String SPRING_SNAPSHOT_REPOSITORY_NAME = "Spring Snapshots";

	static final String SHIBBOLETH_RELEASES_REPOSITORY_NAME = "Shibboleth Releases";

	static final String RELEASE_TRAIN_MAVEN_REPOSITORY_URL = "RELEASE_TRAIN_MAVEN_REPOSITORY_URL";

	static final String RELEASE_TRAIN_MAVEN_REPOSITORY_USERNAME = "RELEASE_TRAIN_MAVEN_REPOSITORY_USERNAME";

	static final String RELEASE_TRAIN_MAVEN_REPOSITORY_PASSWORD = "RELEASE_TRAIN_MAVEN_REPOSITORY_PASSWORD";

	void apply(Project project) {
		configureRepositories(project);
		project.getTasks().withType(Javadoc.class).configureEach((javadoc) -> {
			MinimalJavadocOptions options = javadoc.getOptions();
			options.quiet();
			options.source(JavaPluginConventions.JAVA_BASELINE.getMajorVersion());
			if (options instanceof CoreJavadocOptions coreOptions) {
				coreOptions.addBooleanOption("Xdoclint:-missing", true);
			}
		});
	}

	private void configureRepositories(Project project) {
		project.getRepositories().mavenCentral();
		project.getRepositories().maven((repository) -> {
			repository.setName(SHIBBOLETH_RELEASES_REPOSITORY_NAME);
			repository.setUrl("https://build.shibboleth.net/nexus/content/repositories/releases");
			repository.content((content) -> {
				content.includeGroup("org.opensaml");
				content.includeGroup("net.shibboleth");
			});
		});
		if (System.getenv(RELEASE_TRAIN_MAVEN_REPOSITORY_URL) != null) {
			project.getRepositories().maven((repository) -> {
				repository.setName("Release Train");
				repository.setUrl(System.getenv(RELEASE_TRAIN_MAVEN_REPOSITORY_URL));
				repository.credentials((credentials) -> {
					credentials.setUsername(System.getenv(RELEASE_TRAIN_MAVEN_REPOSITORY_USERNAME));
					credentials.setPassword(System.getenv(RELEASE_TRAIN_MAVEN_REPOSITORY_PASSWORD));
				});
			});
		}

		String version = project.getVersion().toString();
		if (version.contains("-")) {
			project.getRepositories().maven((repository) -> {
				repository.setName(SPRING_MILESTONE_REPOSITORY_NAME);
				repository.setUrl("https://repo.spring.io/milestone");
			});
		}
		if (version.endsWith("-SNAPSHOT")) {
			project.getRepositories().maven((repository) -> {
				repository.setName(SPRING_SNAPSHOT_REPOSITORY_NAME);
				repository.setUrl("https://repo.spring.io/snapshot");
			});
		}
	}

}
