package home.serg.newsserviceimpl.search.dto;


import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.List;

@Value
public class SearchRequestDto {

    @Positive(message = "Hours mast be positive number")
    @Max(value = 720, message = "Hours must be less then 720")
    int hours;

    List<String> keywords;
}
