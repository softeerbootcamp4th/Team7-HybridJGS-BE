package JGS.CasperEvent.global.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    private MockMultipartFile image;

    @InjectMocks
    S3Service s3Service;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(s3Service, "bucketName", "s3Bucket");
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 성공")
    void uploadTest_Success() throws MalformedURLException {
        //given
        URL url = new URL("http", "www.example.com", "/image.jpg");
        image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        given(amazonS3.getUrl(eq("s3Bucket"), anyString()))
                .willReturn(url);
        //when
        String imageUrl = s3Service.upload(image);

        //then
        assertThat(imageUrl).isEqualTo("http://www.example.com/image.jpg");
    }

}