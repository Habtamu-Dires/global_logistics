package com.yotor.global_logistics.tracking.application;

import com.yotor.global_logistics.tracking.application.dto.TrackingRecordRequest;
import com.yotor.global_logistics.tracking.application.dto.TrackingResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tracking")
@Tag(name = "tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping
    public ResponseEntity<?> recordLocation(TrackingRecordRequest req){
        trackingService.recordLocation(req);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{assignment-id}/latest")
    public TrackingResponse getLatest(
            @PathVariable("assignment-id") UUID assignmentId
    ) {
        return trackingService.getLatest(assignmentId);
    }


    @GetMapping("/{assignment-id}")
    public List<TrackingResponse> getRoute(
            @PathVariable("assignment-id") UUID assignmentExternalId
    ) {
        return trackingService.getRoute(assignmentExternalId);
    }


}
