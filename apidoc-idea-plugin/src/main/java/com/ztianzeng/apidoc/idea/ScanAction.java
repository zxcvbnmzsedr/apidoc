package com.ztianzeng.apidoc.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.ModelConverters;
import com.ztianzeng.apidoc.Reader;
import com.ztianzeng.apidoc.SourceBuilder;
import com.ztianzeng.apidoc.models.OpenAPI;
import com.ztianzeng.apidoc.models.info.Info;
import com.ztianzeng.apidoc.utils.Json;

import java.io.IOException;
import java.util.Set;

/**
 * 扫描项目中的所有类
 *
 * @author zhaotianzeng
 * @version V1.0
 * @date 2019-06-16 14:55
 */
public class ScanAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);

        Project project = editor.getProject();
        SourceBuilder sourceBuilder = new SourceBuilder(project.getBasePath());
        Set<JavaClass> controllerData = sourceBuilder.getControllerData();
        Reader reader = new Reader(new OpenAPI());
        new ModelConverters();
        OpenAPI openAPI = reader.read(controllerData);
        Info info = new Info();
        info.title("dddd");
        info.setVersion("111.111");
        openAPI.setInfo(info);
        Json.prettyPrint(openAPI);
        try {
            Json.pretty("/Users/tianzeng/Downloads/aa.json", openAPI);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}