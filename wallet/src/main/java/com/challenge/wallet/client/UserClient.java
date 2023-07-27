package com.challenge.wallet.client;

import org.apache.catalina.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "users", url = "${USER_SERVICE_URL}")
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET, value = "/users/{id}")
    ResponseEntity<User> isUserValid(@PathVariable("id") UUID id);
}
