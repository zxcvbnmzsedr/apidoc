package com.ztianzeng.apidoc.converter;

import com.ztianzeng.apidoc.models.media.Schema;

import java.util.Iterator;

/**
 * 模型解析
 *
 * @author tianzeng
 */
public interface ModelConverter {

    /**
     * 解析
     *
     * @param type
     * @param context
     * @param chain   the chain of model converters to try if this implementation cannot process
     * @return null if this ModelConverter cannot convert the given Type
     */
    Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain);
}
