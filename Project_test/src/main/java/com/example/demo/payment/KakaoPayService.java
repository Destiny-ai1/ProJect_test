package com.example.demo.payment;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.demo.exception.FailException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class KakaoPayService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayService.class);

    @Value("${kakao.api.secret.key}")
    private String DEVFBB94AC81E4224CE7E1416AEC9E05F575C3EF; // 카카오페이 관리자 키 (API 인증용)

    public KakaoPayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 카카오페이 결제 준비 메소드
    public String kakaoPayReady(PaymentDto.Create dto) {
        if (dto.getOrderNo() == null) {
            throw new IllegalArgumentException("주문 번호가 필요합니다.");
        }

        // 먼저 결제 정보를 DB에 저장
        Payment payment = new Payment(dto.getOrderNo(), dto.getUsername(), dto.getItemName(), dto.getTotalAmount(), null, "READY");
        int insertedRows = paymentDao.save(payment);
        if (insertedRows == 0) {
            logger.error("결제 정보 저장 실패: 주문 번호: {}", dto.getOrderNo());
            throw new FailException("결제 정보 저장에 실패했습니다. 주문 번호: " + dto.getOrderNo());
        }
        logger.info("결제 정보 저장 완료: 주문 번호: {}", dto.getOrderNo());

        String readyUrl = "https://open-api.kakaopay.com/online/v1/payment/ready";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + DEVFBB94AC81E4224CE7E1416AEC9E05F575C3EF); // 최신 SecretKey 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 요청 방식

        // 요청 데이터 설정
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", dto.getOrderNo().toString());
        params.put("partner_user_id", dto.getUsername());
        params.put("item_name", dto.getItemName());
        params.put("quantity", 1);
        params.put("total_amount", dto.getTotalAmount());
        params.put("tax_free_amount", 0);
        params.put("approval_url", "http://localhost:8083/payment/approve?orderNo=" + dto.getOrderNo());
        params.put("cancel_url", "http://localhost:8083/payment/cancel?orderNo=" + dto.getOrderNo());
        params.put("fail_url", "http://localhost:8083/payment/fail?orderNo=" + dto.getOrderNo());

        logger.info("Request Body: {}", params);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(readyUrl, request, String.class);

            logger.info("카카오페이 결제 준비 응답 상태 코드: {}", response.getStatusCode());
            logger.info("카카오페이 결제 준비 응답 바디: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.getBody()); // 오류가 뜨면 pom.xml에 JSON 추가
                String tid = jsonResponse.optString("tid", "");
                if (tid.isEmpty()) {
                    logger.warn("TID가 없으므로 기본값으로 설정합니다.");
                    tid = "DEFAULT_TID";
                }


                // TID 저장
                int updatedRows = paymentDao.updateTidByOrderNo(dto.getOrderNo(), tid);
                if (updatedRows == 0) {
                    logger.error("TID 업데이트 실패: 주문 번호: {}, TID: {}", dto.getOrderNo(), tid);
                    throw new FailException("TID 업데이트에 실패했습니다. 주문 번호: " + dto.getOrderNo());
                }

                logger.info("TID 저장 완료: 주문 번호: {}, TID: {}", dto.getOrderNo(), tid);
                return jsonResponse.getString("next_redirect_pc_url");
            } else {
                throw new FailException("결제 준비 요청 실패, 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("카카오페이 결제 준비 중 오류 발생: {}", e.getMessage(), e);
            throw new FailException("카카오페이 결제 준비 중 오류 발생: " + e.getMessage());
        }
    }

    // 카카오페이 결제 승인 메소드
    public void kakaoPayApprove(String pgToken, Long orderNo) {
    	
    	logger.info("결제 승인 요청 시작 - OrderNo: {}, PgToken: {}", orderNo, pgToken);
    	
    	if (orderNo == null || pgToken == null) {
            throw new IllegalArgumentException("orderNo 또는 pgToken이 누락되었습니다.");
        }
    	
        String approveUrl = "https://open-api.kakaopay.com/online/v1/payment/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + DEVFBB94AC81E4224CE7E1416AEC9E05F575C3EF);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 데이터 설정
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("tid", getTidByOrderNo(orderNo));
        params.put("partner_order_id", orderNo.toString());
        params.put("partner_user_id", getUserIdByOrderNo(orderNo));
        params.put("pg_token", pgToken);

        logger.info("Request Body: {}", params);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(approveUrl, request, String.class);

            logger.info("카카오페이 결제 승인 응답 상태 코드: {}", response.getStatusCode());
            logger.info("카카오페이 결제 승인 응답 바디: {}", response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new FailException("결제 승인 요청 실패");
            }
        } catch (Exception e) {
            logger.error("카카오페이 결제 승인 중 오류 발생: {}", e.getMessage(), e);
            throw new FailException("카카오페이 결제 승인 중 오류 발생: " + e.getMessage());
        }
    }
    
 // 결제 취소 메소드
    public void kakaoPayCancel(Long orderNo, int cancelAmount) {
        String cancelUrl = "https://open-api.kakaopay.com/online/v1/payment/cancel"; // 카카오페이 결제 취소 API URL

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + DEVFBB94AC81E4224CE7E1416AEC9E05F575C3EF); // 최신 SecretKey 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 요청 방식

        // TID와 기타 필요한 정보 조회
        String tid = getTidByOrderNo(orderNo);

        // 요청 데이터 설정
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME"); // 테스트용 가맹점 코드
        params.put("tid", tid); // 결제 준비 단계에서 받은 TID
        params.put("cancel_amount", cancelAmount); // 취소 금액
        params.put("cancel_tax_free_amount", 0); // 비과세 금액 (없으면 0)

        logger.info("Request Body: {}", params);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(cancelUrl, request, String.class);

            logger.info("카카오페이 결제 취소 응답 상태 코드: {}", response.getStatusCode());
            logger.info("카카오페이 결제 취소 응답 바디: {}", response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new FailException("결제 취소 요청 실패, 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("카카오페이 결제 취소 중 오류 발생: {}", e.getMessage(), e);
            throw new FailException("카카오페이 결제 취소 중 오류 발생: " + e.getMessage());
        }
    }


    // 기타 메소드 (취소, 조회 등 동일한 방식으로 수정)
    private String getTidByOrderNo(Long orderNo) {
        String tid = paymentDao.findTidByOrderNo(orderNo);
        if (tid == null) {
            throw new FailException("TID를 찾을 수 없습니다: 주문 번호 " + orderNo);
        }
        return tid;
    }

    private String getUserIdByOrderNo(Long orderNo) {
        String userId = paymentDao.findUserIdByOrderNo(orderNo);
        if (userId == null) {
            throw new FailException("사용자 ID를 찾을 수 없습니다: 주문 번호 " + orderNo);
        }
        return userId;
    }
}
