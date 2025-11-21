package kr.co.wave.service.approval;

import jakarta.transaction.Transactional;
import kr.co.wave.dto.approval.TermsApprovalDTO;
import kr.co.wave.dto.config.TermsRepositoryDTO;
import kr.co.wave.dto.config.TermsWithInfoDTO;
import kr.co.wave.entity.approval.TermsApproval;
import kr.co.wave.entity.config.Terms;
import kr.co.wave.repository.approval.TermsApprovalRepository;
import kr.co.wave.repository.config.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermsApprovalService {

    private final TermsRepository termsRepository;
    private final TermsApprovalRepository termsApprovalRepository;
    private final ModelMapper modelMapper; // Entity와 DTO를 변환해주는 객체

    // 약관 결재 요청 전체 가져오기
    @Transactional
    public Page<TermsApprovalDTO> getTermsApprovalAllBySearch(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<TermsApprovalDTO> termsApprovalPage = termsApprovalRepository.findTermsApprovalAllBySearchAll(st, kw, pageable);

        return termsApprovalPage;
    }

    public Page<TermsWithInfoDTO> getTermsAllBySearch(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<TermsRepositoryDTO> termsPage = termsRepository.findTermsAllBySearch(searchType, keyword, pageable);

        // 결재 상태 붙이기
        List<TermsWithInfoDTO> adminTermsList = new ArrayList<>();

        for (TermsRepositoryDTO termsDTO : termsPage.getContent()) {
            TermsWithInfoDTO dto = new TermsWithInfoDTO();

            TermsApprovalDTO termsApprovalDTO = termsApprovalRepository.findTermsApprovalPendingByTermsId(termsDTO.getTermsId());
            if(termsApprovalDTO != null){
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminTermsList.add(dto);
        }

        // Page 객체로 다시 감싸서 반환 (검색결과, 페이지 수 그대로 유지)
        return new PageImpl<>(adminTermsList, pageable, termsPage.getTotalElements());
    }
    // 약관 비활성화 승인
    @Transactional
    public void approval(int termsApprovalId) {
        Optional<TermsApproval> OptionalTermsApproval = termsApprovalRepository.findById(termsApprovalId);

        if(OptionalTermsApproval.isPresent()){
            TermsApproval termsApproval = OptionalTermsApproval.get();
            termsApproval.toggleStatus("승인");

            Optional<Terms> OptionalTerm = termsRepository.findById(termsApproval.getTermsId());

            if(OptionalTerm.isPresent()){
                Terms term = OptionalTerm.get();
                term.toggleStatus("비활성");
            }
        }
    }

    // 카드 비활성화 반려
    @Transactional
    public void rejection(int termsApprovalId) {
        Optional<TermsApproval> OptionalTermsApproval = termsApprovalRepository.findById(termsApprovalId);

        if(OptionalTermsApproval.isPresent()){
            TermsApproval termsApproval = OptionalTermsApproval.get();
            termsApproval.toggleStatus("반려");

            Optional<Terms> OptionalTerm = termsRepository.findById(termsApproval.getTermsId());

            if(OptionalTerm.isPresent()){
                Terms term = OptionalTerm.get();
                term.toggleStatus("활성");
            }
        }
    }

    @Transactional
    public TermsApprovalDTO findTermsApprovalPendingByTermId(int termsId) {
        return termsApprovalRepository.findTermsApprovalPendingByTermsId(termsId);
    }
}
