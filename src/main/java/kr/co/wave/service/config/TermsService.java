package kr.co.wave.service.config;


import jakarta.transaction.Transactional;
import kr.co.wave.dto.approval.TermsApprovalDTO;
import kr.co.wave.dto.config.TermsDTO;
import kr.co.wave.dto.config.TermsRepositoryDTO;
import kr.co.wave.dto.config.TermsWithInfoDTO;
import kr.co.wave.entity.approval.CardApproval;
import kr.co.wave.entity.approval.TermsApproval;
import kr.co.wave.entity.card.Card;
import kr.co.wave.entity.config.Terms;
import kr.co.wave.repository.approval.TermsApprovalRepository;
import kr.co.wave.repository.config.TermsRepository;
import kr.co.wave.service.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermsService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final TermsRepository termsRepository;
    private final TermsApprovalRepository termsApprovalRepository;
    private final ModelMapper modelMapper;
    private final FileUploadUtil fileUploadUtil;

    @Transactional
    public Page<TermsWithInfoDTO> getTermsAllBySearch(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<TermsRepositoryDTO> termsPage = termsRepository.findTermsAllBySearch(st, kw, pageable);

        List<TermsWithInfoDTO> adminTermsList = new ArrayList<>();

        for (TermsRepositoryDTO termsDTO : termsPage.getContent()) {
            TermsWithInfoDTO dto = new TermsWithInfoDTO();

            dto.setTerm(termsDTO);

            TermsApprovalDTO termsApprovalDTO = termsApprovalRepository.findTermsApprovalPendingByTermsId(termsDTO.getTermsId());
            if(termsApprovalDTO != null){
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminTermsList.add(dto);
        }

        return new PageImpl<>(adminTermsList, pageable, termsPage.getTotalElements());
    }

    // 약관 등록
    public void registerTerms(TermsDTO termsDTO) {
        String originalName = termsDTO.getPdfFile().getOriginalFilename(); // 파일 원래 이름
        String pdfPath = fileUploadUtil.saveFile(termsDTO.getPdfFile(), "terms"); // 저장할 상대 경로 반환 , terms = 저장할 폴더 이름

        Terms terms = Terms.builder().
                title(termsDTO.getTitle()).
                content(termsDTO.getContent()).
                isRequired(termsDTO.isRequired()).
                pdfFile(pdfPath).
                originalName(originalName).
                build();

        termsRepository.save(terms);
    }

    // 다운로드
    public ResponseEntity<Resource> fileDownload(int termsId) {
        Terms term = termsRepository.findById(termsId).orElse(null);

        try {
            // 전체 경로 생성: uploadPath + term.getPdfFile()
            Path filePath = Paths.get(uploadPath, term.getPdfFile());

            System.out.println("전체 경로: " + filePath.toAbsolutePath());
            System.out.println("파일 존재: " + Files.exists(filePath));

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = filePath.getFileName().toString();

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath.toAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 새창
    public ResponseEntity<FileSystemResource> filePreview(int termsId) {

        // Terms 객체 가져오기
        Terms term = termsRepository.findById(termsId).orElse(null);

        // PDF 파일 경로가 null인 경우 예외 처리
        if (term == null || term.getPdfFile() == null) {
            return ResponseEntity.notFound().build();
        }

        // 파일 경로 생성: uploadPath와 term.getPdfFile() 결합
        Path filePath = Paths.get(uploadPath, term.getPdfFile());  // 경로 결합

        // PDF 파일 경로 객체
        File pdfFile = filePath.toFile();

        // 파일이 존재하는지 확인
        if (!pdfFile.exists()) {
            return ResponseEntity.status(404).body(new FileSystemResource("PDF 파일이 존재하지 않습니다"));
        }

        // PDF 파일을 inline으로 처리하도록 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + pdfFile.getName());

        // 응답 반환
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(pdfFile));

    }


    public void updateTerms(TermsDTO termsDTO) {

        termsDTO.setContent(termsDTO.getContent());
        termsDTO.setIsRequired(termsDTO.isRequired());
        termsDTO.setUpdatedAt(termsDTO.getUpdatedAt());

        termsRepository.save(modelMapper.map(termsDTO, Terms.class));
    }

    // 카드 활성화
    @Transactional
    public void activateCard(int termsId){
        Optional<Terms> optionalTerms = termsRepository.findById(termsId);

        if(optionalTerms.isPresent()){
            Terms terms = optionalTerms.get();
            terms.toggleStatus("활성");
        }
    }

    // 약관 비활성화 요청
    @Transactional
    public void inactivateCard(int termsId, String reason){

        TermsApproval termsApproval = TermsApproval.builder().
                termsId(termsId).
                reason(reason).
                status("대기").
                build();

        termsApprovalRepository.save(termsApproval);
    }

}
