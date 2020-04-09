package com.waitfor.contentcenter.controller.content;

import com.waitfor.contentcenter.domain.dto.content.ShareAuditDTO;
import com.waitfor.contentcenter.domain.entity.content.Share;
import com.waitfor.contentcenter.service.content.ShareService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {
    private ShareService shareService;
    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, ShareAuditDTO auditDTO){
        // TODO 认证、 授权
        return this.shareService.auditById(id,auditDTO);
    }
}
