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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Set;

/**
 * @goal apidoc
 */
@Mojo(name = "openapi")
public class DocMojo extends AbstractMojo {

    /**
     * @parameter expression="${src}" default-value="src"
     */
    @Parameter(property = "src", defaultValue = "src")
    private String src;

    @Parameter(property = "title", defaultValue = "doc")
    private String title;

    @Parameter(property = "version", defaultValue = "1.0")
    private String version;


    @Override
    public void execute() {
        getLog().info("扫描地址 " + src);
        execute(src);
    }

    public void execute(String url) {
        SourceBuilder sourceBuilder = new SourceBuilder(url);

        Reader reader = new Reader(new OpenAPI(), sourceBuilder);

        Set<JavaClass> controllerData = null;
        controllerData = sourceBuilder.getControllerData();


        OpenAPI openAPI = reader.read(controllerData);
        Info info = new Info();
        info.title(title);
        info.setVersion(version);

        openAPI.setInfo(info);

        Json.prettyPrint(openAPI);
    }


}