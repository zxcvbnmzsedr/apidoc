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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @goal apidoc
 * @requiresDependencyResolution runtime
 */
@Mojo(name = "openapi", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DocMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject mavenProject;
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
            Json.pretty(outputDirectory.getPath() +"/"+ title + ".json", openAPI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}