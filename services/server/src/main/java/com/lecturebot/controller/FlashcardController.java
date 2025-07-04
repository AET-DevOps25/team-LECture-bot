// package com.lecturebot.controller;
//
// import java.util.Optional;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.lecturebot.generated.api.GenAiApi;
// import com.lecturebot.generated.model.FlashcardRequest;
// import com.lecturebot.generated.model.FlashcardResponse;
// import com.lecturebot.service.genai.GenAiClient;
//
// @RestController
// public class FlashcardController implements GenAiApi {
// private final GenAiClient genAiClient;
//
// public FlashcardController(GenAiClient genAiClient) {
// this.genAiClient = genAiClient;
// }
//
// @Override
// public ResponseEntity<FlashcardResponse> generateFlashcards(@RequestBody
// FlashcardRequest flashcardRequest) {
// Optional<FlashcardResponse> response =
// genAiClient.generateFlashcards(flashcardRequest);
// return response.map(ResponseEntity::ok)
// .orElseGet(() -> ResponseEntity.status(500).body(null));
// }
// }
