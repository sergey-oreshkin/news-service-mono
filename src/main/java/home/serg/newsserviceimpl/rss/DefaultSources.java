package home.serg.newsserviceimpl.rss;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

/**
 * DTO for mapping RssSources from file, properties etc.
 */
@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "rss")
public class DefaultSources {
    Map<String, String> sources;
}
