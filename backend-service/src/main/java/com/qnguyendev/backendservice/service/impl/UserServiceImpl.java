package com.qnguyendev.backendservice.service.impl;

import com.qnguyendev.backendservice.common.UserStatus;
import com.qnguyendev.backendservice.dto.request.UserCreationRequest;
import com.qnguyendev.backendservice.dto.request.UserPasswordRequest;
import com.qnguyendev.backendservice.dto.request.UserUpdateRequest;
import com.qnguyendev.backendservice.dto.response.UserPageResponse;
import com.qnguyendev.backendservice.dto.response.UserResponse;
import com.qnguyendev.backendservice.entity.Address;
import com.qnguyendev.backendservice.entity.User;
import com.qnguyendev.backendservice.exeception.ResourceNotFoundException;
import com.qnguyendev.backendservice.repository.AddressRepository;
import com.qnguyendev.backendservice.repository.UserRepository;
import com.qnguyendev.backendservice.service.EmailService;
import com.qnguyendev.backendservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    AddressRepository addressRepository;
    PasswordEncoder passwordEncoder;
    EmailService emailService;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("findAll start");

        // Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // tencot:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        // Xu ly truong hop FE muon bat dau voi page = 1
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<User> entityPage = null;

        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeyword(keyword, pageable);
        } else {
            entityPage = userRepository.findAll(pageable);
        }

        return getUserPageResponse(page, size, entityPage);
    }

    @Override
    public UserResponse findById(Long id) {
        log.info("Find user by id: {}", id);

        User User = getUser(id);

        return UserResponse.builder()
                           .id(id)
                           .firstName(User.getFirstName())
                           .lastName(User.getLastName())
                           .gender(User.getGender())
                           .birthday(User.getDateOfBirth())
                           .username(User.getUsername())
                           .phone(User.getPhone())
                           .email(User.getEmail())
                           .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("Saving user: {}", req);
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setType(req.getType());
        user.setStatus(UserStatus.NONE);
        userRepository.save(user);
        log.info("Saved user: {}", user);

        if (user.getId() != null) {
            log.info("user id: {}", user.getId());
            List<Address> addresses = new ArrayList<>();
            req.getAddresses().forEach(a -> {
                Address address = new Address();
                address.setApartmentNumber(a.getApartmentNumber());
                address.setFloor(a.getFloor());
                address.setBuilding(a.getBuilding());
                address.setStreetNumber(a.getStreetNumber());
                address.setStreet(a.getStreet());
                address.setCity(a.getCity());
                address.setCountry(a.getCountry());
                address.setAddressType(a.getAddressType());
                address.setUser(user);
                addresses.add(address);
            });
            addressRepository.saveAll(addresses);
            log.info("Saved addresses: {}", addresses);
        }

        try {
            emailService.emailVerification(req.getEmail(), req.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}", req);

        // Get user by id
        User user = getUser(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());

        userRepository.save(user);
        log.info("Updated user: {}", user);

        // save address
        List<Address> addresses = new ArrayList<>();

        req.getAddresses().forEach(address -> {
            Address newAddress = addressRepository.findByUserIdAndAddressType(user.getId(), address.getAddressType());
            if (newAddress == null) {
                newAddress = new Address();
            }
            newAddress.setApartmentNumber(address.getApartmentNumber());
            newAddress.setFloor(address.getFloor());
            newAddress.setBuilding(address.getBuilding());
            newAddress.setStreetNumber(address.getStreetNumber());
            newAddress.setStreet(address.getStreet());
            newAddress.setCity(address.getCity());
            newAddress.setCountry(address.getCountry());
            newAddress.setAddressType(address.getAddressType());
            newAddress.setUser(user);

            addresses.add(newAddress);
        });

        // save addresses
        addressRepository.saveAll(addresses);
        log.info("Updated addresses: {}", addresses);
    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing password for user: {}", req);

        // Get user by id
        User user = getUser(req.getId());
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(user);
        log.info("Changed password for user: {}", user);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user: {}", id);

        // Get user by id
        User user = getUser(id);
        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);
        log.info("Deleted user id: {}", id);
    }

    /**
     * Get user by id
     *
     * @param id
     * @return
     */
    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Convert EserEntities to UserResponse
     *
     * @param page
     * @param size
     * @param userEntities
     * @return
     */
    private static UserPageResponse getUserPageResponse(int page, int size, Page<User> userEntities) {
        log.info("Convert User Entity Page");

        List<UserResponse> userList = userEntities.stream().map(entity -> UserResponse.builder()
                                                                                      .id(entity.getId())
                                                                                      .firstName(entity.getFirstName())
                                                                                      .lastName(entity.getLastName())
                                                                                      .gender(entity.getGender())
                                                                                      .birthday(entity.getDateOfBirth())
                                                                                      .username(entity.getUsername())
                                                                                      .phone(entity.getPhone())
                                                                                      .email(entity.getEmail())
                                                                                      .build()
        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userEntities.getTotalElements());
        response.setTotalPages(userEntities.getTotalPages());
        response.setUsers(userList);

        return response;
    }
}
