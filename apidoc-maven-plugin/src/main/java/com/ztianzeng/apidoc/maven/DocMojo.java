/**
 * Copyright © 2017 - 2020 Cnabc. All Rights Reserved.
 */

package com.ztianzeng.apidoc.maven;

import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.maven.ssh.SSHConfig;
import com.ztianzeng.apidoc.maven.ssh.SSHCopy;
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
@Mojo(name = "openapi", defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DocMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    /**
     * 标题
     */
    @Parameter(property = "title", defaultValue = "doc")
    private String title;

    /**
     * 版本
     */
    @Parameter(property = "version", defaultValue = "1.0")
    private String version;

    /**
     * 输出的文件名称
     */
    @Parameter(property = "outFileName", defaultValue = "doc.json")
    private String outFileName;

    /**
     * 输出的文件名称
     */
    @Parameter(property = "toJar", defaultValue = "true")
    private Boolean toJar;
    /**
     * 指定scp目标地址
     */
    @Parameter(property = "ssh",defaultValue = "null")
    private SSHConfig ssh;

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

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
        getLog().info("扫描地址 " + mavenProject.getBasedir().getPath());
        execute(mavenProject.getBasedir().getPath());
    }

    private void execute(String url) {

        SourceBuilder sourceBuilder = new SourceBuilder(url);

        Reader reader = new Reader(new OpenAPI(), sourceBuilder);

        Set<JavaClass> controllerData;
        controllerData = sourceBuilder.getControllerData();


        OpenAPI open = reader.read(controllerData);
        Info info = new Info();
        info.title(title);
        info.setVersion(version);

        open.setInfo(info);


        try {

            String filePath = outputDirectory.getPath() + "/" + outFileName;
            File file = new File(filePath);
            Json.mapper().writeValue(file, open);
            getLog().info("target" + project.getBuild().getOutputDirectory());

            if (ssh != null) {
                getLog().info("输出到远程目录" + ssh);
                SSHCopy.put(ssh, file);
            }
            if (toJar) {
                getLog().info("打包到jar中");
                List<String> combinedFilters = Collections.emptyList();
                List<Resource> resources = new ArrayList<>();
                Resource resource = new Resource();
                resource.setIncludes(Collections.singletonList(outFileName));
                resource.setDirectory(project.getBuild().getOutputDirectory());
                resources.add(resource);

                MavenResourcesExecution mavenResourcesExecution =
                        new MavenResourcesExecution(resources,
                                getOutputDirectory(), project, encoding, combinedFilters,
                                Collections.emptyList(), session);
                mavenResourcesFiltering.filterResources(mavenResourcesExecution);
            }
        } catch (IOException | MavenFilteringException e) {
            e.printStackTrace();
        }


    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}