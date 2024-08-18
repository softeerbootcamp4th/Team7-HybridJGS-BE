package JGS.CasperEvent.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    private String bucketName;
    private MockMultipartFile image;

    @InjectMocks
    S3Service s3Service;

    @BeforeEach
    void setUp(){
        bucketName = "s3Bucket";
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 성공")
    void uploadTest_Success() throws MalformedURLException {
        //given
        URL url = new URL("http", "www.example.com", "/image.jpg");
        image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        given(amazonS3.getUrl(eq(bucketName), anyString()))
                .willReturn(url);
        //when
        String imageUrl = s3Service.upload(image);

        //then
        assertThat(imageUrl).isEqualTo("http://www.example.com/image.jpg");
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 실패 (이미지 비어있음)")
    void uploadTest_Failure_ImageEmpty() {
        //given
        image = new MockMultipartFile("image", "image.png", "png", new byte[0]);

        //when
        AmazonS3Exception amazonS3Exception = assertThrows(AmazonS3Exception.class, () ->
                s3Service.upload(image)
        );

        //then
        assertThat("파일이 유효하지 않습니다.").isEqualTo(amazonS3Exception.getErrorMessage());
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 실패 (원본 파일 확장자 없음)")
    void uploadTest_Failure_ImageExtensionEmpty() {
        //given
        image = new MockMultipartFile("image", "image", "png", "<<data>>".getBytes());

        //when
        AmazonS3Exception amazonS3Exception = assertThrows(AmazonS3Exception.class, () ->
                s3Service.upload(image)
        );

        //then
        assertThat("파일에 확장자가 존재하지 않습니다.").isEqualTo(amazonS3Exception.getErrorMessage());
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 실패 (지원하지 않는 확장자)")
    void uploadTest_Failure_ImageNameEmpty() {
        //given
        image = new MockMultipartFile("image", "image.html", "png", "<<data>>".getBytes());

        //when
        AmazonS3Exception amazonS3Exception = assertThrows(AmazonS3Exception.class, () ->
                s3Service.upload(image)
        );

        //then
        assertThat("유효하지 않은 확장자입니다.").isEqualTo(amazonS3Exception.getErrorMessage());
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 실패 (IOException 발생)")
    void uploadTest_Failure_IOException() throws IOException {
        //given
        image = spy(new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes()));
        doThrow(new IOException())
                .when(image).getInputStream();
        //when
        AmazonS3Exception amazonS3Exception = assertThrows(AmazonS3Exception.class, () ->
                s3Service.upload(image)
        );

        //then
        assertThat("io exception on image upload").isEqualTo(amazonS3Exception.getErrorMessage());
    }

    @Test
    @DisplayName("이미지 업로드 테스트 - 실패 (버킷 업로드 실패)")
    void uploadTest_Failure_BucketException() throws Exception {
        //given
        image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        doThrow(new RuntimeException()).when(amazonS3).putObject(any());

        //when
        AmazonS3Exception amazonS3Exception = assertThrows(AmazonS3Exception.class, () ->
                s3Service.upload(image)
        );

        //then
        assertThat("이미지 업로드에 실패했습니다.").isEqualTo(amazonS3Exception.getErrorMessage());
    }

}