package home.serg.newsserviceimpl.rss;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "rss")
public class DefaultSources {
    Map<String, String> sources;
}
