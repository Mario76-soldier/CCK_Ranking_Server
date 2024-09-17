package CubingClubKorea.CCK_CompRank.Service;

import CubingClubKorea.CCK_CompRank.Entity.RandomNum;
import CubingClubKorea.CCK_CompRank.Repository.RandomNumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Transactional
public class MailSendService {
    private final JavaMailSender mailSender;
    private final RandomNumRepository randomNumRepository;

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

    //임의의 6자리 양수를 반환합니다.
    public String makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return randomNumber.toString();
    }


    //Mail을 어디서 보내는지, 어디로 보내는지 , 인증 번호를 html 형식으로 어떻게 보내는지 작성합니다.
    public String joinEmail(String email) {
        String authNumber=makeRandomNumber();
        while (randomNumRepository.findRandomNumByRandNum(authNumber)==null){
            authNumber=makeRandomNumber();
        }
        String setFrom = "2001cupid@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String title = "CCK CompRanking 회원 가입 인증요청"; // 이메일 제목
        String content =
                "큐빙클럽코리아 기록관에 오신 것을 환영합니다!" + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요"; //이메일 내용 삽입
        mailSend(setFrom, email, title, content, authNumber);
        return authNumber;
    }

    //이메일을 전송합니다.
    public void mailSend(String setFrom, String toMail, String title, String content, String authNumber) {
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
        if(randomNumRepository.findRandomNumByEmail(toMail)!=null){
            randomNumRepository.deleteByEmail(toMail);
        }
        randomNumRepository.save(RandomNum.builder().randNum(authNumber).email(toMail).expire(LocalDateTime.now().plusMinutes(5)).build());
    }
}
