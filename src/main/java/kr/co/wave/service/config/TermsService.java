package kr.co.wave.service.config;


import jakarta.transaction.Transactional;
import kr.co.wave.dto.approval.TermsApprovalDTO;
import kr.co.wave.dto.config.TermsDTO;
import kr.co.wave.dto.config.TermsRepositoryDTO;
import kr.co.wave.dto.config.TermsWarningDTO;
import kr.co.wave.dto.config.TermsWithInfoDTO;
import kr.co.wave.entity.approval.TermsApproval;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    @Transactional
    public void registerTerms(TermsDTO termsDTO) {
        String originalName = termsDTO.getPdfFile().getOriginalFilename(); // 파일 원래 이름
        String pdfPath = fileUploadUtil.saveFile(termsDTO.getPdfFile(), "terms"); // 저장할 상대 경로 반환 , terms = 저장할 폴더 이름

        Terms terms = Terms.builder().
                title(termsDTO.getTitle()).
                category(termsDTO.getCategory()).
                content(termsDTO.getContent()).
                isRequired(termsDTO.isRequired()).
                pdfFile(pdfPath).
                originalName(originalName).
                termStatus("비활성").
                version("0.0.1").
                build();

        termsRepository.save(terms);
    }

    // 다운로드
    @Transactional
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

    @Transactional
    public Terms getTermsById(int termsId) {
        Optional<Terms> optionalTerms = termsRepository.findById(termsId);

        if (optionalTerms.isPresent()) {
            Terms terms = optionalTerms.get();
            return terms;
        }

        return null;
    }

    // 약관 활성화
    @Transactional
    public void activateTerms(int termsId){
        Optional<Terms> optionalTerms = termsRepository.findById(termsId);

        if(optionalTerms.isPresent()){
            Terms terms = optionalTerms.get();
            terms.toggleStatus("활성");
        }
    }

    // 약관 비활성화 요청
    @Transactional
    public void inactivateTerms(int termsId, String reason){

        TermsApproval termsApproval = TermsApproval.builder().
                termsId(termsId).
                reason(reason).
                status("대기").
                build();

        termsApprovalRepository.save(termsApproval);
    }

    @Transactional
    public List<TermsWarningDTO> getTitles(){
        return termsRepository.findAllTitles();
    }

    @Transactional
    public void updateTerms(TermsDTO termsDTO) {
        Terms terms = termsRepository.findById(termsDTO.getTermsId())
                .orElseThrow(() -> new RuntimeException("Terms not found"));

        // 기존 파일 이름을 가져옴
        String originalName = null;
        String pdfPath = null;

        boolean isFileChanged = false;
        boolean isContentChanged = false;

        // 파일이 존재할 경우에만 파일 저장 및 수정
        if (termsDTO.getPdfFile() != null && !termsDTO.getPdfFile().isEmpty()) {
            originalName = termsDTO.getPdfFile().getOriginalFilename(); // 파일 원래 이름
            pdfPath = fileUploadUtil.saveFile(termsDTO.getPdfFile(), "terms"); // 저장할 상대 경로 반환, terms = 저장할 폴더 이름
            
            isFileChanged = true;
        } else {
            // 파일이 없을 경우 기존 파일 경로 유지 (변경 없음)
            Optional<Terms> originFile = termsRepository.findById(termsDTO.getTermsId());
            if (originFile.isPresent()) {
                TermsRepositoryDTO originFileDTO = modelMapper.map(originFile, TermsRepositoryDTO.class);
                originalName = originFileDTO.getOriginalName();
                pdfPath = originFileDTO.getPdfFile();
            }
        }

        // 내용이 같지 않으면
        if (!termsDTO.getContent().equals(terms.getContent()) || !termsDTO.getCategory().equals(terms.getCategory())) {
            isContentChanged = true;
        }

        // 버전 정보 업데이트:
        if (isFileChanged && isContentChanged) {
            // 파일 변경과 내용 수정 둘 다 발생한 경우 (1.0.1 증가)
            termsDTO.setVersion(incrementVersionForFileAndContentChange(termsDTO.getVersion()));
        } else if (isFileChanged) {
            // 파일 변경만 발생한 경우 (1.0.0 증가)
            termsDTO.setVersion(incrementVersionForFileChange(termsDTO.getVersion()));
        } else if (isContentChanged) {
            // 내용 수정만 발생한 경우 (0.0.1 증가)
            termsDTO.setVersion(incrementVersionForUpdate(termsDTO.getVersion()));
        }

        // Terms 객체 빌드
        Terms updateTerms = Terms.builder()
                .termsId(terms.getTermsId())
                .category(termsDTO.getCategory())
                .title(termsDTO.getTitle())
                .content(termsDTO.getContent())
                .isRequired(termsDTO.isRequired())
                .pdfFile(pdfPath)
                .originalName(originalName)
                .createdAt(termsDTO.getCreatedAt())
                .updatedAt(LocalDate.now())  // 현재 시간으로 수정일 갱신
                .version(termsDTO.getVersion())
                .termStatus(termsDTO.getTermStatus())
                .build();

        termsRepository.save(updateTerms);
    }

    // 파일 변경 및 내용 수정이 모두 일어난 경우 버전 증가 로직
    private String incrementVersionForFileAndContentChange(String currentVersion) {
        String[] versionParts = currentVersion.split("\\.");
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);

        // 파일 변경은 major 버전 증가, 내용 수정은 patch 버전 증가
        major++;  // 파일 변경 시 major 버전 증가
        patch++;  // 내용 수정 시 patch 버전 증가

        return major + "." + minor + "." + patch;
    }

    // 수정 시, 버전 마지막 숫자만 증가
    private String incrementVersionForUpdate(String currentVersion) {
        String[] versionParts = currentVersion.split("\\.");
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);

        // 패치 버전만 증가
        patch++;

        return major + "." + minor + "." + patch;
    }

    // 파일 변경 시, 첫 번째 숫자 증가
    private String incrementVersionForFileChange(String currentVersion) {
        String[] versionParts = currentVersion.split("\\.");
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);

        // 첫 번째 숫자 증가하고, 나머지는 0으로 설정
        major++;
        minor = 0;
        patch = 0;

        return major + ".0.0";
    }
}
