package com.ztianzeng.apidoc.models.media;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The <code>PrimitiveType</code> enumeration defines a mapping of limited set
 * of classes into Swagger primitive types.
 */
public enum PrimitiveType {
    /**
     *
     */
    STRING(String.class, "string") {
        @Override
        public Schema createProperty() {
            return new StringSchema();
        }
    },
    BOOLEAN(Boolean.class, "boolean") {
        @Override
        public Schema createProperty() {
            return new BooleanSchema();
        }
    },
    BYTE(Byte.class, "byte") {
        @Override
        public ByteArraySchema createProperty() {
            return new ByteArraySchema();
        }
    },
    BINARY(Byte.class, "binary") {
        @Override
        public BinarySchema createProperty() {
            return new BinarySchema();
        }
    },
    URI(java.net.URI.class, "uri") {
        @Override
        public Schema createProperty() {
            return new StringSchema().format("uri");
        }
    },
    URL(java.net.URL.class, "url") {
        @Override
        public Schema createProperty() {
            return new StringSchema().format("url");
        }
    },
    EMAIL(String.class, "email") {
        @Override
        public Schema createProperty() {
            return new StringSchema().format("email");
        }
    },
    UUID(java.util.UUID.class, "uuid") {
        @Override
        public UUIDSchema createProperty() {
            return new UUIDSchema();
        }
    },
    INT(Integer.class, "integer") {
        @Override
        public IntegerSchema createProperty() {
            return new IntegerSchema();
        }
    },
    LONG(Long.class, "long") {
        @Override
        public Schema createProperty() {
            return new IntegerSchema().format("int64");
        }
    },
    FLOAT(Float.class, "float") {
        @Override
        public Schema createProperty() {
            return new NumberSchema().format("float");
        }
    },
    DOUBLE(Double.class, "double") {
        @Override
        public Schema createProperty() {
            return new NumberSchema().format("double");
        }
    },
    INTEGER(java.math.BigInteger.class) {
        @Override
        public Schema createProperty() {
            return new IntegerSchema().format(null);
        }
    },
    DECIMAL(java.math.BigDecimal.class, "number") {
        @Override
        public Schema createProperty() {
            return new NumberSchema();
        }
    },
    NUMBER(Number.class, "number") {
        @Override
        public Schema createProperty() {
            return new NumberSchema();
        }
    },
    DATE(DateStub.class, "date") {
        @Override
        public DateSchema createProperty() {
            return new DateSchema();
        }
    },
    DATE_TIME(java.util.Date.class, "date-time") {
        @Override
        public DateTimeSchema createProperty() {
            return new DateTimeSchema();
        }
    },
    PARTIAL_TIME(java.time.LocalTime.class, "partial-time") {
        @Override
        public Schema createProperty() {
            return new StringSchema().format("partial-time");
        }
    },
    FILE(java.io.File.class, "file") {
        @Override
        public FileSchema createProperty() {
            return new FileSchema();
        }
    },
    OBJECT(Object.class, "object") {
        @Override
        public Schema createProperty() {
            return new Schema().type("object");
        }
    };

    private static final Map<String, PrimitiveType> KEY_CLASSES;
    private static final Map<Class<?>, PrimitiveType> BASE_CLASSES;
    /**
     * Adds support of a small number of "well-known" types, specifically for
     * Joda lib.
     */
    private static final Map<String, PrimitiveType> EXTERNAL_CLASSES;

    /**
     * Allows to exclude specific classes from KEY_CLASSES mappings to primitive
     * Joda lib.
     */
    private static Set<String> customExcludedClasses = new ConcurrentHashMap<String, String>().newKeySet();

    /**
     * Adds support for custom mapping of classes to primitive types
     */
    private static Map<String, PrimitiveType> customClasses = new ConcurrentHashMap<String, PrimitiveType>();

    /**
     * class qualified names prefixes to be considered as "system" types
     */
    private static Set<String> systemPrefixes = new ConcurrentHashMap<String, Boolean>().newKeySet();
    /**
     * class qualified names NOT to be considered as "system" types
     */
    private static Set<String> nonSystemTypes = new ConcurrentHashMap<String, Boolean>().newKeySet();
    /**
     * package names NOT to be considered as "system" types
     */
    private static Set<String> nonSystemTypePackages = new ConcurrentHashMap<String, Boolean>().newKeySet();

    /**
     * Alternative names for primitive types that have to be supported for
     * backward compatibility.
     */
    private static final Map<String, PrimitiveType> NAMES;
    private final Class<?> keyClass;
    private final String commonName;

    public static final Map<String, String> datatypeMappings;

    static {
        systemPrefixes.add("java.");
        systemPrefixes.add("javax.");
        nonSystemTypes.add("java.time.LocalTime");

        final Map<String, String> dms = new HashMap<>();
        dms.put("integer_int32", "integer");
        dms.put("integer_", "integer");
        dms.put("integer_int64", "long");
        dms.put("number_", "number");
        dms.put("number_float", "float");
        dms.put("number_double", "double");
        dms.put("string_", "string");
        dms.put("string_byte", "byte");
        dms.put("string_email", "email");
        dms.put("string_binary", "binary");
        dms.put("string_uri", "uri");
        dms.put("string_url", "url");
        dms.put("string_uuid", "uuid");
        dms.put("string_date", "date");
        dms.put("string_date-time", "date-time");
        dms.put("string_partial-time", "partial-time");
        dms.put("string_password", "password");
        dms.put("boolean_", "boolean");
        dms.put("object_", "object");
        datatypeMappings = Collections.unmodifiableMap(dms);

        final Map<String, PrimitiveType> keyClasses = new HashMap<>();
        addKeys(keyClasses, BOOLEAN, Boolean.class.getName(), Boolean.TYPE.getName());
        addKeys(keyClasses, STRING, String.class.getName(), Character.class.getName(), Character.TYPE.getName());
        addKeys(keyClasses, BYTE, Byte.class.getName(), Byte.TYPE.getName());
        addKeys(keyClasses, URL, java.net.URL.class.getName());
        addKeys(keyClasses, URI, java.net.URI.class.getName());
        addKeys(keyClasses, UUID, java.util.UUID.class.getName());
        addKeys(keyClasses, INT, Integer.class.getName(), Integer.TYPE.getName(), Short.class.getName(), Short.TYPE.getName());
        addKeys(keyClasses, LONG, Long.class.getName(), Long.TYPE.getName());
        addKeys(keyClasses, FLOAT, Float.class.getName(), Float.TYPE.getName());
        addKeys(keyClasses, DOUBLE, Double.class.getName(), Double.TYPE.getName());
        addKeys(keyClasses, INTEGER, java.math.BigInteger.class.getName());
        addKeys(keyClasses, DECIMAL, java.math.BigDecimal.class.getName());
        addKeys(keyClasses, NUMBER, Number.class.getName());
        addKeys(keyClasses, DATE, DateStub.class.getName());
        addKeys(keyClasses, DATE_TIME, java.util.Date.class.getName());
        addKeys(keyClasses, DATE_TIME, Calendar.class.getName());
        addKeys(keyClasses, FILE, java.io.File.class.getName());
        addKeys(keyClasses, OBJECT, Object.class.getName());
        KEY_CLASSES = Collections.unmodifiableMap(keyClasses);

        final Map<Class<?>, PrimitiveType> baseClasses = new HashMap<Class<?>, PrimitiveType>();
        addKeys(baseClasses, DATE_TIME, java.util.Date.class, java.util.Calendar.class);
        BASE_CLASSES = Collections.unmodifiableMap(baseClasses);

        final Map<String, PrimitiveType> externalClasses = new HashMap<String, PrimitiveType>();
        addKeys(externalClasses, DATE, "org.joda.time.LocalDate", "java.time.LocalDate");
        addKeys(externalClasses, DATE_TIME, "java.time.LocalDateTime", "java.time.ZonedDateTime",
                "java.time.OffsetDateTime", "javax.xml.datatype.XMLGregorianCalendar", "org.joda.time.LocalDateTime",
                "org.joda.time.ReadableDateTime", "org.joda.time.DateTime");
        addKeys(externalClasses, LONG, "java.time.Instant");
        EXTERNAL_CLASSES = Collections.unmodifiableMap(externalClasses);

        final Map<String, PrimitiveType> names = new TreeMap<String, PrimitiveType>(String.CASE_INSENSITIVE_ORDER);
        for (PrimitiveType item : values()) {
            final String name = item.getCommonName();
            if (name != null) {
                addKeys(names, item, name);
            }
        }
        addKeys(names, INT, "int");
        addKeys(names, OBJECT, "object");
        NAMES = Collections.unmodifiableMap(names);
    }

    private PrimitiveType(Class<?> keyClass) {
        this(keyClass, null);
    }

    private PrimitiveType(Class<?> keyClass, String commonName) {
        this.keyClass = keyClass;
        this.commonName = commonName;
    }


    /**
     * Adds support for custom mapping of classes to primitive types
     *
     * @return Map of custom classes to primitive type
     * @since 2.0.6
     */
    public static Set<String> customExcludedClasses() {
        return customExcludedClasses;
    }

    /**
     * Adds support for custom mapping of classes to primitive types
     *
     * @return Map of custom classes to primitive type
     * @since 2.0.6
     */
    public static Map<String, PrimitiveType> customClasses() {
        return customClasses;
    }

    /**
     * class qualified names prefixes to be considered as "system" types
     *
     * @return Mutable set of class qualified names prefixes to be considered as "system" types
     * @since 2.0.6
     */
    public static Set<String> systemPrefixes() {
        return systemPrefixes;
    }

    /**
     * class qualified names NOT to be considered as "system" types
     *
     * @return Mutable set of class qualified names NOT to be considered as "system" types
     * @since 2.0.6
     */
    public static Set<String> nonSystemTypes() {
        return nonSystemTypes;
    }

    /**
     * package names NOT to be considered as "system" types
     *
     * @return Mutable set of package names NOT to be considered as "system" types
     * @since 2.0.6
     */
    public static Set<String> nonSystemTypePackages() {
        return nonSystemTypePackages;
    }

    public static PrimitiveType fromType(String className) {
        final PrimitiveType key = KEY_CLASSES.get(className);
        if (key != null) {
            if (!customExcludedClasses.contains(className)) {
                return key;
            }
        }

        final PrimitiveType custom = customClasses.get(className);
        if (custom != null) {
            return custom;
        }

        final PrimitiveType external = EXTERNAL_CLASSES.get(className);
        if (external != null) {
            return external;
        }

        return null;
    }

    public static PrimitiveType fromName(String name) {
        if (name == null) {
            return null;
        }
        PrimitiveType fromName = NAMES.get(name);
        if (fromName == null) {
            fromName = EXTERNAL_CLASSES.get(name);
        }
        return fromName;
    }

    public static PrimitiveType fromTypeAndFormat(String type, String format) {
        if (StringUtils.isEmpty(type) && type.equals("object")) {
            return null;
        }
        return fromName(datatypeMappings.get(String.format("%s_%s", StringUtils.isNotEmpty(type) ? "" : type, StringUtils.isEmpty(format) ? "" : format)));
    }


    public Class<?> getKeyClass() {
        return keyClass;
    }

    public String getCommonName() {
        return commonName;
    }

    public abstract Schema createProperty();

    private static <K> void addKeys(Map<K, PrimitiveType> map, PrimitiveType type, K... keys) {
        for (K key : keys) {
            map.put(key, type);
        }
    }

    private static class DateStub {
        private DateStub() {
        }
    }

    /**
     * Convenience method to map LocalTime to string primitive with rfc3339 format partial-time.
     * See https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14
     *
     * @since 2.0.6
     */
    public static void enablePartialTime() {
        customClasses().put("org.joda.time.LocalTime", PrimitiveType.PARTIAL_TIME);
        customClasses().put("java.time.LocalTime", PrimitiveType.PARTIAL_TIME);
    }
}
