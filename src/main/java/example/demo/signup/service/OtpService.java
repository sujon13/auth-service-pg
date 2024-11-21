package example.demo.signup.service;

import example.demo.service.RestApiService;
import example.demo.signup.enums.OtpValidation;
import example.demo.signup.model.EmailRequest;
import example.demo.signup.model.Otp;
import example.demo.signup.model.OtpRequest;
import example.demo.signup.model.OtpResponse;
import example.demo.signup.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OtpService {
    @Value("${email-service.url:localhost:8086}")
    private String emailServiceBaseUrl;

    private final OtpRepository otpRepository;
    private final RestApiService restApiService;

    private static final int MIN = 100000;
    private static final int MAX = 1000000;
    private static final int LIFE_TIME_IN_MINUTES = 3;

    private int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private Otp createOtp(final int userId) {
        int generatedOtp = getRandom(MIN, MAX);
        Otp otp = new Otp();
        otp.setUserId(userId);
        otp.setOtp(generatedOtp);
        otp.setExpireAt(LocalDateTime.now().plusMinutes(LIFE_TIME_IN_MINUTES));
        return otp;
    }

    private Otp createAndSaveOtp(final int userId) {
        Otp otp = createOtp(userId);
        otpRepository.save(otp);
        return otp;
    }

    private Otp updateExpirationTime(final Otp otp) {
        otp.setExpireAt(LocalDateTime.now().plusMinutes(LIFE_TIME_IN_MINUTES));
        return otp;
    }

    private Otp createOrUpdateOtp(final int userId) {
        return otpRepository.findByUserIdAndNotExpiredAndNotUsed(userId)
                .map(this::updateExpirationTime)
                .orElseGet(() -> createAndSaveOtp(userId));
    }

    private EmailRequest generateEmailRequest(final int otp, String recipient) {
        final String subject = "Live Exam One Time password (OTP)";
        final String bodyTemplate = """
                Dear User,<br><br>
                Your OTP (One Time Password) is <b> %d </b>. Use this OTP to complete the signup process.<br>
                It is valid for the next 3 minutes. Please do not share it with others.<br><br>
                Thank you.<br><br>
                Regards,<br>
                Live Exam IT team
                """;
        final String body = String.format(bodyTemplate, otp);
        return EmailRequest.builder()
                .recipient(recipient)
                .subject(subject)
                .body(body)
                .build();
    }

    private void sendOtp(final EmailRequest emailRequest) {
        final String emailServiceUrl = emailServiceBaseUrl + "/send-email";
        restApiService.postWithoutBody(emailServiceUrl, emailRequest);
    }

    private OtpResponse createOtpResponse(Otp otp) {
        return OtpResponse.builder()
                .id(otp.getId())
                .userId(otp.getUserId())
                .build();
    }

    public OtpResponse sendOtp(final int userId, final String recipient) {
        final Otp otp = createOrUpdateOtp(userId);
        final EmailRequest emailRequest = generateEmailRequest(otp.getOtp(), recipient);

        //virtualThreadExecutor.submit(() -> sendOtp(emailRequest));
        sendOtp(emailRequest);
        return createOtpResponse(otp);
    }

    private boolean isOtpExpired(Otp otp) {
        return otp.getExpireAt().isBefore(LocalDateTime.now());
    }

    private boolean matchOtp(Otp otp, OtpRequest otpRequest) {
        return otp.getOtp().equals(otpRequest.getOtp());
    }

    private boolean matchUserId(Otp otp, OtpRequest otpRequest) {
        return otp.getUserId().equals(otpRequest.getUserId());
    }

    public OtpValidation validateOtp(final OtpRequest otpRequest) {
        Optional<Otp> optionalOtp = otpRepository.findById(otpRequest.getId());
        if (optionalOtp.isEmpty()) {
            return OtpValidation.NOT_FOUND;
        }
        Otp otp = optionalOtp.get();

        if (isOtpExpired(otp)) {
            return OtpValidation.EXPIRED;
        }

        if (otp.isUsed()) {
            return OtpValidation.USED;
        }

        if (!matchUserId(otp, otpRequest)) {
           return OtpValidation.USER_NOT_MATCHED;
        }

        if (matchOtp(otp, otpRequest)) {
            return OtpValidation.MATCHED;
        } else {
            return OtpValidation.INVALID;
        }
    }

    public void makeOtpUsed(final int otpId) {
        Optional<Otp> optionalOtp = otpRepository.findById(otpId);
        optionalOtp.ifPresent(otp -> otp.setUsed(true));
    }
}
