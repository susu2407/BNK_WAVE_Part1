package kr.co.wave.controller.admin.config.terms;

import kr.co.wave.dto.config.TermsDTO;
import kr.co.wave.dto.config.TermsRepositoryDTO;
import kr.co.wave.dto.config.TermsWithInfoDTO;
import kr.co.wave.service.approval.TermsApprovalService;
import kr.co.wave.service.config.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
public class AdminTermsController {

    private final TermsService termsService;
    private final TermsApprovalService termsApprovalService;

    // 약관관리
    @GetMapping("/admin/config/terms/list")
    public String adminConfigPolicy(@RequestParam(required = false) String searchType,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "0") int page,
                                    Model model) {

        Page<TermsWithInfoDTO> termsList = termsService.getTermsAllBySearch(searchType, keyword, page, 10);

        model.addAttribute("termsList", termsList);

        return "admin/config/terms/list";
    }

    // 약관 등록으로 이동
    @GetMapping("/admin/config/terms/register")
    public String adminConfigRegister() {
        return "admin/config/terms/register";
    }

    // 약관 등록
    @PostMapping("/admin/config/terms/register")
    public String adminConfigRegister(TermsDTO termsDTO) {

        // DB에 저장
        termsService.registerTerms(termsDTO);

        return "redirect:/admin/config/terms/register";
    }

    // PDF 다운로드
    @GetMapping("/terms/download/{id}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable int id) {
        return termsService.fileDownload(id);
    }

    // PDF 새창 열기
    @GetMapping("/terms/pdfView/{id}")
    public ResponseEntity<FileSystemResource> viewPdf(@PathVariable int id) {
       return termsService.filePreview(id);
    }

    // 활성화
    @GetMapping("/admin/config/terms/activate")
    public String cardActivate(@RequestParam String termsId) {

        termsService.activateTerms(Integer.parseInt(termsId));

        return "redirect:/admin/config/terms/list";
    }

    // 비활성화
    @PostMapping("/admin/config/terms/inactivate")
    public String termsInactivate(@RequestParam String termsId, @RequestParam String reason) {

        termsService.inactivateTerms(Integer.parseInt(termsId), reason);

        return "redirect:/admin/config/terms/list";
    }

    // 약관 업데이트 이동
    @GetMapping("/admin/config/terms/update")
    public String updateTerms(@RequestParam String termsId, Model model){

        model.addAttribute("termsId", termsId);
        model.addAttribute("warningList", termsService.getTitles());
        model.addAttribute("terms", termsService.getTermsById(Integer.parseInt(termsId)));
        return "admin/config/terms/update";
    }

    // 약관 업데이트
    @PostMapping("/admin/config/terms/update")
    public String updateTerms(TermsDTO termsDTO){

        termsService.updateTerms(termsDTO);

        return "redirect:/admin/config/terms/list";
    }

}
