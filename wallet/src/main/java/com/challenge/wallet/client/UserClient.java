package com.challenge.wallet.client;

import com.challenge.wallet.dto.wallet.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "user", url = "http://localhost:8081")
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET, value = "/users/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable("id") UUID id);
}
