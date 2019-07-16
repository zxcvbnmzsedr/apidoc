/**
 * Copyright © 2017 - 2020 Cnabc. All Rights Reserved.
 */

package com.ztianzeng.apidoc.maven;

import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.info.Info;
import com.ztianzeng.apidoc.utils.Json;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author tianzeng
 * @goal apidoc
 * @requiresDependencyResolution runtime
 */
@Mojo(name = "openapi", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DocMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;
    /**
     * @parameter expression="${src}" default-value="src"
     */
    @Parameter(property = "src", defaultValue = "src")
    private String src;

    @Parameter(property = "title", defaultValue = "doc")
    private String title;

    @Parameter(property = "version", defaultValue = "1.0")
    private String version;
    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;
    /**
     * The list of resources we want to transfer.
     */
    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;
    /**
     *
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;
    /**
     *
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;
    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;
    /**
     *
     */
    @Component(role = MavenResourcesFiltering.class, hint = "default")
    protected MavenResourcesFiltering mavenResourcesFiltering;

    @Override
    public void execute() {

        try {
            // 加载classloader
            Set<URL> urls = new HashSet<>();
            List<String> elements = mavenProject.getRuntimeClasspathElements();
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }
            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(contextClassLoader);


        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        getLog().info("扫描地址 " + src);
        execute(src);
    }

    public void execute(String url) {

        SourceBuilder sourceBuilder = new SourceBuilder(url);

        Reader reader = new Reader(new OpenAPI(), sourceBuilder);

        Set<JavaClass> controllerData;
        controllerData = sourceBuilder.getControllerData();


        OpenAPI openAPI = reader.read(controllerData);
        Info info = new Info();
        info.title(title);
        info.setVersion(version);

        openAPI.setInfo(info);

        Json.prettyPrint(openAPI);

        try {
            Json.pretty(outputDirectory.getPath() + "/" + title + ".json", openAPI);

            List<String> combinedFilters = Collections.emptyList();
            MavenResourcesExecution mavenResourcesExecution =
                    new MavenResourcesExecution(getResources(),
                            getOutputDirectory(), project, encoding, combinedFilters,
                            Collections.emptyList(), session);

            mavenResourcesFiltering.filterResources(mavenResourcesExecution);
        } catch (IOException | MavenFilteringException e) {
            e.printStackTrace();
        }


    }

    public List<Resource> getResources() {
        return resources;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public String getSrc() {
        return src;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

//    /**
//     * @return The combined filters.
//     */
//    protected List<String> getCombinedFiltersList() {
//        if (filters == null || filters.isEmpty()) {
//            return useBuildFilters ? buildFilters : null;
//        } else {
//            List<String> result = new ArrayList<String>();
//
//            if (useBuildFilters && buildFilters != null && !buildFilters.isEmpty()) {
//                result.addAll(buildFilters);
//            }
//
//            result.addAll(filters);
//
//            return result;
//        }
//    }
}