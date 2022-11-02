package home.serg.newsserviceimpl.rss;

import home.serg.newsserviceimpl.rss.dto.RssSourceDto;
import home.serg.newsserviceimpl.rss.service.RssMapper;
import home.serg.newsserviceimpl.rss.service.RssService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("rss")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RssController {

    private final RssService rssService;

    private final RssMapper rssMapper;

    @GetMapping
    public List<RssSourceDto> get(@AuthenticationPrincipal User user) {
        return rssMapper.toDto(rssService.getByUser(user));
    }

    @PostMapping
    public ResponseEntity<Void> post(@AuthenticationPrincipal User user, @Valid @RequestBody RssSourceDto sourceDto) {
        rssService.addSource(user, rssMapper.toEntity(sourceDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Void> patch(@AuthenticationPrincipal User user, @Valid @RequestBody RssSourceDto sourceDto) {
        rssService.changeActive(user, rssMapper.toEntity(sourceDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user,
                                       @PathVariable("title") String title) {
        rssService.delete(user, title);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
