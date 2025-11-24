package kr.co.wave.service.member;

import kr.co.wave.dto.MemberDTO;
import kr.co.wave.entity.member.Member;
import kr.co.wave.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    //로그인
    @Transactional
    public void register(MemberDTO dto) {
        if (memberRepository.existsByMemId(dto.getMemId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        Member m = Member.builder()
                .memId(dto.getMemId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role(dto.getRole() == null ? "GENERAL" : dto.getRole())
                .build();
        memberRepository.save(m);
    }

    @Transactional(readOnly = true)
    public Member login(String memId, String rawPassword) {
        return memberRepository.findByMemId(memId)
                .filter(m -> passwordEncoder.matches(rawPassword, m.getPassword()))
                .orElse(null);
    }

    // 회원가입
    @Transactional
    public MemberDTO signup(MemberDTO memberDTO) {
        String plain = memberDTO.getPassword();
        String encoded = passwordEncoder.encode(plain);

        Member member = Member.builder().
                        memId(memberDTO.getMemId()).
                        password(encoded).
                        name(memberDTO.getName()).
                        role(memberDTO.getRole()).
                        build();

        Member saved = memberRepository.save(member);
        return memberDTO;
    }

    // ID로 회원 찾기
    public MemberDTO getMemberById(String memId) {
        Optional<Member> optMember = memberRepository.findById(memId);
        return optMember.map(member -> modelMapper.map(member, MemberDTO.class)).orElse(null);
    }

    // 모든 회원 가져오기
    public List<MemberDTO> getMemberAll() {
        List<Member> memberList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (Member member : memberList) {
            memberDTOList.add(modelMapper.map(member, MemberDTO.class));
        }
        return memberDTOList;
    }

    // 관리자
    public Page<Member> getAdminMemberAll(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        return memberRepository.findAdminAll(st, kw, pageable);
    }

}
