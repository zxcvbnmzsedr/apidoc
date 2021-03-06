package com.ztianzeng.apidoc.converter;

import com.thoughtworks.qdox.model.JavaClass;
import com.ztianzeng.apidoc.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ModelConverterContextImpl implements ModelConverterContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConverterContextImpl.class);

    private final List<ModelConverter> converters;
    private final Map<String, Schema> modelByName;
    private final HashMap<AnnotatedType, Schema> modelByType;
    private final Set<AnnotatedType> processedTypes;

    public ModelConverterContextImpl(List<ModelConverter> converters) {
        this.converters = converters;
        modelByName = new TreeMap<>();
        modelByType = new HashMap<>();
        processedTypes = new HashSet<>();
    }

    public ModelConverterContextImpl(ModelConverter converter) {
        this(new ArrayList<ModelConverter>());
        converters.add(converter);
    }

    @Override
    public Iterator<ModelConverter> getConverters() {
        return converters.iterator();
    }

    @Override
    public void defineModel(String name, Schema model) {
        AnnotatedType aType = null;
        defineModel(name, model, aType, null);
    }

    @Override
    public void defineModel(String name, Schema model, JavaClass type, String prevName) {
        defineModel(name, model, new AnnotatedType().javaClass(type), prevName);
    }

    @Override
    public void defineModel(String name, Schema model, AnnotatedType type, String prevName) {
        modelByName.put(name, model);

        if (StringUtils.isNotBlank(prevName) && !prevName.equals(name)) {
            modelByName.remove(prevName);
        }

        if (type != null && type.getJavaClass() != null) {
            modelByType.put(type, model);
        }
    }

    @Override
    public Map<String, Schema> getDefinedModels() {
        return Collections.unmodifiableMap(modelByName);
    }

    @Override
    public Schema resolve(AnnotatedType type) {

        if (processedTypes.contains(type)) {
            return modelByType.get(type);
        } else {
            processedTypes.add(type);
        }

        Iterator<ModelConverter> converters = this.getConverters();
        Schema resolved = null;
        if (converters.hasNext()) {
            ModelConverter converter = converters.next();
            LOGGER.trace("trying extension " + converter);
            resolved = converter.resolve(type, this, converters);
        }
        if (resolved != null) {
            modelByType.put(type, resolved);

            Schema resolvedImpl = resolved;
            if (resolvedImpl.getName() != null) {
                modelByName.put(resolvedImpl.getName(), resolved);
            }
        } else {
            processedTypes.remove(type);
        }

        return resolved;
    }
}
