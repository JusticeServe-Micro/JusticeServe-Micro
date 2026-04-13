package com.justiceserve.identityservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "compliance-service")
public interface AuditFeignClient {
    @PostMapping("/api/audit-logs/internal")
    void log(@RequestParam("userId") Long userId,
             @RequestParam("action") String action,
             @RequestParam("resource") String resource);
}

//package com.justiceserve.identityservice.feign;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(name = "compliance-service", fallback = AuditFeignClient.AuditFeignFallback.class)
//public interface AuditFeignClient {
//    @PostMapping("/api/audit-logs/internal")
//    void log(@RequestParam("userId") Long userId,
//             @RequestParam("action") String action,
//             @RequestParam("resource") String resource);
//
//    @Component
//    class AuditFeignFallback implements AuditFeignClient {
//        @Override
//        public void log(Long userId, String action, String resource) {
//        }
//    }
//}
