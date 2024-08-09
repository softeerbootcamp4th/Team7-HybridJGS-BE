package JGS.CasperEvent.global.jwt.service;

import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.dto.UserLoginDto;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public BaseUser verifyUser(UserLoginDto userLoginDto) {
        return userRepository.findById(userLoginDto.getPhoneNumber()).orElseGet(
                () -> userRepository.save(new BaseUser(userLoginDto.getPhoneNumber(), Role.USER)));
    }
}
