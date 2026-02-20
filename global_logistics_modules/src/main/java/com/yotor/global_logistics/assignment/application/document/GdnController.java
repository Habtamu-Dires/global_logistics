package com.yotor.global_logistics.assignment.application.document;

import com.yotor.global_logistics.assignment.application.assignment.dto.IdResponse;
import com.yotor.global_logistics.assignment.application.document.dto.CreateGdnRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/gdn")
@Tag(name = "gdn")
@RequiredArgsConstructor
public class GdnController {

    private final GdnService gdnService;

    @PostMapping("/{assignment-id}")
    public ResponseEntity<IdResponse> generateGdn(
            @PathVariable("assignment-id") UUID assignmentId,
            @RequestBody @Valid CreateGdnRequest req
    ){
        var res = gdnService.generate(assignmentId, req);

        return ResponseEntity.ok(new IdResponse(res));
    }
}
