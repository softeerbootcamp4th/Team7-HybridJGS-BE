package JGS.CasperEvent.global.jwt.service;

import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.dto.UserLoginDto;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저 식별 테스트 - 성공 (가입한 유저)")
    void verifyUserTest_Success() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("010-0000-0000");
        BaseUser user = new BaseUser("010-0000-0000", Role.USER);
        given(userRepository.findByPhoneNumber("010-0000-0000")).willReturn(Optional.of(user));

        //when
        BaseUser verifiedUser = userService.verifyUser(userLoginDto);

        //then
        assertThat(verifiedUser.getId()).isEqualTo("010-0000-0000");
        assertThat(verifiedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("유저 식별 테스트 - 성공 (가입하지 않은 유저)")
    void testName() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("010-0000-0000");
        BaseUser user = new BaseUser("010-0000-0000", Role.USER);
        given(userRepository.findByPhoneNumber("010-0000-0000")).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        //when
        BaseUser verifiedUser = userService.verifyUser(userLoginDto);

        //then
        assertThat(verifiedUser.getId()).isEqualTo("010-0000-0000");
        assertThat(verifiedUser.getRole()).isEqualTo(Role.USER);
    }

}