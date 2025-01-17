package onde.there.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.image.service.AwsS3Service;
import onde.there.member.exception.MemberException;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.utils.MailService;
import onde.there.member.utils.RedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final onde.there.member.repository.MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisService<Member> memberRedisService;
    private final AwsS3Service awsS3Service;

    public boolean checkId(MemberDto.CheckIdRequest checkIdRequest) {
        return !memberRepository.existsById(checkIdRequest.getId());
    }

    public boolean checkEmail(MemberDto.CheckEmailRequest checkEmailRequest) {
        return !memberRepository.existsByEmail(checkEmailRequest.getEmail());
    }

    public boolean checkNickName(String nickName) {
        return !memberRepository.existsByNickName(nickName);
    }

    public Member sendSignupMail(MemberDto.SignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            MemberException memberException = new MemberException(MemberErrorCode.DUPLICATED_MEMBER_EMAIL);

            log.error("memberService.sendSignupMail Error");
            log.error("request => {}", signupRequest);
            log.error("exception => {}", memberException.toString());

            throw memberException;
        }

        if (memberRepository.existsById(signupRequest.getId())) {
            MemberException memberException = new MemberException(MemberErrorCode.DUPLICATED_MEMBER_ID);

            log.error("memberService.sendSignupMail Error");
            log.error("request => {}", signupRequest);
            log.error("exception => {}", memberException.toString());

            throw memberException;
        }

        String uuid = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        Member member = Member.from(signupRequest, encodedPassword);
        mailService.sendSignupMail(uuid, member);
        memberRedisService.set(uuid, member, 10, TimeUnit.MINUTES);
        return member;
    }

    @Transactional
    public Member registerMember(String key) {
        Member member = memberRedisService.get(key)
                .orElseThrow(() -> {
                    MemberException memberException = new MemberException(MemberErrorCode.SIGNUP_CONFIRM_TIMEOUT);
                    log.error("memberService.registerMember Error");
                    log.error("request key => {}", key);
                    log.error("exception => {}", memberException.toString());
                    return memberException;
                });

        memberRedisService.delete(key);
        member.setProfileImageUrl("https://onde-bucket.s3.ap-northeast-2.amazonaws.com/profile-image-default.png");
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public Member update(MultipartFile multipartFile, MemberDto.UpdateRequest updateRequest) {
        Member member = memberRepository.findById(updateRequest.getId())
                .orElseThrow(() -> {
                    MemberException memberException = new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
                    log.error("memberService.update Error");
                    log.error("request id => {}", updateRequest);
                    log.error("exception => {}", memberException.toString());
                    return memberException;
                });

        String profileUrl = parseUpdateProfileUrl(multipartFile, member);
        String encodedPassword = parseUpdatedEncodedPassword(updateRequest, member);
        member.update(updateRequest, encodedPassword, profileUrl);
        return member;
    }

    private String parseUpdatedEncodedPassword(MemberDto.UpdateRequest updateRequest, Member member) {
        return updateRequest.getPassword().equals("") ?
                member.getPassword() :
                passwordEncoder.encode(updateRequest.getPassword());
    }

    private String parseUpdateProfileUrl(MultipartFile multipartFile, Member member) {
        boolean condition = multipartFile == null || multipartFile.isEmpty();
        return condition ? member.getProfileImageUrl() : awsS3Service.uploadFiles(List.of(multipartFile)).get(0);
    }
}
