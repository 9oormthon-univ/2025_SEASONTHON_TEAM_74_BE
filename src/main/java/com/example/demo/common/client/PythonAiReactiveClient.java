//package com.example.demo.common.client;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.MultipartBodyBuilder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class PythonAiReactiveClient {
//    private final WebClient webClient;
//
//    public Mono<ClientResponse> generateMergedVideoDynamic(
//            String storeInfo,
//            List<String> descList,
//            String ttsGender,
//            List<Path> imagePaths
//    ) {
//        MultipartBodyBuilder mb = new MultipartBodyBuilder();
//        mb.part("store_info", storeInfo);
//        mb.part("description", String.join(",", descList)); // <-- 단일 문자열 필드명은 "description"
//        mb.part("tts_gender", ttsGender);
//
//        for (Path p : imagePaths) {
//            FileSystemResource res = new FileSystemResource(p.toFile());
//            // 가능하면 실제 MIME으로 지정
//            MediaType ct = Optional.ofNullable(probe(p)).orElse(MediaType.APPLICATION_OCTET_STREAM);
//            mb.part("images", res) // <-- 파일 파트 이름은 반드시 "images" (복수)
//                    .filename(p.getFileName().toString())
//                    .contentType(ct);
//        }
//
//        return webClient.post()
//                .uri("/ai/generate-merged-video-dynamic")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(mb.build()))
//                .exchange();
//    }
//
//    private MediaType probe(Path p) {
//        try {
//            String t = Files.probeContentType(p);
//            if (t == null) return null;
//            return MediaType.parseMediaType(t);
//        } catch (Exception e) { return null; }
//    }
//
//    public Mono<ClientResponse> generateStoreIntro(String storeInfo, List<String> descList) {
//        MultipartBodyBuilder mb = new MultipartBodyBuilder();
//        mb.part("store_info", storeInfo);
//        // 파이썬은 descriptions를 단일 문자열로 받으니 줄바꿈/쉼표 등 원하는 포맷으로 합쳐 전달
//        mb.part("descriptions", String.join("\n", descList));
//
//        return webClient.post()
//                .uri("/ai/generate-store-intro")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(mb.build()))
//                .exchange();
//    }
//
//}
//
//
