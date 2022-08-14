package home.serg.newsserviceimpl.security;

import home.serg.newsserviceimpl.security.dto.RequestLogin;
import home.serg.newsserviceimpl.security.dto.ResponseLogin;
import home.serg.newsserviceimpl.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService service;

    @GetMapping("check")
    public ResponseEntity<Void> check() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid @RequestBody RequestLogin request) {
        service.register(request.getUsername(), request.getPassword());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<ResponseLogin> login(@Valid @RequestBody RequestLogin request) {
        return new ResponseEntity<>(service.getLogin(request.getUsername(), request.getPassword()), HttpStatus.OK);
    }

    @PostMapping("refresh")
    public ResponseEntity<ResponseLogin> refresh() {
        return new ResponseEntity<>(service.getRefresh(), HttpStatus.OK);
    }
}
