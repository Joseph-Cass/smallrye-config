package io.smallrye.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.junit.Assert;
import org.junit.Test;

public class ConfigValuePropertiesConfigSourceTest {
    @Test
    public void interceptor() throws Exception {
        SmallRyeConfig config = (SmallRyeConfig) buildConfig();

        Assert.assertEquals("1", config.getValue("my.prop", String.class));
        Assert.assertEquals("20", config.getValue("my.prop.20", String.class));
    }

    @Test
    public void configSourceMap() throws IOException {
        final ConfigValuePropertiesConfigSource configSource = new ConfigValuePropertiesConfigSource(
                ConfigValuePropertiesConfigSourceTest.class.getResource("/config-values.properties"));
        final Map<String, String> properties = configSource.getProperties();

        assertEquals("abc", properties.get("my.prop"));
        assertEquals("abc", properties.get("my.prop"));
        assertThrows(UnsupportedOperationException.class, () -> properties.remove("x"));
        assertThrows(UnsupportedOperationException.class, () -> properties.put("x", "x"));
        assertThrows(UnsupportedOperationException.class, () -> properties.putAll(new HashMap<>()));
        assertThrows(UnsupportedOperationException.class, properties::clear);
    }

    private static Config buildConfig() throws Exception {
        return new SmallRyeConfigBuilder()
                .addDefaultSources()
                .withSources(new ConfigValuePropertiesConfigSource(
                        ConfigValuePropertiesConfigSourceTest.class.getResource("/config-values.properties")))
                .withInterceptors((ConfigSourceInterceptor) (context, name) -> {
                    ConfigValue configValue = context.proceed(name);
                    // Return the line number instead for asssert
                    if (configValue != null) {
                        configValue = configValue.withValue(configValue.getLineNumber() + "");
                    }

                    return configValue;
                })
                .build();
    }
}
