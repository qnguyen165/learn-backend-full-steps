package com.qnguyendev.backendservice.service;

import com.qnguyendev.backendservice.common.Gender;
import com.qnguyendev.backendservice.common.UserStatus;
import com.qnguyendev.backendservice.common.UserType;
import com.qnguyendev.backendservice.dto.request.AddressRequest;
import com.qnguyendev.backendservice.dto.request.UserCreationRequest;
import com.qnguyendev.backendservice.dto.request.UserPasswordRequest;
import com.qnguyendev.backendservice.dto.request.UserUpdateRequest;
import com.qnguyendev.backendservice.dto.response.UserPageResponse;
import com.qnguyendev.backendservice.dto.response.UserResponse;
import com.qnguyendev.backendservice.entity.User;
import com.qnguyendev.backendservice.repository.AddressRepository;
import com.qnguyendev.backendservice.repository.UserRepository;
import com.qnguyendev.backendservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock AddressRepository addressRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock EmailService emailService;

    private static User tayJava;
    private static User johnDoe;

    @BeforeAll
    static void beforeAll() {
        tayJava = new User();
        tayJava.setId(1L);
        tayJava.setFirstName("Tay");
        tayJava.setLastName("Java");
        tayJava.setGender(Gender.MALE);
        tayJava.setDateOfBirth(new Date());
        tayJava.setEmail("quoctay87@gmail.com");
        tayJava.setPhone("0975118228");
        tayJava.setUsername("tayjava");
        tayJava.setPassword("password");
        tayJava.setType(UserType.USER);
        tayJava.setStatus(UserStatus.ACTIVE);

        johnDoe = new User();
        johnDoe.setId(2L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setGender(Gender.FEMALE);
        johnDoe.setDateOfBirth(new Date());
        johnDoe.setEmail("johndoe@gmail.com");
        johnDoe.setPhone("0123456789");
        johnDoe.setUsername("johndoe");
        johnDoe.setPassword("password");
        johnDoe.setType(UserType.USER);
        johnDoe.setStatus(UserStatus.INACTIVE);
    }

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService);
    }

    @Test
    void testGetUserList_Success() {
        // Giả lập phương thức search của UserRepository
        Page<User> userPage = new PageImpl<>(List.of(tayJava, johnDoe));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.totalElements);
    }

    @Test
    void testSearchUser_Success() {
        // Giả lập phương thức search của UserRepository
        Page<User> userPage = new PageImpl<>(List.of(tayJava, johnDoe));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll("tay", null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.totalElements);
        assertEquals("tayjava", result.getUsers().get(0).getUsername());
    }

    @Test
    void testGetUserList_Empty() {
        // Giả lập hành vi của UserRepository
        Page<User> userPage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(0, result.getUsers().size());
    }

    @Test
    void testGetUserById_Success() {
        // Giả lập hành vi của UserRepository
        when(userRepository.findById(1L)).thenReturn(Optional.of(tayJava));

        // Gọi phương thức cần kiểm tra
        UserResponse result = userService.findById(1L);

        Assertions.assertNotNull(result);
        assertEquals("tayjava", result.getUsername());
    }

    @Test
    void testSave_Success() {
        // Giả lập hành vi của UserRepository
        when(userRepository.save(any(User.class))).thenReturn(tayJava);

        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setFirstName("Tay");
        userCreationRequest.setLastName("Java");
        userCreationRequest.setGender(Gender.MALE);
        userCreationRequest.setBirthday(new Date());
        userCreationRequest.setEmail("quoctay87@gmail.com");
        userCreationRequest.setPhone("0975118228");
        userCreationRequest.setUsername("tayjava");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("ApartmentNumber");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("StreetNumber");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);
        userCreationRequest.setAddresses(List.of(addressRequest));

        // Gọi phương thức cần kiểm tra
        long result = userService.save(userCreationRequest);

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    void testUpdate_Success() {
        Long userId = 2L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setGender(Gender.FEMALE);
        updatedUser.setDateOfBirth(new Date());
        updatedUser.setEmail("janesmith@gmail.com");
        updatedUser.setPhone("0123456789");
        updatedUser.setUsername("janesmith");
        updatedUser.setType(UserType.USER);
        updatedUser.setStatus(UserStatus.ACTIVE);

        // Giả lập hành vi của UserRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(johnDoe));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(userId);
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setGender(Gender.MALE);
        updateRequest.setBirthday(new Date());
        updateRequest.setEmail("janesmith@gmail.com");
        updateRequest.setPhone("0123456789");
        updateRequest.setUsername("janesmith");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("ApartmentNumber");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("StreetNumber");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);
        updateRequest.setAddresses(List.of(addressRequest));

        // Gọi phương thức cần kiểm tra
        userService.update(updateRequest);

        UserResponse result = userService.findById(userId);

        assertEquals("janesmith", result.getUsername());
        assertEquals("janesmith@gmail.com", result.getEmail());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testChangePassword_Success() {
        Long userId = 2L;

        UserPasswordRequest request = new UserPasswordRequest();
        request.setId(userId);
        request.setPassword("newPassword");
        request.setConfirmPassword("newPassword");

        // Giả lập hành vi của repository và password encoder
        when(userRepository.findById(userId)).thenReturn(Optional.of(johnDoe));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");

        // Gọi phương thức cần kiểm tra
        userService.changePassword(request);

        // Kiểm tra mật khẩu được mã hóa và lưu
        assertEquals("encodedNewPassword", johnDoe.getPassword());
        verify(userRepository, times(1)).save(johnDoe);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    void testDeleteUser_Success() {
        // Chuẩn bị dữ liệu
        Long userId = 1L;

        // Giả lập hành vi repository
        when(userRepository.findById(userId)).thenReturn(Optional.of(tayJava));

        // Gọi phương thức cần kiểm tra
        userService.delete(userId);

        // Kiểm tra kết quả
        assertEquals(UserStatus.INACTIVE, tayJava.getStatus());
        verify(userRepository, times(1)).save(tayJava);
    }
}