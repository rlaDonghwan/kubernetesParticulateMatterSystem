package inhatc.k8sProject.fineDust.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

/**
 * 애플리케이션의 전반적인 구성을 정의하는 클래스입니다.
 * 스프링의 의존성 주입(DI) 컨테이너에 사용되는 빈들을 설정합니다.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate 빈을 생성하고, 스프링의 DI 컨테이너에 등록합니다.
     * 이 메소드는 HTTP 요청을 보낼 때 "Accept: application/json" 헤더를 자동으로 추가하여
     * JSON 응답 형식을 명확히 요청합니다.
     *
     * @return 생성된 RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 모든 요청에 "Accept: application/json" 헤더를 추가하기 위한 인터셉터 설정
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}
