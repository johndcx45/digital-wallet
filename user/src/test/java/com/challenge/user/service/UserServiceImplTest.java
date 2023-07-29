package com.challenge.user.service;

import com.challenge.user.domain.User;
import com.challenge.user.dto.CreateUserRequest;
import com.challenge.user.repository.UserRepository;
import com.challenge.user.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    private CreateUserRequest createUserRequest;
    private User firstUser;
    private User secondUser;
    private UUID firstUserId;
    private UUID secondUserId;

    @BeforeEach
    void setup() {
        this.userService = new UserServiceImpl(userRepository, kafkaTemplate);
        firstUserId = UUID.fromString("2a409788-1eb0-4ad9-bbc2-b512c25dce2f");
        secondUserId = UUID.fromString("80648b2f-394c-42db-b554-9324a9db9cf7");

        firstUser = User.builder()
                .userId(firstUserId)
                .firstName("First Name")
                .lastName("Last Name")
                .socialSecurityNumber("12345678900")
                .createdAt(new Date())
                .username("username-1")
                .build();
        secondUser = User.builder()
                .userId(secondUserId)
                .firstName("Name")
                .lastName("Last Name")
                .socialSecurityNumber("98765432100")
                .createdAt(new Date())
                .username("username-2")
                .build();

        createUserRequest = new CreateUserRequest(
                "username-1",
                "First Name",
                "Last Name",
                "12345678900"
        );
    }

    @Test
    void givenPageAndSizeParametersValid_whenFindWithPagination_thenReturnUsers() {
        Integer page = 0;
        Integer size = 2;
        Page<User> usersPage = new PageImpl<>(Arrays.asList(firstUser, secondUser));

        when(userRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(usersPage);

        List<User> response = userService.findWithPagination(page, size);

        verify(userRepository, times(1)).findAll(PageRequest.of(page, size));
        Assertions.assertThat(response.get(0).getUserId()).isEqualTo(firstUserId);
        Assertions.assertThat(response.get(1).getUserId()).isEqualTo(secondUserId);
    }

    @Test
    void givenPageAndSizeParametersValid_whenFindWithPagination_thenReturnNothing() {
        Integer page = 0;
        Integer size = 2;
        Page<User> usersPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(usersPage);

        List<User> response = userService.findWithPagination(page, size);

        verify(userRepository, times(1)).findAll(PageRequest.of(page, size));
        Assertions.assertThat(response).isEqualTo(null);
    }

    @Test
    void givenPageAndSizeParametersInvalid_whenFindWithPagination_thenThrowException() {
        Integer page = -1;
        Integer size = -1;

        Assertions.assertThatThrownBy(() -> {
            userService.findWithPagination(page, size);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidUserId_whenFindById_thenReturnUser() {
        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(firstUser));

        Optional<User> optional = userService.findById(firstUserId);

        verify(userRepository, times(1)).findById(firstUserId);
        Assertions.assertThat(optional.get().getUserId()).isEqualTo(firstUserId);
    }

    @Test
    void givenValidUser_whenCreateUser_thenReturnNewUser() {
        when(userRepository.save(any(User.class))).thenReturn(firstUser);

        User user = userService.createUser(createUserRequest);

        Assertions.assertThat(user.getUsername()).isEqualTo(createUserRequest.getUsername());
        Assertions.assertThat(user.getFirstName()).isEqualTo(createUserRequest.getFirstName());
        Assertions.assertThat(user.getLastName()).isEqualTo(createUserRequest.getLastName());
        Assertions.assertThat(user.getSocialSecurityNumber()).isEqualTo(createUserRequest.getSocialSecurityNumber());
    }

    @Test
    void givenValidWalletCreatedResponse_whenAssignWalletIdToRespectiveUser_thenReturnNothing() {
        String walletCreatedResponseMessage = "{\"userId\":\"2a409788-1eb0-4ad9-bbc2-b512c25dce2f\",\"walletId\":\"c406c32b-72c1-4c04-a10c-a60347fe0f47\"}";

        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(firstUser));
        when(userRepository.save(Mockito.any())).thenReturn(firstUser);

        userService.assignWalletIdToRespectiveUser(walletCreatedResponseMessage);

        verify(userRepository, times(1)).save(firstUser);
    }

    @Test
    void givenValidUserId_whenAssignWalletIdToRespectiveUser_thenThrowException() {
        String walletCreatedResponseMessage = "{\"userId\":\"\",\"walletId\":\"c406c32b-72c1-4c04-a10c-a60347fe0f47\"}";

        Assertions.assertThatThrownBy(() -> {
           userService.assignWalletIdToRespectiveUser(walletCreatedResponseMessage);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenInvalidUserId_whenAssignWalletIdToRespectiveUser_thenThrowException() {
        String walletCreatedResponseMessage = "{\"userId\":\"2a409788-1eb0-4ad9-bbc2-b512c25dce2f\",\"walletId\":\"c406c32b-72c1-4c04-a10c-a60347fe0f47\"}";

        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            userService.assignWalletIdToRespectiveUser(walletCreatedResponseMessage);
        }).isInstanceOf(RuntimeException.class);
    }
    @Test
    void givenValidUserIdWithInvalidWalletId_whenAssignWalletIdToRespectiveUser_thenThrowException() {
        String walletCreatedResponseMessage = "{\"userId\":\"2a409788-1eb0-4ad9-bbc2-b512c25dce2f\",\"walletId\":\"c406c32b-72c1-4c04-a10c-a60347fe0f47\"}";
        firstUser.setWalletId(UUID.fromString("36631429-3f24-49b6-9862-a00185849684"));

        when(userRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(firstUser));

        Assertions.assertThatThrownBy(() -> {
            userService.assignWalletIdToRespectiveUser(walletCreatedResponseMessage);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenValidId_whenDeleteById_thenDeleteById() {
        userService.deleteById(firstUserId);
        verify(userRepository, times(1)).deleteById(firstUserId);
    }
}