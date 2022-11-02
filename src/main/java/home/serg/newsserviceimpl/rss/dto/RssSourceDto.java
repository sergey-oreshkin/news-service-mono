package home.serg.newsserviceimpl.rss.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
public class RssSourceDto {

    @NotBlank
    String title;

    @Pattern(regexp = "^(http|https):\\/\\/[^ \"]+$")
    String link;

    Boolean isActive;
}
