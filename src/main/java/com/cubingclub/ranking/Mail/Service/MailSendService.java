package com.cubingclub.ranking.Mail.Service;

import com.cubingclub.ranking.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.ranking.Account.Service.AccountService;
import com.cubingclub.ranking.Mail.Entity.RandomNum;
import com.cubingclub.ranking.Mail.Repository.RandomNumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Transactional
public class MailSendService {
    private final JavaMailSender mailSender;
    private final RandomNumRepository randomNumRepository;
    private final AccountService accountService;
    private final SpringTemplateEngine springTemplateEngine;

    public boolean CheckAuthNum(String email,String authNum){
        if(randomNumRepository.findRandomNumByRandNum(authNum)==null){
            return false;
        }
        else if(!randomNumRepository.findRandomNumByRandNum(authNum).getExpire().isAfter((LocalDateTime.now()))){
            return false;
        }
        else return randomNumRepository.findRandomNumByRandNum(authNum).getEmail().equals(email);
    }

    @Scheduled(fixedDelay = 1000*60*60)
    public void clearAuthNum(){
        List<RandomNum> randList=randomNumRepository.findRandomNumByExpireBefore(LocalDateTime.now());
        for(RandomNum rand:randList) {
            randomNumRepository.deleteByEmail(rand.getEmail());
        }
    }

    public String makeTempPassword(){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom r=new SecureRandom();
        StringBuilder pw=new StringBuilder();
        for(int i=0; i<16; i++){
            int index=r.nextInt(chars.length());
            pw.append(chars.charAt(index));
        }
        return pw.toString();
    }



    //임의의 6자리 양수를 반환합니다.
    public String makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return randomNumber.toString();
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }

    //이메일을 전송합니다.
    public void verifyMailSend(String setFrom, String toMail, String title, String content, String authNumber) {
        mailSend(setFrom,toMail,title,content);
        if(randomNumRepository.findRandomNumByEmail(toMail)!=null){
            randomNumRepository.deleteByEmail(toMail);
        }
        randomNumRepository.save(RandomNum.builder().randNum(authNumber).email(toMail).expire(LocalDateTime.now().plusMinutes(5)).build());
    }

    public String buildPasswordEmailContent(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);

        // emailTemplate.html 파일을 템플릿으로 지정 (resources/templates/emailTemplate.html)
        return springTemplateEngine.process("reissue_password", context);
    }
    //Mail을 어디서 보내는지, 어디로 보내는지 , 인증 번호를 html 형식으로 어떻게 보내는지 작성합니다.
    public Integer passwordEmail(PwReissueRequestDTO pwReissueRequestDTO) {
        if(!CheckAuthNum(pwReissueRequestDTO.getEmail(), pwReissueRequestDTO.getAuthNum())){
            return -2;
        }
        String tempPassword=makeTempPassword();
        if(!accountService.updatePassword(pwReissueRequestDTO.getEmail(), tempPassword)){
            return -1;
        }
        String setFrom = "cubingclubkorea@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "Cubing Club Korea 임시 비밀번호 생성"; // 이메일 제목
        String content = buildPasswordEmailContent(tempPassword);
        mailSend(setFrom, pwReissueRequestDTO.getEmail(), title, content);
        return 0;
    }

    public String buildEmailAuthContent(String authNumber) {
        Context context = new Context();
        context.setVariable("authNumber", authNumber);
        return springTemplateEngine.process("verify", context);
    }
    public Integer joinEmail(String email) {
        if(accountService.checkEmailDuplicate(email)){
            return -1;
        }
        String authNumber=makeRandomNumber();
        while (randomNumRepository.findRandomNumByRandNum(authNumber)!=null){
            authNumber=makeRandomNumber();
        }
        String setFrom = "cubingclubkorea@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "Cubing Club Korea 인증요청"; // 이메일 제목
        String content = buildEmailAuthContent(authNumber);
        verifyMailSend(setFrom, email, title, content, authNumber);
        return 0;
    }
    public Integer verifyPassEmail(String email) {
        if(!accountService.checkEmailDuplicate(email)){
            return -1;
        }
        String authNumber=makeRandomNumber();
        while (randomNumRepository.findRandomNumByRandNum(authNumber)!=null){
            authNumber=makeRandomNumber();
        }
        String setFrom = "cubingclubkorea@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "Cubing Club Korea 인증요청"; // 이메일 제목
        String content = buildEmailAuthContent(authNumber);
        verifyMailSend(setFrom, email, title, content, authNumber);
        return 0;
    }
}
